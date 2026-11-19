package biblivre.digitalmedia.s3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import biblivre.digitalmedia.DirectMediaStore;
import biblivre.digitalmedia.StorageLocation;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/** Signing is local HMAC, so these run without AWS. */
class S3DigitalMediaDAOPresignTest {
    private S3Presigner presigner;
    private S3DigitalMediaDAO dao;

    @BeforeEach
    void setUp() {
        presigner =
                S3Presigner.builder()
                        .region(Region.US_EAST_1)
                        .endpointOverride(URI.create("https://media.example.org"))
                        .serviceConfiguration(
                                S3Configuration.builder().pathStyleAccessEnabled(true).build())
                        .credentialsProvider(
                                StaticCredentialsProvider.create(
                                        AwsBasicCredentials.create("AKIDEXAMPLE", "secret")))
                        .build();

        dao = new S3DigitalMediaDAO(mock(S3Client.class), presigner, "biblivre");
    }

    @AfterEach
    void tearDown() {
        presigner.close();
    }

    @Test
    void newStorageKey_isScopedBySchema() {
        String key = dao.newStorageKey("single");

        assertTrue(key.matches("single/[0-9a-f-]{36}"), key);
    }

    @Test
    void presignDownload_carriesTypeAndDispositionOnThePublicEndpoint() {
        URI url =
                dao.presignDownload(
                        StorageLocation.key("single/abc"),
                        "application/pdf",
                        "attachment;filename=\"book.pdf\"");

        String query = URLDecoder.decode(url.getRawQuery(), StandardCharsets.UTF_8);

        assertEquals("media.example.org", url.getHost());
        assertEquals("/biblivre/single/abc", url.getPath());
        assertTrue(query.contains("response-content-type=application/pdf"), query);
        assertTrue(
                query.contains("response-content-disposition=attachment;filename=\"book.pdf\""),
                query);
        assertTrue(
                query.contains("X-Amz-Expires=" + DirectMediaStore.DOWNLOAD_URL_TTL.toSeconds()),
                query);
    }

    @Test
    void presignDownload_ofLegacyObjectUsesTheLargeObjectId() {
        URI url =
                dao.presignDownload(
                        new StorageLocation(4242L, "4242"), "image/png", "inline;filename=\"a\"");

        assertEquals("/biblivre/4242", url.getPath());
    }

    @Test
    void presignUpload_isBoundToTypeAndLength() {
        DirectMediaStore.PresignedUpload upload =
                dao.presignUpload("single/abc", "application/pdf", 1234);

        String query = URLDecoder.decode(upload.url().getRawQuery(), StandardCharsets.UTF_8);

        assertEquals("/biblivre/single/abc", upload.url().getPath());
        assertTrue(query.contains("X-Amz-SignedHeaders="), query);
        assertTrue(query.contains("content-type"), query);
        assertTrue(query.contains("content-length"), query);
        assertTrue(
                query.contains("X-Amz-Expires=" + DirectMediaStore.UPLOAD_URL_TTL.toSeconds()),
                query);
    }
}
