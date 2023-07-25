package biblivre.multi_schema;

import static org.junit.jupiter.api.Assertions.*;

import biblivre.AbstractContainerDatabaseTest;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.configurations.ConfigurationBO;
import biblivre.core.schemas.SchemaBO;
import biblivre.login.LoginBO;

import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class MultiSchemaHandlerTest extends AbstractContainerDatabaseTest {

    @Test
    void create() {
        try {
            execute(
                    () -> {
                        Handler multiSchemaHandler = getWiredHandler();

                        String schemaName = "test";

                        ExtendedRequest request = prepareMockRequest(schemaName);

                        ExtendedResponse response = Mockito.mock(ExtendedResponse.class);

                        multiSchemaHandler.create(request, response);

                        LoginBO loginBO = getWiredLoginBO();

                        SchemaThreadLocal.setSchema(schemaName);

                        assertNotNull(loginBO.login("admin", "abracadabra"));
                    });
        } catch (Exception e) {
            fail(e);
        }
    }

    @NotNull
    private static ExtendedRequest prepareMockRequest(String schemaName) throws IOException {
        ExtendedRequest request = Mockito.mock(ExtendedRequest.class);

        Mockito.when(request.getString("title")).thenReturn(schemaName);
        Mockito.when(request.getString("subtitle")).thenReturn(schemaName);
        Mockito.when(request.getString("schema")).thenReturn(schemaName);

        return request;
    }

    @NotNull
    private Handler getWiredHandler() {
        Handler handler = new Handler();

        SchemaBO schemaBO = getWiredSchemaBO();

        handler.setSchemaBO(schemaBO);

        ConfigurationBO configurationBO = getWiredConfigurationBO();

        schemaBO.setConfigurationBO(configurationBO);

        return handler;
    }
}
