package biblivre.core.translations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import biblivre.AbstractContainerDatabaseTest;
import biblivre.TestDatasourceConfiguration;
import biblivre.core.SchemaThreadLocal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Import({TestDatasourceConfiguration.class})
@ActiveProfiles("test")
class TranslationBOTest extends AbstractContainerDatabaseTest {

    @Autowired private TranslationBO translationBO;

    @Test
    void isJavaLocaleAvailable() {
        SchemaThreadLocal.setSchema("single");

        assertTrue(translationBO.isJavaLocaleAvailable("pt-BR"));

        assertTrue(translationBO.isJavaLocaleAvailable("fr-FR"));

        assertFalse(translationBO.isJavaLocaleAvailable("foo-bar"));

        assertFalse(translationBO.isJavaLocaleAvailable("foobar"));
    }
}
