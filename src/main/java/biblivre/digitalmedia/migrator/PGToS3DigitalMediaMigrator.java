package biblivre.digitalmedia.migrator;

import biblivre.core.SchemaThreadLocal;
import biblivre.core.exceptions.DAOException;
import biblivre.core.schemas.SchemaBO;
import biblivre.core.schemas.SchemaDTO;
import biblivre.digitalmedia.DigitalMediaDTO;
import biblivre.digitalmedia.postgres.DatabaseFile;
import biblivre.digitalmedia.postgres.PostgresLargeObjectDigitalMediaDAO;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@ConditionalOnProperty(name = "DIGITAL_MEDIA_MIGRATOR")
@Slf4j
public class PGToS3DigitalMediaMigrator implements DigitalMediaStoreMigrator {
    private S3Client s3;
    private String bucketName;
    private SchemaBO schemaBO;

    private PostgresLargeObjectDigitalMediaDAO digitalMediaDAO;

    @Override
    public void init() {
        this.s3 =
                S3Client.builder().credentialsProvider(DefaultCredentialsProvider.create()).build();

        this.bucketName = System.getenv("S3_BUCKET_NAME");
    }

    @Override
    public String from() {
        return "PostgreSQL large object";
    }

    @Override
    public String to() {
        return "AWS S3";
    }

    @Override
    public void migrate() {
        Set<SchemaDTO> schemas = schemaBO.getSchemas();

        for (SchemaDTO schema : schemas) {
            _doMigrate(schema.getSchema());
        }
    }

    private void _doMigrate(String schemaName) {
        log.info("Migrating {}.", schemaName);

        SchemaThreadLocal.withSchema(
                schemaName,
                () -> {
                    List<DigitalMediaDTO> list = digitalMediaDAO.list();

                    for (DigitalMediaDTO media : list) {
                        try {
                            DatabaseFile databaseFile =
                                    (DatabaseFile)
                                            digitalMediaDAO.load(media.getId(), media.getName());

                            if (databaseFile == null) {
                                log.warn(
                                        "Could not load {}, (id: {})",
                                        media.getName(),
                                        media.getId());

                                continue;
                            }

                            log.info(
                                    "Uploading {}, (id: {}, size: {})",
                                    media.getId(),
                                    media.getName(),
                                    FileUtils.byteCountToDisplaySize(databaseFile.getSize()));

                            _uploadToS3(databaseFile);

                            log.info("Removing {}, (id: {})", media.getName(), media.getId());

                            _delete(databaseFile);

                            _cleanUp(databaseFile);
                        } catch (SQLException e) {
                            throw new DAOException(e);
                        }
                    }
                });
    }

    private void _cleanUp(DatabaseFile databaseFile) throws SQLException {
        Connection connection = databaseFile.getConnection();

        connection.commit();

        connection.close();
    }

    private void _delete(DatabaseFile databaseFile) throws SQLException {
        long oid = databaseFile.getLargeObject().getLongOID();

        Connection connection = databaseFile.getConnection();

        PGConnection con = connection.unwrap(PGConnection.class);

        LargeObjectManager largeObjectAPI = con.getLargeObjectAPI();

        largeObjectAPI.delete(oid);
    }

    private void _uploadToS3(DatabaseFile databaseFile) throws SQLException {
        LargeObject largeObject = databaseFile.getLargeObject();

        long oid = largeObject.getLongOID();

        PutObjectRequest request =
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(String.valueOf(oid))
                        .contentType(databaseFile.getContentType())
                        .contentLength(databaseFile.getSize())
                        .build();

        InputStream inputStream = largeObject.getInputStream();

        s3.putObject(request, RequestBody.fromInputStream(inputStream, databaseFile.getSize()));
    }

    @Autowired
    public void setSchemaBO(SchemaBO schemaBO) {
        this.schemaBO = schemaBO;
    }

    @Autowired
    public void setDigitalMediaDAO(PostgresLargeObjectDigitalMediaDAO digitalMediaDAO) {
        this.digitalMediaDAO = digitalMediaDAO;
    }
}
