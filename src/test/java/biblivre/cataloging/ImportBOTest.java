package biblivre.cataloging;

import static org.junit.jupiter.api.Assertions.assertEquals;

import biblivre.cataloging.dataimport.impl.ISO2709ImportProcessor;
import biblivre.cataloging.dataimport.impl.MarcFileImportProcessor;
import biblivre.cataloging.dataimport.impl.MarcXMLImportProcessor;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ImportBOTest {
    ImportBO importBO;

    @BeforeEach
    void setUp() {
        importBO = new ImportBO();

        importBO.setImportProcessors(
                List.of(
                        new ISO2709ImportProcessor(),
                        new MarcXMLImportProcessor(),
                        new MarcFileImportProcessor()));
    }

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
