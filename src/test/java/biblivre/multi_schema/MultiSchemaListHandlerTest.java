package biblivre.multi_schema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.HandlerContext;
import biblivre.core.HandlerContextThreadLocal;
import biblivre.core.configurations.ConfigurationBO;
import biblivre.core.controllers.SchemaUtil;
import biblivre.core.schemas.SchemaBO;
import biblivre.core.schemas.SchemaDTO;
import biblivre.core.utils.Constants;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONArray;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MultiSchemaListHandlerTest {
    private Handler handler;
    private SchemaBO schemaBO;
    private ConfigurationBO configurationBO;
    private HandlerContext handlerContext;

    @BeforeEach
    void setUp() {
        schemaBO = mock(SchemaBO.class);
        configurationBO = mock(ConfigurationBO.class);
        handler = new Handler();
        handler.setSchemaBO(schemaBO);
        handler.setConfigurationBO(configurationBO);

        handlerContext = new HandlerContext();
        HandlerContextThreadLocal.setHandlerContext(handlerContext);

        when(configurationBO.getString(Constants.CONFIG_SUBTITLE)).thenReturn("");
        when(schemaBO.getSchemas())
                .thenReturn(
                        Set.of(
                                new SchemaDTO("temp", "Temp Library"),
                                new SchemaDTO("other", "Other Library")));
    }

    @AfterEach
    void tearDown() {
        HandlerContextThreadLocal.remove();
    }

    @Test
    void list_whenUnbound_returnsEveryEnabledLibrary() {
        ExtendedRequest request = mock(ExtendedRequest.class);

        handler.list(request, mock(ExtendedResponse.class));

        assertEquals(Set.of("temp", "other"), listedSchemas());
        assertFalse(handlerContext.getJson().getBoolean("bound"));
    }

    @Test
    void list_whenBound_returnsOnlyTheBoundLibrary() {
        ExtendedRequest request = mock(ExtendedRequest.class);
        when(request.getHeader(SchemaUtil.BOUND_SCHEMA_HEADER)).thenReturn("temp");

        handler.list(request, mock(ExtendedResponse.class));

        assertEquals(Set.of("temp"), listedSchemas());
        assertTrue(handlerContext.getJson().getBoolean("bound"));
    }

    private Set<String> listedSchemas() {
        JSONArray data = handlerContext.getJson().getJSONArray("data");
        Set<String> schemas = new HashSet<>();
        for (int i = 0; i < data.length(); i++) {
            schemas.add(data.getJSONObject(i).getString("schema"));
        }
        return schemas;
    }
}
