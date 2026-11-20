package biblivre.digitalmedia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import biblivre.AbstractContainerDatabaseTest;
import biblivre.TestDatasourceConfiguration;
import biblivre.core.SchemaThreadLocal;
import java.sql.Connection;
import java.sql.Statement;
import java.time.Duration;
import java.util.UUID;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@Import({TestDatasourceConfiguration.class})
@ActiveProfiles("test")
class PendingUploadDAOTest extends AbstractContainerDatabaseTest {
    @Autowired private PendingUploadDAO pendingUploadDAO;

    @Autowired private DigitalMediaDAO digitalMediaDAO;

    @Autowired private DataSource dataSource;

    @BeforeEach
    void setUp() {
        SchemaThreadLocal.setSchema("single");
    }

    @AfterEach
    void tearDown() {
        pendingUploadDAO.listExpired().forEach(upload -> pendingUploadDAO.delete(upload.id()));
        SchemaThreadLocal.remove();
    }

    @Test
    void complete_turnsPendingUploadIntoMediaOnce() throws Exception {
        PendingUpload upload = upload("direct.pdf");

        pendingUploadDAO.insert(upload, Duration.ofHours(1));

        assertEquals(upload, pendingUploadDAO.findActive(upload.id()).orElseThrow());

        int id = pendingUploadDAO.complete(upload).orElseThrow();

        StoredMedia media = digitalMediaDAO.find(id, "direct.pdf").orElseThrow();

        assertEquals(StorageLocation.key(upload.storageKey()), media.location());
        assertEquals("application/pdf", media.contentType());
        assertEquals(10, media.size());
        assertTrue(pendingUploadDAO.findActive(upload.id()).isEmpty());
        assertTrue(pendingUploadDAO.complete(upload).isEmpty());

        // The Postgres store under test can't delete an S3 key; drop the row directly.
        try (Connection con = dataSource.getConnection();
                Statement st = con.createStatement()) {
            st.execute("DELETE FROM digital_media WHERE id = " + id);
        }
    }

    @Test
    void expiredUpload_cannotCompleteAndIsListedForSweeping() {
        PendingUpload upload = upload("late.pdf");

        pendingUploadDAO.insert(upload, Duration.ZERO);

        assertTrue(pendingUploadDAO.findActive(upload.id()).isEmpty());
        assertTrue(pendingUploadDAO.complete(upload).isEmpty());
        assertTrue(pendingUploadDAO.listExpired().contains(upload));
        assertNull(digitalMediaDAO.load(0, "late.pdf"));
    }

    private static PendingUpload upload(String name) {
        return new PendingUpload(
                UUID.randomUUID(), "single/" + UUID.randomUUID(), name, "application/pdf", 10, 1);
    }
}
