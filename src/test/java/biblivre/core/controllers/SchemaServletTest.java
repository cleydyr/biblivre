package biblivre.core.controllers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import biblivre.core.RequestParserHelper;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;

class SchemaServletTest {

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
