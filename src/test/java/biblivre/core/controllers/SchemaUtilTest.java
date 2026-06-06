package biblivre.core.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import biblivre.core.utils.Constants;
import org.junit.jupiter.api.Test;

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
}
