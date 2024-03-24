package biblivre.multi_schema;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import biblivre.AbstractContainerDatabaseTest;
import biblivre.TestDatasourceConfiguration;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.configurations.ConfigurationBO;
import biblivre.login.LoginBO;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Import({TestDatasourceConfiguration.class})
@ActiveProfiles("test")
class MultiSchemaHandlerTest extends AbstractContainerDatabaseTest {
    @Autowired LoginBO loginBO;
    @Autowired private ConfigurationBO configurationBO;
    @Autowired private Handler multiSchemaHandler;

    @Test
    void create() {
        try {
            String schemaName = "test";

            ExtendedRequest request = prepareMockRequest(schemaName);

            ExtendedResponse response = Mockito.mock(ExtendedResponse.class);

            multiSchemaHandler.create(request, response);

            SchemaThreadLocal.setSchema(schemaName);

            assertNotNull(loginBO.login("admin", "abracadabra"));
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
}
