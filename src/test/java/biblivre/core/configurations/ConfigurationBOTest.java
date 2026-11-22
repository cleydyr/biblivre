package biblivre.core.configurations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import biblivre.core.SchemaThreadLocal;
import biblivre.core.utils.Constants;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class ConfigurationBOTest {

    @AfterEach
    void tearDown() {
        SchemaThreadLocal.remove();
    }

    @Test
    void getBoolean_rejectsUnsetSchemaInsteadOfReadingAnotherSchema() {
        SchemaThreadLocal.remove();
        ConfigurationsDAO configurationsDAO = mock(ConfigurationsDAO.class);
        when(configurationsDAO.list()).thenReturn(List.of());
        ConfigurationBO configurationBO = new ConfigurationBO();
        configurationBO.setConfigurationsDAO(configurationsDAO);

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> configurationBO.getBoolean(Constants.CONFIG_NEW_LIBRARY));

        assertTrue(exception.getMessage().contains("schema"));
        verify(configurationsDAO, never()).list();
    }
}
