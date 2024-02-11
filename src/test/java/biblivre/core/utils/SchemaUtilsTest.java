package biblivre.core.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SchemaUtilsTest {

    @Test
    void buildSchemasMapFromFormattedTitleAndSubtitle() {
        String schemasJson =
                "{\"schema1\":\"<title1,subtitle1>\",\"schema2\":\"<title2,subtitle2>\"}";
        var result = SchemaUtils.buildSchemasMap(schemasJson);
        assertEquals(2, result.size());
        assertEquals("title1", result.get("schema1").getLeft());
        assertEquals("subtitle1", result.get("schema1").getRight());
        assertEquals("title2", result.get("schema2").getLeft());
        assertEquals("subtitle2", result.get("schema2").getRight());
    }

    @Test
    void buildSchemasMapFromJSONObject() {
        String schemasJson =
                "{\"schema1\":{\"left\":\"title1\",\"right\":\"subtitle1\"},\"schema2\":{\"left\":\"title2\",\"right\":\"subtitle2\"}}";
        var result = SchemaUtils.buildSchemasMap(schemasJson);
        assertEquals(2, result.size());
        assertEquals("title1", result.get("schema1").getLeft());
        assertEquals("subtitle1", result.get("schema1").getRight());
        assertEquals("title2", result.get("schema2").getLeft());
        assertEquals("subtitle2", result.get("schema2").getRight());
    }
}
