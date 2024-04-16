package biblivre.cataloging.holding;

import static org.junit.jupiter.api.Assertions.assertEquals;

import biblivre.AbstractContainerDatabaseTest;
import biblivre.TestDatasourceConfiguration;
import biblivre.core.*;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

// There's no shame in ugly testing. Only in not testing.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@Import({TestDatasourceConfiguration.class})
@ActiveProfiles("test")
class HoldingDAOImplTest extends AbstractContainerDatabaseTest {
    @Autowired biblivre.cataloging.bibliographic.Handler bibliographicHandler;

    @Autowired biblivre.cataloging.holding.Handler holdingHandler;

    @Test
    void countSearchResults() throws IOException {
        // Setup
        SchemaThreadLocal.setSchema("single");

        Supplier<JSONObject> jsonPayloadGenerator = handlerJsonPayloadGenerator();

        // Given that I have a holding with a creation date of 2023-04-12
        String bibliographicRecordCreationFormData =
                "action=save&oldId=+&id=+&from=form&data=%7B%7D&material_type=book&database=main";

        ExtendedRequest request1 = getFormDataMockRequest(bibliographicRecordCreationFormData);

        JSONObject bibliographicRecordCreationResponseBody = jsonPayloadGenerator.get();

        bibliographicHandler.save(request1, Mockito.mock(ExtendedResponse.class));

        int recordId = bibliographicRecordCreationResponseBody.getJSONObject("data").getInt("id");

        String automaticHoldingCreationFormData =
                STR."holding_count=1&holding_volume_number=&holding_volume_count=&holding_acquisition_date=&holding_library=&holding_acquisition_type=&record_id=\{
                        recordId}&database=main";

        ExtendedRequest automaticHoldingCreationRequest =
                getFormDataMockRequest(automaticHoldingCreationFormData);

        holdingHandler.createAutomaticHolding(
                automaticHoldingCreationRequest, Mockito.mock(ExtendedResponse.class));

        // When I search for holdings created between yesterday and tomorrow
        LocalDate yesterday = LocalDate.now().minusDays(1);

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        String holdingSearchFormData =
                STR."search_parameters=%7B%22database%22%3A%22main%22%2C%22material_type%22%3A%22all%22%2C%22search_mode%22%3A%22advanced%22%2C%22holding_search%22%3Atrue%2C%22search_terms%22%3A%5B%7B%22field%22%3A%22holding_created%22%2C%22operator%22%3A%22AND%22%2C%22start_date%22%3A%22\{
                        yesterday}T00%3A00%3A00%22%2C%22end_date%22%3A%22\{
                        tomorrow}T00%3A00%3A00%22%7D%2C%7B%22query%22%3A%22true%22%2C%22field%22%3A%22holding_label_never_printed%22%2C%22operator%22%3A%22AND%22%7D%5D%7D";

        ExtendedRequest holdingSearchRequest = getFormDataMockRequest(holdingSearchFormData);

        JSONObject holdingSearchResponseBody = jsonPayloadGenerator.get();

        bibliographicHandler.search(holdingSearchRequest, Mockito.mock(ExtendedResponse.class));

        // Then I should get a count of one on the search results
        assertEquals(1, holdingSearchResponseBody.getJSONObject("search").getInt("record_count"));
    }

    public static @NotNull Supplier<JSONObject> handlerJsonPayloadGenerator() {
        HandlerContext context = Mockito.mock(HandlerContext.class);

        HandlerContextThreadLocal.setHandlerContext(context);

        return () -> {
            JSONObject jsonObject = new JSONObject();

            Mockito.when(context.getJson()).thenReturn(jsonObject);

            return jsonObject;
        };
    }

    private static @NotNull ExtendedRequest getFormDataMockRequest(
            String bibliographicRecordCreationFormData) {
        return mockExtendedRequest(parseFormData(bibliographicRecordCreationFormData));
    }

    private static Map<String, String> parseFormData(String formData2) {
        Map<String, String> parameters = new HashMap<>();

        for (String pair : formData2.split("&")) {
            String[] keyValue = pair.split("=", -1);

            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
            String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);

            parameters.put(key, value);
        }

        return parameters;
    }

    private static @NotNull ExtendedRequest mockExtendedRequest(Map<String, String> parameters) {
        ExtendedRequest request = Mockito.mock(ExtendedRequest.class);

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            Mockito.when(request.getRequestParameter(entry.getKey())).thenReturn(entry.getValue());
        }

        Mockito.when(request.getInteger(Mockito.anyString())).thenCallRealMethod();
        Mockito.when(request.getInteger(Mockito.anyString(), Mockito.any())).thenCallRealMethod();
        Mockito.when(request.getString(Mockito.anyString())).thenCallRealMethod();
        Mockito.when(request.getString(Mockito.anyString(), Mockito.anyString()))
                .thenCallRealMethod();
        Mockito.when(request.getEnum(Mockito.any(), Mockito.anyString())).thenCallRealMethod();
        Mockito.when(request.getEnum(Mockito.any(Class.class), Mockito.anyString(), Mockito.any()))
                .thenCallRealMethod();
        Mockito.when(request.getLoggedUserId()).thenReturn(1);

        return request;
    }
}
