package biblivre.core.configurations;

import static org.junit.jupiter.api.Assertions.assertEquals;

import biblivre.core.SchemaThreadLocal;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class ConfigurationsDAOImplTest {

    @AfterEach
    void tearDown() {
        SchemaThreadLocal.remove();
    }

    @Test
    void list_returnsEmptyWhenSchemaIsUnset() {
        SchemaThreadLocal.remove();

        assertEquals(List.of(), new ConfigurationsDAOImpl().list());
    }
}
