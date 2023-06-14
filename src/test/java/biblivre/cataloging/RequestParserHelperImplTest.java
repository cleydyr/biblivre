package biblivre.cataloging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import biblivre.core.RequestParserHelper;
import biblivre.core.RequestParserHelperImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RequestParserHelperImplTest {
    @Test
    public void testIsMustRedirectToSchema() {
        RequestParserHelper requestParserHelper = new RequestParserHelperImpl();

        assertFalse(requestParserHelper.isMustRedirectToSchema(""));
    }

    @Test
    public void testParseController() {
        RequestParserHelper requestParserHelper = new RequestParserHelperImpl();

        assertEquals(requestParserHelper.parseController("", ""), "");

        assertEquals(
                requestParserHelper.parseController("DigitalMediaController", ""),
                "DigitalMediaController");

        Assertions.assertNotEquals(requestParserHelper.parseController("foobar", ""), "foobar");
    }
}
