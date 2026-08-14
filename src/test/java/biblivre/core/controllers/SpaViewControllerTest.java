package biblivre.core.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

class SpaViewControllerTest {

    @Test
    void spaPage_putsTomcatContextPathOnTheModel() {
        SpaViewController controller = new SpaViewController();
        Model model = new ConcurrentModel();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath("/catalogo");

        controller.spaPage(model, request);

        assertEquals("/catalogo", model.getAttribute("contextPath"));
        assertEquals("\"/catalogo\"", model.getAttribute("contextPathJson"));
        assertEquals("/catalogo/static/spa", model.getAttribute("spaStaticBase"));
    }

    @Test
    void spaPage_putsEmptyContextPathWhenDeployedAtServerRoot() {
        SpaViewController controller = new SpaViewController();
        Model model = new ConcurrentModel();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath("");

        controller.spaPage(model, request);

        assertEquals("", model.getAttribute("contextPath"));
        assertEquals("\"\"", model.getAttribute("contextPathJson"));
        assertEquals("/static/spa", model.getAttribute("spaStaticBase"));
    }
}
