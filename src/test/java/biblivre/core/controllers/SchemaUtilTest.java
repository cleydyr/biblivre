package biblivre.core.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import biblivre.core.schemas.SchemaDTO;
import biblivre.core.utils.Constants;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class SchemaUtilTest {

    private static final String CONTEXT_PATH = "/biblivre";

    @Test
    void buildSpaSearchHref_forGlobalSchema_addsShowSelectSchemaParam() {
        assertEquals(
                CONTEXT_PATH + SchemaUtil.SPA_SEARCH_PATH + "?showSelectSchema",
                SchemaUtil.buildSpaSearchHref(CONTEXT_PATH, Constants.GLOBAL_SCHEMA));
    }

    @Test
    void buildSpaSearchHref_forLibrarySchema_addsSchemaParam() {
        assertEquals(
                CONTEXT_PATH + SchemaUtil.SPA_SEARCH_PATH + "?schema=public",
                SchemaUtil.buildSpaSearchHref(CONTEXT_PATH, "public"));
    }

    @Test
    void buildSpaSearchHref_forSingleSchema_addsSchemaParam() {
        assertEquals(
                CONTEXT_PATH + SchemaUtil.SPA_SEARCH_PATH + "?schema=single",
                SchemaUtil.buildSpaSearchHref(CONTEXT_PATH, Constants.SINGLE_SCHEMA));
    }

    @Test
    void buildSpaSearchHref_forBlankSchema_returnsPathWithoutQuery() {
        assertEquals(
                CONTEXT_PATH + SchemaUtil.SPA_SEARCH_PATH,
                SchemaUtil.buildSpaSearchHref(CONTEXT_PATH, ""));
        assertEquals(
                CONTEXT_PATH + SchemaUtil.SPA_SEARCH_PATH,
                SchemaUtil.buildSpaSearchHref(CONTEXT_PATH, null));
    }

    @Test
    void buildSpaStaticBase_prefixesWhateverContextTheContainerProvides() {
        assertEquals("/catalogo/static/spa", SchemaUtil.buildSpaStaticBase("/catalogo"));
        assertEquals("/biblivre/static/spa", SchemaUtil.buildSpaStaticBase("/biblivre"));
        assertEquals("/static/spa", SchemaUtil.buildSpaStaticBase(""));
        assertEquals("/static/spa", SchemaUtil.buildSpaStaticBase(null));
    }

    @Test
    void extractBoundSchema_readsBoundHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(SchemaUtil.BOUND_SCHEMA_HEADER, "temp");

        assertEquals("temp", SchemaUtil.extractBoundSchema(request));
    }

    @Test
    void extractBoundSchema_treatsBlankAsAbsent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(SchemaUtil.BOUND_SCHEMA_HEADER, "  ");

        assertNull(SchemaUtil.extractBoundSchema(request));
    }

    @Test
    void extractPathSchema_readsFirstSegment() {
        MockHttpServletRequest request = requestAt("/Biblivre6", "/Biblivre6/temp/cataloging");

        assertEquals("temp", SchemaUtil.extractPathSchema(request));
    }

    @Test
    void extractPathSchema_ignoresReservedSpaPath() {
        MockHttpServletRequest request = requestAt("/Biblivre6", "/Biblivre6/spa/search");

        assertNull(SchemaUtil.extractPathSchema(request));
    }

    @Test
    void extractPathSchema_ignoresReservedApiPath() {
        MockHttpServletRequest request = requestAt("/Biblivre6", "/Biblivre6/api/v2/users");

        assertNull(SchemaUtil.extractPathSchema(request));
    }

    @Test
    void extractSchema_stillPrefersClientSchemaHeaderWhenUnbound() {
        MockHttpServletRequest request = requestAt("/Biblivre6", "/Biblivre6/temp/");
        request.addHeader("X-Biblivre-Schema", "other");

        assertEquals("other", SchemaUtil.extractSchema(request));
    }

    @Test
    void extractSchema_doesNotTreatSpaAsALibrarySchema() {
        MockHttpServletRequest request = requestAt("/Biblivre6", "/Biblivre6/spa/search");

        assertNull(SchemaUtil.extractSchema(request));
    }

    @Test
    void isForbiddenPathForBoundSchema_whenPathNamesAnotherSchema() {
        assertTrue(SchemaUtil.isForbiddenPathForBoundSchema("temp", "other"));
        assertFalse(SchemaUtil.isForbiddenPathForBoundSchema("temp", "temp"));
        assertFalse(SchemaUtil.isForbiddenPathForBoundSchema("temp", null));
    }

    @Test
    void visibleSchemas_whenBound_returnsOnlyThatSchema() {
        SchemaDTO temp = new SchemaDTO("temp", "Temp");
        SchemaDTO other = new SchemaDTO("other", "Other");

        assertEquals(Set.of(temp), SchemaUtil.visibleSchemas("temp", Set.of(temp, other)));
    }

    @Test
    void visibleSchemas_whenUnbound_returnsAll() {
        SchemaDTO temp = new SchemaDTO("temp", "Temp");
        SchemaDTO other = new SchemaDTO("other", "Other");

        assertEquals(Set.of(temp, other), SchemaUtil.visibleSchemas(null, Set.of(temp, other)));
    }

    private static MockHttpServletRequest requestAt(String contextPath, String requestURI) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath(contextPath);
        request.setRequestURI(requestURI);
        return request;
    }
}
