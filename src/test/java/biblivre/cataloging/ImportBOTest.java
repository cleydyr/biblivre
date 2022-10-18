package biblivre.cataloging;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.xml.sax.SAXException;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ImportBOTest {
    @Autowired ImportBO importBO;

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
