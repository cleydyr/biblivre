package biblivre.core;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import biblivre.core.translations.LanguageBO;
import biblivre.core.translations.TranslationBO;
import biblivre.core.translations.TranslationsMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class ExtendedRequestTest {

    @AfterEach
    void tearDown() {
        SchemaThreadLocal.remove();
    }

    @Test
    void constructor_doesNotLoadTranslationsWhenSchemaIsUnset() throws Exception {
        SchemaThreadLocal.remove();
        TranslationBO translationBO = mock(TranslationBO.class);
        LanguageBO languageBO = mock(LanguageBO.class);
        MockHttpServletRequest request = requestAt("/Biblivre6/wp-config.php");

        ExtendedRequest extendedRequest =
                new ExtendedRequest(
                        request, mock(RequestParserHelper.class), languageBO, translationBO);

        verify(translationBO, never()).get(anyString());
        verify(languageBO, never()).getDefaultLanguage();
        assertNull(extendedRequest.getTranslationsMap());
    }

    @Test
    void constructor_loadsTranslationsWhenSchemaIsSet() throws Exception {
        SchemaThreadLocal.setSchema("library");
        TranslationBO translationBO = mock(TranslationBO.class);
        LanguageBO languageBO = mock(LanguageBO.class);
        when(languageBO.isNotLoaded(nullable(String.class))).thenReturn(true);
        when(languageBO.getDefaultLanguage()).thenReturn("pt-BR");
        when(translationBO.get("pt-BR"))
                .thenReturn(new TranslationsMap("library", "pt-BR", 1, null));
        RequestParserHelper requestParserHelper = mock(RequestParserHelper.class);
        when(requestParserHelper.parseController(anyString(), anyString())).thenReturn("jsp");
        when(requestParserHelper.isMustRedirectToSchema(anyString())).thenReturn(false);

        ExtendedRequest extendedRequest =
                new ExtendedRequest(
                        requestAt("/Biblivre6/library/"),
                        requestParserHelper,
                        languageBO,
                        translationBO);

        verify(translationBO).get("pt-BR");
        assertNotNull(extendedRequest.getTranslationsMap());
    }

    private static MockHttpServletRequest requestAt(String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath("/Biblivre6");
        request.setRequestURI(uri);
        request.setServletPath(uri.substring("/Biblivre6".length()));
        return request;
    }
}
