package biblivre.core.translations;

import static org.junit.jupiter.api.Assertions.*;

import biblivre.AbstractContainerDatabaseTest;
import biblivre.core.SchemaThreadLocal;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class TranslationBOTest extends AbstractContainerDatabaseTest {

    @Test
    void isJavaLocaleAvailable() {
        execute(
                () -> {
                    SchemaThreadLocal.setSchema("single");

                    TranslationBO translationBO = getWiredTranslationsBO();

                    assertTrue(translationBO.isJavaLocaleAvailable("pt-BR"));

                    assertTrue(translationBO.isJavaLocaleAvailable("fr-FR"));

                    assertFalse(translationBO.isJavaLocaleAvailable("foo-bar"));

                    assertFalse(translationBO.isJavaLocaleAvailable("foobar"));
                });
    }
}
