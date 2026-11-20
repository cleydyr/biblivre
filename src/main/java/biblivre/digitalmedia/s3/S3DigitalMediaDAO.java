package biblivre.digitalmedia.s3;

import biblivre.core.file.BiblivreFile;
import biblivre.digitalmedia.BaseDigitalMediaDAO;
import biblivre.digitalmedia.DirectMediaStore;
import biblivre.digitalmedia.StorageLocation;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@ConditionalOnProperty(
        value = "DIGITAL_MEDIA_DAO_IMPL",
        havingValue = "biblivre.digitalmedia.s3.S3DigitalMediaDAO")
@Slf4j
public class S3DigitalMediaDAO extends BaseDigitalMediaDAO implements DirectMediaStore {
    private final S3Client s3;
    private final S3Presigner presigner;
    private final String bucketName;

    public S3DigitalMediaDAO() {
        this(
                S3Client.builder().credentialsProvider(DefaultCredentialsProvider.create()).build(),
                publicPresigner(),
                System.getenv("S3_BUCKET_NAME"));
    }

    public S3DigitalMediaDAO(S3Client s3, S3Presigner presigner, String bucketName) {
        this.s3 = s3;
        this.presigner = presigner;
        this.bucketName = bucketName;
    }

    /**
     * The signature covers the host, so the presigner must use the endpoint the browser reaches,
     * which may differ from the one the server talks to.
     */
    private static S3Presigner publicPresigner() {
        S3Presigner.Builder builder =
                S3Presigner.builder().credentialsProvider(DefaultCredentialsProvider.create());

        String endpoint = System.getenv("S3_PUBLIC_ENDPOINT");

        if (StringUtils.isNotBlank(endpoint)) {
            builder.endpointOverride(URI.create(endpoint));
        }

        String region = System.getenv("S3_PUBLIC_REGION");

        if (StringUtils.isNotBlank(region)) {
            builder.region(Region.of(region));
        }

        if (Boolean.parseBoolean(System.getenv("S3_PATH_STYLE_ACCESS"))) {
            builder.serviceConfiguration(
                    S3Configuration.builder().pathStyleAccessEnabled(true).build());
        }

        return builder.build();
    }

    @Override
    protected StorageLocation allocate(String schema) {
        return StorageLocation.key(newStorageKey(schema));
    }

    @Override
    public String newStorageKey(String schema) {
        return schema + "/" + UUID.randomUUID();
    }

    @Override
    protected void persist(InputStream is, StorageLocation location, long size) {
        log.info(
                "Uploading file to S3, (key: {}, size: {})",
                location.storageKey(),
                FileUtils.byteCountToDisplaySize(size));

        PutObjectRequest request =
                PutObjectRequest.builder().bucket(bucketName).key(location.storageKey()).build();

        s3.putObject(request, RequestBody.fromInputStream(is, size));
    }

    @Override
    protected void deleteBlob(StorageLocation location) {
        deleteObject(location.storageKey());
    }

    @Override
    public void deleteObject(String storageKey) {
        DeleteObjectRequest request =
                DeleteObjectRequest.builder().bucket(bucketName).key(storageKey).build();

        s3.deleteObject(request);
    }

    @Override
    protected BiblivreFile getFile(StorageLocation location) {
        GetObjectRequest request =
                GetObjectRequest.builder().bucket(bucketName).key(location.storageKey()).build();

        ResponseInputStream<GetObjectResponse> inputStream = s3.getObject(request);

        return new S3File(inputStream);
    }

    @Override
    public URI presignDownload(
            StorageLocation location, String contentType, String contentDisposition) {
        GetObjectRequest request =
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(location.storageKey())
                        .responseContentType(contentType)
                        .responseContentDisposition(contentDisposition)
                        .responseCacheControl("private, no-store")
                        .build();

        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(DOWNLOAD_URL_TTL)
                        .getObjectRequest(request)
                        .build();

        return toUri(presigner.presignGetObject(presignRequest).url());
    }

    @Override
    public PresignedUpload presignUpload(String storageKey, String contentType, long size) {
        PutObjectRequest request =
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(storageKey)
                        .contentType(contentType)
                        .contentLength(size)
                        .build();

        PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(UPLOAD_URL_TTL)
                        .putObjectRequest(request)
                        .build();

        PresignedPutObjectRequest presigned = presigner.presignPutObject(presignRequest);

        return new PresignedUpload(toUri(presigned.url()), presigned.expiration());
    }

    @Override
    public Optional<StoredObject> head(String storageKey) {
        HeadObjectRequest request =
                HeadObjectRequest.builder().bucket(bucketName).key(storageKey).build();

        try {
            HeadObjectResponse response = s3.headObject(request);

            return Optional.of(new StoredObject(response.contentLength(), response.contentType()));
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return Optional.empty();
            }

            throw e;
        }
    }

    private static URI toUri(URL url) {
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Presigner returned an invalid URL", e);
        }
    }
}
