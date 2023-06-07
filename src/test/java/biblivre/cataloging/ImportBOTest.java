package biblivre.cataloging;

import static org.junit.jupiter.api.Assertions.assertEquals;

import biblivre.cataloging.dataimport.impl.ISO2709ImportProcessor;
import biblivre.cataloging.dataimport.impl.MarcFileImportProcessor;
import biblivre.cataloging.dataimport.impl.MarcXMLImportProcessor;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

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
    void testImportBO() throws IOException, URISyntaxException {
        ImportDTO importDTO =
                importBO.loadFromFile(() -> fromResource("cataloguing/import/biblivre-import.mrc"));

        assertEquals(importDTO.getFailure(), 0);

        assertEquals(importDTO.getSuccess(), 25);
    }

    @Test
    void testLoadFromFileWithXML()
            throws IOException, URISyntaxException, ParserConfigurationException, SAXException {
        ImportDTO importDTO =
                importBO.loadFromFile(
                        () -> fromResource("cataloguing/import/biblioteca-de-espanha.xml"));

        assertEquals(importDTO.getFailure(), 0);

        assertEquals(importDTO.getSuccess(), 6);
    }

    private InputStream fromResource(String path) throws IOException, URISyntaxException {
        return Files.newInputStream(Paths.get("src/test/resources/" + path));
    }
}
