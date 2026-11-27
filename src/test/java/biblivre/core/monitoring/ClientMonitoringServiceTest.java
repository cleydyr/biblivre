package biblivre.core.monitoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import biblivre.core.SchemaThreadLocal;
import biblivre.login.LoginDTO;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Clock;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.mock.web.MockHttpServletRequest;

class ClientMonitoringServiceTest {

    static final String DSN = "https://key@errors.example.org/1";

    HttpClient httpClient = mock(HttpClient.class);

    ClientMonitoringService service(String dsn) {
        ClientMonitoringProperties properties = new ClientMonitoringProperties();
        properties.setDsn(dsn);
        properties.setEnvironment("staging");

        Properties build = new Properties();
        build.setProperty("version", "6.0.0");

        return new ClientMonitoringService(
                properties, new BuildProperties(build), httpClient, Clock.systemUTC());
    }

    @AfterEach
    void clearSchema() {
        SchemaThreadLocal.remove();
    }

    @Test
    void disabledWhenDsnIsBlankOrInvalid() {
        assertFalse(service("").isEnabled());
        assertFalse(service("not a dsn").isEnabled());
        assertEquals("{}", service("").clientConfigJson(new MockHttpServletRequest()));
    }

    @Test
    void clientConfigCarriesReleaseSchemaAndLoggedUser() {
        SchemaThreadLocal.setSchema("bib1");

        LoginDTO login = new LoginDTO();
        login.setId(42);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath("/Biblivre6");
        request.getSession().setAttribute("bib1.logged_user", login);

        JSONObject config = new JSONObject(service(DSN).clientConfigJson(request));

        assertEquals(DSN, config.getString("dsn"));
        assertEquals("/Biblivre6/api/v2/client-errors", config.getString("tunnel"));
        assertEquals("biblivre@6.0.0", config.getString("release"));
        assertEquals("staging", config.getString("environment"));
        assertEquals("bib1", config.getString("schema"));
        assertEquals("42", config.getString("userId"));
    }

    @Test
    void clientConfigDoesNotCreateSessionForAnonymousVisitor() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        JSONObject config = new JSONObject(service(DSN).clientConfigJson(request));

        assertFalse(config.has("userId"));
        assertEquals(null, request.getSession(false));
    }

    @Test
    void clientConfigIsSafeInsideScriptTag() {
        SchemaThreadLocal.setSchema("</script><b>");

        String json = service(DSN).clientConfigJson(new MockHttpServletRequest());

        assertFalse(json.contains("<"));
        assertTrue(json.contains("\\u003C"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void forwardPostsToGlitchTipEnvelopeEndpoint() {
        when(httpClient.sendAsync(any(HttpRequest.class), any()))
                .thenReturn(new CompletableFuture<>());

        service(DSN).forward(new byte[] {1, 2}, "203.0.113.9");

        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).sendAsync(captor.capture(), any());
        HttpRequest sent = captor.getValue();

        assertEquals("https://errors.example.org/api/1/envelope/", sent.uri().toString());
        assertEquals("POST", sent.method());
        assertEquals("203.0.113.9", sent.headers().firstValue("X-Forwarded-For").orElseThrow());
        assertTrue(
                sent.headers()
                        .firstValue("X-Sentry-Auth")
                        .orElseThrow()
                        .contains("sentry_key=key"));
    }
}
