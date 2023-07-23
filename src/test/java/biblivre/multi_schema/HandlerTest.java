package biblivre.multi_schema;

import static org.junit.jupiter.api.Assertions.*;

import biblivre.AbstractContainerDatabaseTest;
import biblivre.administration.permissions.PermissionBO;
import biblivre.administration.permissions.PermissionDAOImpl;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.configurations.ConfigurationBO;
import biblivre.core.schemas.SchemaBO;
import biblivre.core.schemas.SchemaDAO;
import biblivre.core.schemas.SchemasDAOImpl;
import biblivre.login.LoginBO;
import biblivre.login.LoginDAOImpl;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class HandlerTest extends AbstractContainerDatabaseTest {

    @Test
    void create() {
        try {
            execute(
                    () -> {
                        Handler handler = getWiredHandler();

                        String schemaName = "test";

                        ExtendedRequest request = prepareMockRequest(schemaName);

                        ExtendedResponse response = Mockito.mock(ExtendedResponse.class);

                        handler.create(request, response);

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
    private LoginBO getWiredLoginBO() {
        LoginBO loginBO = new LoginBO();

        loginBO.setLoginDAO(getInstance(LoginDAOImpl.class));

        PermissionBO permissionBO = new PermissionBO();

        permissionBO.setPermissionDAO(getInstance(PermissionDAOImpl.class));

        loginBO.setPermissionBO(permissionBO);
        return loginBO;
    }

    @NotNull
    private Handler getWiredHandler() {
        Handler handler = new Handler();

        SchemaBO schemaBO = new SchemaBO();

        schemaBO.setDatabaseTemplate(new ClassPathResource("/META-INF/sql/biblivre-template.sql"));

        SchemaDAO schemaDAO = getInstance(SchemasDAOImpl.class);

        schemaBO.setSchemaDAO(schemaDAO);

        handler.setSchemaBO(schemaBO);

        ConfigurationBO configurationBO = new ConfigurationBO();

        configurationBO.setSchemaBO(schemaBO);

        handler.setConfigurationBO(configurationBO);
        return handler;
    }
}
