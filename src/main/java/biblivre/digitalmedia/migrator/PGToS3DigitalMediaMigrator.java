package biblivre.digitalmedia.migrator;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;

import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;
import biblivre.core.file.DatabaseFile;
import biblivre.core.schemas.Schemas;
import biblivre.digitalmedia.PGDigitalMediaDAO;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class PGToS3DigitalMediaMigrator extends AbstractDAO implements DigitalMediaStoreMigrator {
	private final S3Client s3;
	private final String bucketName;

	public PGToS3DigitalMediaMigrator() {
		this.s3 = S3Client.builder()
			.credentialsProvider(DefaultCredentialsProvider.create())
			.build();

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
		Schemas.getSchemas().stream()
			.map(schema -> schema.getSchema())
			.map(schemaName -> (PGDigitalMediaDAO) AbstractDAO.getInstance(
				PGDigitalMediaDAO.class, schemaName))
			.forEach(pgDigitalMediaDAO -> {
				pgDigitalMediaDAO.list().stream()
				.forEach(media -> {
					try {
						DatabaseFile databaseFile =
							(DatabaseFile) pgDigitalMediaDAO.load(
								media.getId(), media.getName());

						LargeObject largeObject = databaseFile.getLargeObject();

						long oid = largeObject.getLongOID();

						PutObjectRequest request = PutObjectRequest.builder()
								.bucket(bucketName)
								.key(String.valueOf(oid))
								.contentType(databaseFile.getContentType())
								.contentLength(databaseFile.getSize())
								.build();

						InputStream inputStream =
								largeObject.getInputStream();

						s3.putObject(
							request,
							RequestBody.fromInputStream(
								inputStream, databaseFile.getSize()));

						Connection connection = databaseFile.getConnection();

						PGConnection con = getPGConnection(connection);

						LargeObjectManager largeObjectAPI = con.getLargeObjectAPI();

						largeObjectAPI.delete(oid);

						databaseFile.close();
					}
					catch (SQLException e) {
						throw new DAOException(e);
					}
				});
			});




	}
}
