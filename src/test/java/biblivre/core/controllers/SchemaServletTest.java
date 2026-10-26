package biblivre.core.controllers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.RequestParserHelper;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.configurations.ConfigurationBO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.lang.reflect.Method;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;

class SchemaServletTest {

    @AfterEach
    void tearDown() {
        SchemaThreadLocal.remove();
    }

    @Test
    void setRequestParserHelper_isAutowired() throws Exception {
        Method setter =
                SchemaServlet.class.getMethod("setRequestParserHelper", RequestParserHelper.class);

        assertNotNull(
                setter.getAnnotation(Autowired.class),
                "RequestParserHelper must be Spring-injected so extra/ static files can wrap the request");
    }

    @Test
    void service_forwardsApiRequestsToDispatcherServlet() throws Exception {
        RecordingRequestDispatcher requestDispatcher = new RecordingRequestDispatcher();
        SchemaServlet servlet = servletWithNamedDispatcher(requestDispatcher);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v2/schemas");
        request.setServletPath("/api/v2/schemas");
        MockHttpServletResponse response = new MockHttpServletResponse();

        servlet.service(request, response);

        assertSame(request, requestDispatcher.forwardedRequest);
        assertSame(response, requestDispatcher.forwardedResponse);
    }

    @Test
    void service_throwsWhenDispatcherServletIsMissing() throws Exception {
        SchemaServlet servlet = new SchemaServlet();
        servlet.init(new MockServletConfig(new MockServletContext()));
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v2/schemas");
        request.setServletPath("/api/v2/schemas");
        MockHttpServletResponse response = new MockHttpServletResponse();

        ServletException exception =
                assertThrows(ServletException.class, () -> servlet.service(request, response));

        assertTrue(exception.getMessage().contains("dispatcherServlet"));
    }

    @Test
    void doGet_doesNotReadConfigurationWhenSchemaIsUnset() throws Exception {
        SchemaThreadLocal.remove();
        ConfigurationBO configurationBO = mock(ConfigurationBO.class);
        SchemaServlet servlet = new SchemaServlet();
        servlet.setConfigurationBO(configurationBO);
        servlet.init(new MockServletConfig(new MockServletContext()));

        ExtendedRequest request = mock(ExtendedRequest.class);
        when(request.getServletPath()).thenReturn("/other");
        when(request.getController()).thenReturn("");
        when(request.getString(anyString())).thenReturn("");
        when(request.getBoolean(anyString())).thenReturn(false);
        ExtendedResponse response = mock(ExtendedResponse.class);

        servlet.doGet(request, response);

        verify(configurationBO, never()).getBoolean(anyString());
    }

    private static SchemaServlet servletWithNamedDispatcher(RequestDispatcher requestDispatcher)
            throws Exception {
        MockServletContext servletContext = new MockServletContext();
        servletContext.registerNamedDispatcher("dispatcherServlet", requestDispatcher);

        SchemaServlet servlet = new SchemaServlet();
        servlet.init(new MockServletConfig(servletContext));
        return servlet;
    }

    private static final class RecordingRequestDispatcher implements RequestDispatcher {
        private ServletRequest forwardedRequest;
        private ServletResponse forwardedResponse;

        @Override
        public void forward(ServletRequest request, ServletResponse response) {
            this.forwardedRequest = request;
            this.forwardedResponse = response;
        }

        @Override
        public void include(ServletRequest request, ServletResponse response) {}
    }
}
