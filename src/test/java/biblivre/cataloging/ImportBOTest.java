package biblivre.cataloging;

import static org.junit.jupiter.api.Assertions.assertEquals;

import biblivre.AbstractContainerDatabaseTest;
import biblivre.TestDatasourceConfiguration;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Testcontainers
@Import({TestDatasourceConfiguration.class})
public class ImportBOTest extends AbstractContainerDatabaseTest {
    @Autowired ImportBO importBO;

    @Test
    void testImportBO() {
        ImportDTO importDTO =
                importBO.loadFromFile(() -> fromResource("cataloguing/import/biblivre-import.mrc"));

        assertEquals(importDTO.getFailure(), 0);

        assertEquals(importDTO.getSuccess(), 25);
    }

    @Test
    void testLoadFromFileWithXML() {
        ImportDTO importDTO =
                importBO.loadFromFile(
                        () -> fromResource("cataloguing/import/biblioteca-de-espanha.xml"));

        assertEquals(importDTO.getFailure(), 0);

        assertEquals(importDTO.getSuccess(), 6);
    }

    private InputStream fromResource(String path) throws IOException {
        return Files.newInputStream(Paths.get("src/test/resources/" + path));
    }
}
