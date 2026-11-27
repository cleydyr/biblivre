package biblivre.core.configurations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import biblivre.core.SchemaThreadLocal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class ConfigurationsDAOImplTest {

    @AfterEach
    void tearDown() {
        SchemaThreadLocal.remove();
    }

    @Test
    void list_rejectsUnsetSchemaInsteadOfUsingANullCacheKey() {
        SchemaThreadLocal.remove();
        ConfigurationsDAOImpl configurationsDAO = new ConfigurationsDAOImpl();

        IllegalStateException exception =
                assertThrows(IllegalStateException.class, configurationsDAO::list);

        assertTrue(exception.getMessage().contains("schema"));
        assertEquals(0, cacheOf(configurationsDAO).size());
    }

    @Test
    void save_rejectsUnsetSchemaInsteadOfRemovingANullCacheKey() {
        SchemaThreadLocal.remove();

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> new ConfigurationsDAOImpl().save(List.of(), 1));

        assertTrue(exception.getMessage().contains("schema"));
    }

    @Test
    void list_returnsCachedConfigurationsForTheBoundSchema() {
        SchemaThreadLocal.setSchema("mylibrary");
        ConfigurationsDAOImpl configurationsDAO = new ConfigurationsDAOImpl();
        List<ConfigurationsDTO> cached = List.of(new ConfigurationsDTO());
        cacheOf(configurationsDAO).put("mylibrary", cached);

        assertSame(cached, configurationsDAO.list());
    }

    @SuppressWarnings("unchecked")
    private static Map<String, List<ConfigurationsDTO>> cacheOf(
            ConfigurationsDAOImpl configurationsDAO) {
        return (Map<String, List<ConfigurationsDTO>>)
                ReflectionTestUtils.getField(configurationsDAO, "cache");
    }
}
