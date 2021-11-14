package biblivre.cataloging;

import static org.junit.jupiter.api.Assertions.assertEquals;

import biblivre.cataloging.bibliographic.BiblioRecordBO;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.xml.sax.SAXException;

@TestInstance(Lifecycle.PER_CLASS)
public class ImportBOTest {
    ImportBO importBO = new ImportBO();

    @BeforeAll
    void setUp() {
        importBO.setBiblioRecordBO(new BiblioRecordBO());
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
        ClassLoader classLoader = ImportBOTest.class.getClassLoader();

        URL resource = classLoader.getResource(path);

        return Files.newInputStream(Paths.get(resource.toURI()));
    }
}
