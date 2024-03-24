package biblivre.digitalmedia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import biblivre.AbstractContainerDatabaseTest;
import biblivre.TestDatasourceConfiguration;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.file.BiblivreFile;
import biblivre.core.file.MemoryFile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.io.Files;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@Import({TestDatasourceConfiguration.class})
@ActiveProfiles("test")
public class DigitalMediaDAOTest extends AbstractContainerDatabaseTest {
    @Autowired private BaseDigitalMediaDAO dao;

    private static final String DEFAULT_SCHEMA = "single";

    @BeforeEach
    public void deleteAll() {
        SchemaThreadLocal.setSchema(DEFAULT_SCHEMA);

        List<DigitalMediaDTO> list = dao.list();

        for (DigitalMediaDTO digitalMedia : list) {
            dao.delete(digitalMedia.getId());
        }
    }

    @Test
    public void testDelete() {
        try {
            int quantity = 5;

            for (int i = 0; i < quantity; i++) {
                String fileName = "testfile1.txt";

                String fileContent = "foobar";

                MemoryFile file = _createMemoryFile(fileName, fileContent);

                dao.save(file);
            }

            List<DigitalMediaDTO> listBefore = dao.list();

            assertEquals(listBefore.size(), quantity);

            for (DigitalMediaDTO digitalMedia : listBefore) {
                dao.delete(digitalMedia.getId());
            }

            List<DigitalMediaDTO> listAfter = dao.list();

            assertEquals(listAfter.size(), 0);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testImport() {
        try {
            File file = File.createTempFile("temp", null);

            String content = "foobar";

            Files.write(content, file, Charset.defaultCharset());

            long oid = dao.importFile(file);

            BiblivreFile retrievedFile = dao.getFile(oid);

            retrievedFile.setSize(content.length());

            Assertions.assertNull(retrievedFile.getName());

            String retrievedContent = _getContent(retrievedFile);

            assertEquals(content, retrievedContent);
        } catch (Exception e) {
            fail(e);
        }
    }

    private String _getContent(BiblivreFile retrievedFile) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        retrievedFile.copy(outputStream);

        return outputStream.toString();
    }

    @Test
    public void testLoad() throws IOException {
        int quantity = 5;

        for (int i = 0; i < quantity; i++) {
            MemoryFile file = _createMemoryFile("testfile.txt", "foobar");

            dao.save(file);

            file.close();
        }

        assertEquals(quantity, dao.list().size());
    }

    @Test
    public void testCorrectDataIsRetrieved() {
        String fileName = "testfile.txt";

        String fileContent = "foobar";

        MemoryFile file = _createMemoryFile(fileName, fileContent);

        Integer id = dao.save(file);

        try (BiblivreFile retrievedFile = dao.load(id, fileName)) {

            assertEquals(fileName, retrievedFile.getName());

            Assertions.assertNull(dao.load(0, "nonexistent"));

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            retrievedFile.copy(outputStream);

            assertEquals(fileContent, outputStream.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCorrectBlobIsDeleted() {
        String fileName1 = "testfile1.txt";

        String fileContent1 = "foobar";

        MemoryFile file1 = _createMemoryFile(fileName1, fileContent1);

        String fileName2 = "testfile2.txt";

        String fileContent2 = "foobar";

        MemoryFile file2 = _createMemoryFile(fileName2, fileContent2);

        Integer id1 = dao.save(file1);

        Integer id2 = dao.save(file2);

        dao.delete(id2);

        try (BiblivreFile retrievedFile1 = dao.load(id1, fileName1)) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            retrievedFile1.copy(outputStream);

            assertEquals(fileContent1, outputStream.toString());
        } catch (Exception e) {
            fail(e);
        }
    }

    private MemoryFile _createMemoryFile(String fileName, String content) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes());

        return new MemoryFile(fileName, "text/plain", content.length(), inputStream);
    }
}
