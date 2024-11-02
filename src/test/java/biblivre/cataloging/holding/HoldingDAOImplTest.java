package biblivre.cataloging.holding;

import static org.junit.jupiter.api.Assertions.assertEquals;

import biblivre.AbstractContainerDatabaseTest;
import biblivre.TestDatasourceConfiguration;
import biblivre.cataloging.enums.RecordType;
import biblivre.core.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Autowired biblivre.cataloging.holding.HoldingDAOImpl holdingDAO;

    @Autowired
    @Qualifier("recordDAO")
    biblivre.cataloging.RecordDAOImpl bibliographicDAO;

    @AfterEach
    void setUp() {
        holdingDAO.list(0, Integer.MAX_VALUE).forEach(holdingDAO::delete);

        bibliographicDAO
                .list(0, Integer.MAX_VALUE, RecordType.BIBLIO)
                .forEach(bibliographicDAO::delete);
    }

    @Test
    void countSearchResults() {
        // Setup
        SchemaThreadLocal.setSchema("single");

        Supplier<JSONObject> jsonPayloadGenerator = handlerJsonPayloadGenerator();

        // Given that I have a holding with a creation date of 2023-04-12
        String bibliographicRecordCreationFormData =
                "oldId=+&id=+&from=form&data=%7B%7D&material_type=book&database=main";

        ExtendedRequest request1 = getFormDataMockRequest(bibliographicRecordCreationFormData);

        JSONObject bibliographicRecordCreationResponseBody = jsonPayloadGenerator.get();

        bibliographicHandler.save(request1, Mockito.mock(ExtendedResponse.class));

        int recordId = bibliographicRecordCreationResponseBody.getJSONObject("data").getInt("id");

        String automaticHoldingCreationFormData =
                "holding_count=1&holding_volume_number=&holding_volume_count=&holding_acquisition_date=&holding_library=&holding_acquisition_type=&record_id=%s&database=main"
                        .formatted(recordId);

        ExtendedRequest automaticHoldingCreationRequest =
                getFormDataMockRequest(automaticHoldingCreationFormData);

        holdingHandler.createAutomaticHolding(
                automaticHoldingCreationRequest, Mockito.mock(ExtendedResponse.class));

        // When I search for holdings created between yesterday and tomorrow
        LocalDate yesterday = LocalDate.now().minusDays(1);

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        String holdingSearchFormData =
                "search_parameters=%s."
                        .formatted(
                                encodeToURL(
                                                """
                            {
                            "database": "main",
                            "material_type": "all",
                            "search_mode": "advanced",
                            "holding_search": true,
                            "search_terms": [
                              {
                                "field": "holding_created",
                                "operator": "AND",
                                "start_date": "%sT00:00:00",
                                "end_date": "%sT00:00:00"
                              },
                              {
                                "query": "true",
                                "field": "holding_label_never_printed",
                                "operator": "AND"
                              }
                            ]
                          }
                          """
                                                .formatted(yesterday, tomorrow)));

        ExtendedRequest holdingSearchRequest = getFormDataMockRequest(holdingSearchFormData);

        JSONObject holdingSearchResponseBody = jsonPayloadGenerator.get();

        bibliographicHandler.search(holdingSearchRequest, Mockito.mock(ExtendedResponse.class));

        // Then I should get a count of one on the search results
        assertEquals(1, holdingSearchResponseBody.getJSONObject("search").getInt("record_count"));
    }

    @Test
    void save() {
        // Setup
        SchemaThreadLocal.setSchema("single");

        Supplier<JSONObject> jsonPayloadGenerator = handlerJsonPayloadGenerator();

        // Given that I create a bibliographic record
        String bibliographicRecordCreationFormData =
                "oldId=+&id=+&from=form&data=%7B%7D&material_type=book&database=main";

        ExtendedRequest request1 = getFormDataMockRequest(bibliographicRecordCreationFormData);

        JSONObject bibliographicRecordCreationResponseBody = jsonPayloadGenerator.get();

        bibliographicHandler.save(request1, Mockito.mock(ExtendedResponse.class));

        int recordId = bibliographicRecordCreationResponseBody.getJSONObject("data").getInt("id");

        // When I create a holding for the bibliographic record

        String saveHoldingFormData =
                "oldId=+&id=+&record_id=%s&availability=available&from=holding_form&data=%%7B%%7D"
                        .formatted(recordId);

        ExtendedRequest saveHoldingRequest = getFormDataMockRequest(saveHoldingFormData);

        holdingHandler.save(saveHoldingRequest, Mockito.mock(ExtendedResponse.class));

        // When I search for holdings created between yesterday and tomorrow
        LocalDate yesterday = LocalDate.now().minusDays(1);

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        String holdingSearchFormData =
                        """
                search_parameters=%s
                """
                        .formatted(
                                encodeToURL(
                                                """
                                            {
                                            "database": "main",
                                            "material_type": "all",
                                            "search_mode": "advanced",
                                            "holding_search": true,
                                            "search_terms": [
                                              {
                                                "field": "holding_created",
                                                "operator": "AND",
                                                "start_date": "%sT00:00:00",
                                                "end_date": "%sT00:00:00"
                                              },
                                              {
                                                "query": "true",
                                                "field": "holding_label_never_printed",
                                                "operator": "AND"
                                              }
                                            ]
                                          }
                      """
                                                .formatted(yesterday, tomorrow)));

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

    private static String encodeToURL(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
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
