package biblivre.core.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import biblivre.core.SchemaThreadLocal;
import biblivre.core.configurations.ConfigurationBO;
import biblivre.core.configurations.ConfigurationsDAOImpl;
import biblivre.core.configurations.ConfigurationsDTO;
import biblivre.core.configurations.FlagsProvider;
import biblivre.core.schemas.SchemaBO;
import biblivre.core.schemas.SchemaDTO;
import biblivre.core.utils.Constants;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

class SchemaFilterTest {

    @BeforeEach
    void setUp() {
        // github-ci runs test classes in parallel and reuses threads. Another class can
        // leave a schema bound; the filter restores that value, so start from a clean thread.
        SchemaThreadLocal.remove();
    }

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
    void usesBoundSchemaForRobotsTxt() throws Exception {
        SchemaDTO temp = new SchemaDTO("temp", "Temp");
        SchemaFilter filter = boundFilter("temp", true, Set.of(temp));
        MockHttpServletRequest request = requestAt("/Biblivre6/robots.txt");
        request.addHeader(SchemaUtil.BOUND_SCHEMA_HEADER, "temp");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(
                request, response, (req, res) -> assertEquals("temp", SchemaThreadLocal.get()));

        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
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

    @Test
    void errorForwardAfterSchemaIsClearedCanReadThatLibraryConfiguration() throws Exception {
        ConfigurationsDAOImpl configurationsDAO = new ConfigurationsDAOImpl();
        List<ConfigurationsDTO> cached = List.of(new ConfigurationsDTO());
        cacheOf(configurationsDAO).put("mylibrary", cached);

        SchemaFilter filter = loadedFilter("mylibrary");
        MockHttpServletRequest request = requestAt("/Biblivre6/mylibrary/");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                (req, res) -> assertEquals("mylibrary", SchemaThreadLocal.get()));
        assertNull(SchemaThreadLocal.get());

        request.setRequestURI("/Biblivre6/error");
        request.setDispatcherType(DispatcherType.FORWARD);
        request.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, "/Biblivre6/mylibrary/");
        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 500);

        filter.doFilter(
                request, response, (req, res) -> assertSame(cached, configurationsDAO.list()));

        assertNull(SchemaThreadLocal.get());
    }

    @Test
    void errorForwardRebindsTheOriginalLibraryInsteadOfTheErrorPath() throws Exception {
        SchemaFilter filter = loadedFilter("mylibrary");
        MockHttpServletRequest request = requestAt("/Biblivre6/error");
        request.setDispatcherType(DispatcherType.FORWARD);
        request.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, "/Biblivre6/mylibrary/");
        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 500);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                (req, res) -> assertEquals("mylibrary", SchemaThreadLocal.get()));

        assertEquals("mylibrary", request.getAttribute(SchemaFilter.RESOLVED_SCHEMA_ATTRIBUTE));
        assertNull(SchemaThreadLocal.get());
    }

    @Test
    void containerErrorDispatchRebindsTheOriginalLibrary() throws Exception {
        SchemaFilter filter = loadedFilter("mylibrary");
        MockHttpServletRequest request = requestAt("/Biblivre6/jsp/error_fatal.jsp");
        request.setDispatcherType(DispatcherType.ERROR);
        request.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, "/Biblivre6/mylibrary/");
        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 500);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                (req, res) -> assertEquals("mylibrary", SchemaThreadLocal.get()));

        assertNull(SchemaThreadLocal.get());
    }

    @Test
    void nestedForwardKeepsTheSchemaAfterTheForwardReturns() throws Exception {
        SchemaFilter filter = loadedFilter("mylibrary");
        MockHttpServletRequest request = requestAt("/Biblivre6/mylibrary/");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                (req, res) -> {
                    assertEquals("mylibrary", SchemaThreadLocal.get());
                    request.setDispatcherType(DispatcherType.FORWARD);
                    request.setRequestURI("/Biblivre6/WEB-INF/jsp/index.jsp");
                    filter.doFilter(
                            request,
                            response,
                            (innerRequest, innerResponse) ->
                                    assertEquals("mylibrary", SchemaThreadLocal.get()));
                    assertEquals("mylibrary", SchemaThreadLocal.get());
                });

        assertNull(SchemaThreadLocal.get());
    }

    @Test
    void errorForwardForForbiddenPathDoesNotRejectAgain() throws Exception {
        SchemaDTO temp = new SchemaDTO("temp", "Temp");
        SchemaFilter filter = boundFilter("temp", true, Set.of(temp));
        MockHttpServletRequest request = requestAt("/Biblivre6/error");
        request.setDispatcherType(DispatcherType.FORWARD);
        request.addHeader(SchemaUtil.BOUND_SCHEMA_HEADER, "temp");
        request.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, "/Biblivre6/other/");
        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, HttpServletResponse.SC_FORBIDDEN);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(
                request, response, (req, res) -> assertEquals("temp", SchemaThreadLocal.get()));

        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    void errorForwardForUnknownBoundSchemaDoesNotUseAnotherLibrary() throws Exception {
        SchemaFilter filter = new SchemaFilter();
        SchemaBO schemaBO = mock(SchemaBO.class);
        when(schemaBO.isNotLoaded(any())).thenReturn(true);
        when(schemaBO.getSchemas()).thenReturn(Set.of());
        ConfigurationBO configurationBO = mock(ConfigurationBO.class);
        when(configurationBO.isMultipleSchemasEnabled()).thenReturn(true);
        filter.setSchemaBO(schemaBO);
        filter.setConfigurationBO(configurationBO);
        filter.setFlagsProvider(mock(FlagsProvider.class));

        MockHttpServletRequest request = requestAt("/Biblivre6/error");
        request.setDispatcherType(DispatcherType.ERROR);
        request.addHeader(SchemaUtil.BOUND_SCHEMA_HEADER, "ghost");
        request.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, "/Biblivre6/ghost/");
        request.setAttribute(RequestDispatcher.ERROR_EXCEPTION, new IllegalStateException("boom"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        filter.doFilter(
                request,
                response,
                (req, res) -> {
                    chainCalled.set(true);
                    assertNull(SchemaThreadLocal.get());
                });

        assertTrue(chainCalled.get());
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        verify(configurationBO, never()).isMultipleSchemasEnabled();
    }

    @Test
    void usesSingleSchemaWhenTheRequestHasNoLibrary() throws Exception {
        SchemaFilter filter = filterWithUnknownSchemas(false);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(
                requestAt("/Biblivre6/"),
                response,
                (req, res) -> assertEquals(Constants.SINGLE_SCHEMA, SchemaThreadLocal.get()));
    }

    @Test
    void usesGlobalSchemaWhenMultipleSchemasAreEnabledAndTheRequestHasNoLibrary() throws Exception {
        SchemaFilter filter = filterWithUnknownSchemas(true);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(
                requestAt("/Biblivre6/"),
                response,
                (req, res) -> assertEquals(Constants.GLOBAL_SCHEMA, SchemaThreadLocal.get()));
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

    private static SchemaFilter loadedFilter(String schema) {
        SchemaFilter filter = new SchemaFilter();
        SchemaBO schemaBO = mock(SchemaBO.class);
        when(schemaBO.isNotLoaded(any())).thenReturn(true);
        when(schemaBO.isNotLoaded(schema)).thenReturn(false);
        when(schemaBO.getSchemas()).thenReturn(Set.of(new SchemaDTO(schema, schema)));
        filter.setSchemaBO(schemaBO);
        filter.setConfigurationBO(mock(ConfigurationBO.class));
        filter.setFlagsProvider(mock(FlagsProvider.class));
        return filter;
    }

    private static SchemaFilter filterWithUnknownSchemas(boolean multipleSchemas) {
        SchemaFilter filter = new SchemaFilter();
        SchemaBO schemaBO = mock(SchemaBO.class);
        when(schemaBO.isNotLoaded(any())).thenReturn(true);
        when(schemaBO.getSchemas()).thenReturn(Set.of());
        ConfigurationBO configurationBO = mock(ConfigurationBO.class);
        when(configurationBO.isMultipleSchemasEnabled()).thenReturn(multipleSchemas);
        filter.setSchemaBO(schemaBO);
        filter.setConfigurationBO(configurationBO);
        filter.setFlagsProvider(mock(FlagsProvider.class));
        return filter;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, List<ConfigurationsDTO>> cacheOf(
            ConfigurationsDAOImpl configurationsDAO) {
        return (Map<String, List<ConfigurationsDTO>>)
                ReflectionTestUtils.getField(configurationsDAO, "cache");
    }
}
