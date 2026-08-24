package biblivre.view.taglibs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import biblivre.core.SchemaThreadLocal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;

class LayoutHeadTest {

    @AfterEach
    void tearDown() {
        SchemaThreadLocal.remove();
    }

    @Test
    void exposesThreadLocalSchemaOnRequestBeforeHeadBodyRenders() {
        SchemaThreadLocal.setSchema("bcuniaodosaber");

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockPageContext pageContext =
                new MockPageContext(
                        new MockServletContext(), request, new MockHttpServletResponse());

        LayoutHead layoutHead = new LayoutHead();
        layoutHead.setPageContext(pageContext);
        layoutHead.doStartTag();

        assertEquals("bcuniaodosaber", request.getAttribute("schema"));
    }
}
