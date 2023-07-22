package biblivre.multi_schema;

import biblivre.AbstractContainerDatabaseTest;
import biblivre.administration.permissions.PermissionBO;
import biblivre.administration.permissions.PermissionDAOImpl;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.Updates;
import biblivre.core.configurations.ConfigurationBO;
import biblivre.core.schemas.SchemaBO;
import biblivre.core.schemas.SchemaDAO;
import biblivre.core.schemas.SchemasDAOImpl;
import biblivre.login.LoginBO;
import biblivre.login.LoginDAOImpl;
import biblivre.update.v6_0_0$3_0_0$alpha.Update;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class HandlerTest extends AbstractContainerDatabaseTest {

    @Test
    void create() {
        try {
            execute(() -> {
                Updates updateSuite = new Updates();

                updateSuite.setUpdateServicesMap(Map.of(
                        Update.class.getName(), new Update()
                ));

                updateSuite.schemaUpdate("single");

                Handler handler = getWiredHandler();

                String schemaName = "test";

                ExtendedRequest request = prepareMockRequest(schemaName);

                ExtendedResponse response = Mockito.mock(ExtendedResponse.class);

                handler.create(request, response);

                LoginBO loginBO = getWiredLoginBO();

                SchemaThreadLocal.setSchema(schemaName);

                assertNotNull(loginBO.login("admin", "abracadabra"));
            });
        }
        catch (Exception e) {
            fail(e);
        }
    }

    @NotNull
    private static ExtendedRequest prepareMockRequest(String schemaName) {
        ExtendedRequest request = Mockito.mock(ExtendedRequest.class);

        HttpSession session = Mockito.mock(HttpSession.class);

        ServletContext servletContext = Mockito.mock(ServletContext.class);

        Mockito.when(session.getServletContext()).thenReturn(servletContext);

        Mockito.when(request.getSession()).thenReturn(session);

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

        SchemaDAO schemaDAO = getInstance(SchemasDAOImpl.class);

        schemaBO.setSchemaDAO(schemaDAO);

        handler.setSchemaBO(schemaBO);

        ConfigurationBO configurationBO = new ConfigurationBO();

        configurationBO.setSchemaBO(schemaBO);

        handler.setConfigurationBO(configurationBO);
        return handler;
    }
}