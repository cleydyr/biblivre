package biblivre.digitalmedia.s3;

import biblivre.core.file.BiblivreFile;
import biblivre.digitalmedia.BaseDigitalMediaDAO;
import java.io.InputStream;
import java.util.Collections;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@ConditionalOnProperty(
        value = "DIGITAL_MEDIA_DAO_IMPL",
        havingValue = "biblivre.digitalmedia.s3.S3DigitalMediaDAO")
public class S3DigitalMediaDAO extends BaseDigitalMediaDAO {
    private final S3Client s3;
    private final String bucketName;

    private static final Logger logger = LoggerFactory.getLogger(S3DigitalMediaDAO.class);

    public S3DigitalMediaDAO() {
        this.s3 =
                S3Client.builder().credentialsProvider(DefaultCredentialsProvider.create()).build();

        this.bucketName = System.getenv("S3_BUCKET_NAME");
    }

    @Override
    protected void persist(InputStream is, long oid, long size) {

        logger.info(
                "Uploading file to S3, (oid: {}, size: {})",
                oid,
                FileUtils.byteCountToDisplaySize(size));

        PutObjectRequest request =
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(String.valueOf(oid))
                        .metadata(Collections.singletonMap("oid", String.valueOf(oid)))
                        .build();

        s3.putObject(request, RequestBody.fromInputStream(is, size));
    }

    @Override
    protected void deleteBlob(long oid) {
        DeleteObjectRequest request =
                DeleteObjectRequest.builder().bucket(bucketName).key(String.valueOf(oid)).build();

        s3.deleteObject(request);
    }

    @Override
    protected BiblivreFile getFile(long oid) {
        GetObjectRequest request =
                GetObjectRequest.builder().bucket(bucketName).key(String.valueOf(oid)).build();

        ResponseInputStream<GetObjectResponse> inputStream = s3.getObject(request);

        return new S3File(inputStream);
    }
}
