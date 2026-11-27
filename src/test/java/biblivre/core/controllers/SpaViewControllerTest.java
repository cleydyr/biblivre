package biblivre.core.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import biblivre.core.monitoring.ClientMonitoringService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

class SpaViewControllerTest {

    private static SpaViewController newController() {
        ClientMonitoringService monitoring = mock(ClientMonitoringService.class);
        when(monitoring.clientConfigJson(any(HttpServletRequest.class)))
                .thenReturn("{\"dsn\":\"x\"}");
        return new SpaViewController(monitoring);
    }

    @Test
    void spaPage_putsTomcatContextPathOnTheModel() {
        SpaViewController controller = newController();
        Model model = new ConcurrentModel();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath("/catalogo");

        controller.spaPage(model, request);

        assertEquals("/catalogo", model.getAttribute("contextPath"));
        assertEquals("\"/catalogo\"", model.getAttribute("contextPathJson"));
        assertEquals("/catalogo/static/spa", model.getAttribute("spaStaticBase"));
        assertEquals("{\"dsn\":\"x\"}", model.getAttribute("monitoringConfigJson"));
    }

    @Test
    void spaPage_putsEmptyContextPathWhenDeployedAtServerRoot() {
        SpaViewController controller = newController();
        Model model = new ConcurrentModel();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath("");

        controller.spaPage(model, request);

        assertEquals("", model.getAttribute("contextPath"));
        assertEquals("\"\"", model.getAttribute("contextPathJson"));
        assertEquals("/static/spa", model.getAttribute("spaStaticBase"));
    }
}
