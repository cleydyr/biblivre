package biblivre.core.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import biblivre.core.SchemaThreadLocal;
import biblivre.core.configurations.ConfigurationBO;
import biblivre.core.configurations.FlagsProvider;
import biblivre.core.schemas.SchemaBO;
import biblivre.core.schemas.SchemaDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class SchemaFilterTest {

    @AfterEach
    void tearDown() {
        SchemaThreadLocal.remove();
    }

    @Test
    void rejectsPathToAnotherSchemaWhenBound() throws Exception {
        SchemaFilter filter = boundFilter("temp", true);
        MockHttpServletRequest request = requestAt("/Biblivre6/other/");
        request.addHeader(SchemaUtil.BOUND_SCHEMA_HEADER, "temp");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
        verify(chain, never()).doFilter(any(), any());
        assertNull(SchemaThreadLocal.get());
    }

    @Test
    void rejectsUnknownBoundSchema() throws Exception {
        SchemaFilter filter = boundFilter("ghost", false);
        MockHttpServletRequest request = requestAt("/Biblivre6/spa/search");
        request.addHeader(SchemaUtil.BOUND_SCHEMA_HEADER, "ghost");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void usesBoundSchemaOnReservedPathsAndIgnoresClientSchemaHeader() throws Exception {
        SchemaDTO temp = new SchemaDTO("temp", "Temp");
        SchemaDTO other = new SchemaDTO("other", "Other");
        SchemaFilter filter = boundFilter("temp", true, Set.of(temp, other));
        MockHttpServletRequest request = requestAt("/Biblivre6/spa/search");
        request.addHeader(SchemaUtil.BOUND_SCHEMA_HEADER, "temp");
        request.addHeader("X-Biblivre-Schema", "other");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(
                request, response, (req, res) -> assertEquals("temp", SchemaThreadLocal.get()));

        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertEquals(Set.of(temp), request.getAttribute("schemas"));
        assertEquals("/Biblivre6/spa/search?schema=temp", request.getAttribute("spaHref"));
    }

    @Test
    void allowsPathThatMatchesBoundSchema() throws Exception {
        SchemaDTO temp = new SchemaDTO("temp", "Temp");
        SchemaFilter filter = boundFilter("temp", true, Set.of(temp));
        MockHttpServletRequest request = requestAt("/Biblivre6/temp/");
        request.addHeader(SchemaUtil.BOUND_SCHEMA_HEADER, "temp");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(
                request, response, (req, res) -> assertEquals("temp", SchemaThreadLocal.get()));

        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    private static SchemaFilter boundFilter(String boundSchema, boolean loaded) {
        return boundFilter(boundSchema, loaded, Set.of());
    }

    private static SchemaFilter boundFilter(
            String boundSchema, boolean loaded, Set<SchemaDTO> schemas) {
        SchemaFilter filter = new SchemaFilter();
        SchemaBO schemaBO = mock(SchemaBO.class);
        when(schemaBO.isNotLoaded(boundSchema)).thenReturn(!loaded);
        when(schemaBO.getSchemas()).thenReturn(schemas);
        filter.setSchemaBO(schemaBO);
        filter.setConfigurationBO(mock(ConfigurationBO.class));
        filter.setFlagsProvider(mock(FlagsProvider.class));
        return filter;
    }

    private static MockHttpServletRequest requestAt(String requestURI) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath("/Biblivre6");
        request.setRequestURI(requestURI);
        return request;
    }
}
