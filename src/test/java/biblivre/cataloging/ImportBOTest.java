package biblivre.cataloging;

import static org.junit.jupiter.api.Assertions.assertEquals;

import biblivre.cataloging.bibliographic.BiblioRecordBO;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class ImportBOTest {
    ImportBO importBO = new ImportBO();

    @BeforeAll
    void setUp() {
        importBO.setBiblioRecordBO(new BiblioRecordBO());
    }

    @Test
    void testImportBO() throws IOException, URISyntaxException {
        ClassLoader classLoader = ImportBOTest.class.getClassLoader();

        URL resource = classLoader.getResource("cataloguing/import/biblivre-import.mrc");

        InputStream marcInputStream = Files.newInputStream(Paths.get(resource.toURI()));

        ImportDTO importDTO = importBO.loadFromFile(marcInputStream);

        assertEquals(importDTO.getFailure(), 0);

        assertEquals(importDTO.getSuccess(), 25);
    }
}
