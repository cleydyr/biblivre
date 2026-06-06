package biblivre.cataloging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import biblivre.administration.indexing.IndexingGroupBO;
import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.cataloging.bibliographic.PaginableRecordBO;
import biblivre.cataloging.enums.RecordType;
import biblivre.cataloging.search.SearchDTO;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.HandlerContext;
import biblivre.core.HandlerContextThreadLocal;
import biblivre.core.PagingDTO;
import biblivre.core.auth.AuthorizationPoints;
import biblivre.core.enums.ActionResult;
import biblivre.core.file.DiskFile;
import biblivre.marc.MaterialType;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class PaginableCatalogingHandlerTest {
    private static final String SEARCH_PARAMETERS =
            """
            {"database":"main","material_type":"all","search_mode":"list_all"}
            """;

    private PaginableRecordBO paginableRecordBO;
    private SearchResultsExcelExporter searchResultsExcelExporter;
    private IndexingGroupBO indexingGroupBO;
    private TestHandler handler;
    private HandlerContext handlerContext;
    private Map<String, Object> sessionAttributes;

    @BeforeEach
    void setUp() {
        paginableRecordBO = mock(PaginableRecordBO.class);
        searchResultsExcelExporter = mock(SearchResultsExcelExporter.class);
        indexingGroupBO = mock(IndexingGroupBO.class);
        sessionAttributes = new HashMap<>();

        handler = new TestHandler(paginableRecordBO, searchResultsExcelExporter);
        handler.setIndexingGroupBO(indexingGroupBO);

        handlerContext = new HandlerContext();
        HandlerContextThreadLocal.setHandlerContext(handlerContext);

        when(paginableRecordBO.getRecordType()).thenReturn(RecordType.BIBLIO);
        when(indexingGroupBO.getDefaultSortableGroupId(RecordType.BIBLIO)).thenReturn(5);
    }

    @AfterEach
    void tearDown() {
        HandlerContextThreadLocal.remove();
    }

    @Test
    void exportSearchExcel_rejectsBlankSearchParameters() {
        ExtendedRequest request = mockRequest(Map.of("search_parameters", "   "));
        ExtendedResponse response = mock(ExtendedResponse.class);

        handler.exportSearchExcel(request, response);

        assertEquals(
                ActionResult.WARNING, handlerContext.getMessage().getLevel());
        assertEquals(
                "cataloging.error.invalid_search_parameters",
                handlerContext.getMessage().getText());
        assertNull(handlerContext.getJson().opt("uuid"));
    }

    @Test
    void exportSearchExcel_storesPayloadInSessionAndReturnsUuid() {
        ExtendedRequest request =
                mockRequest(
                        Map.of(
                                "search_parameters",
                                SEARCH_PARAMETERS,
                                "sort",
                                "3",
                                "indexing_group",
                                "7"));
        ExtendedResponse response = mock(ExtendedResponse.class);

        handler.exportSearchExcel(request, response);

        String exportId = handlerContext.getJson().getString("uuid");
        assertNotNull(exportId);

        JSONObject payload = new JSONObject((String) sessionAttributes.get(exportId));
        assertEquals(SEARCH_PARAMETERS, payload.getString("search_parameters"));
        assertEquals(3, payload.getInt("sort"));
        assertEquals(7, payload.getInt("indexing_group"));
    }

    @Test
    void exportSearchExcel_usesDefaultSortWhenNotProvided() {
        ExtendedRequest request =
                mockRequest(Map.of("search_parameters", SEARCH_PARAMETERS));
        ExtendedResponse response = mock(ExtendedResponse.class);

        handler.exportSearchExcel(request, response);

        String exportId = handlerContext.getJson().getString("uuid");
        JSONObject payload = new JSONObject((String) sessionAttributes.get(exportId));

        assertEquals(5, payload.getInt("sort"));
        assertEquals(0, payload.getInt("indexing_group"));
    }

    @Test
    void downloadSearchExcel_rejectsMissingSessionPayload() {
        ExtendedRequest request = mockRequest(Map.of("id", "missing-export-id"));
        ExtendedResponse response = mock(ExtendedResponse.class);

        handler.downloadSearchExcel(request, response);

        assertEquals(ActionResult.WARNING, handlerContext.getMessage().getLevel());
        assertEquals(
                "cataloging.error.no_records_found", handlerContext.getMessage().getText());
        assertNull(handlerContext.getFile());
    }

    @Test
    void downloadSearchExcel_rejectsEmptySearchResults() {
        String exportId = "export-1";
        sessionAttributes.put(
                exportId,
                new JSONObject()
                        .put("search_parameters", SEARCH_PARAMETERS)
                        .put("sort", 5)
                        .put("indexing_group", 0)
                        .toString());

        ExtendedRequest request = mockRequest(Map.of("id", exportId));
        ExtendedResponse response = mock(ExtendedResponse.class);
        AuthorizationPoints authorizationPoints = mock(AuthorizationPoints.class);

        when(request.getAuthorizationPoints()).thenReturn(authorizationPoints);
        when(paginableRecordBO.search(any(), eq(authorizationPoints), eq(5), eq(0)))
                .thenReturn(new SearchDTO(RecordType.BIBLIO));

        handler.downloadSearchExcel(request, response);

        assertEquals(ActionResult.WARNING, handlerContext.getMessage().getLevel());
        assertEquals(
                "cataloging.error.no_records_found", handlerContext.getMessage().getText());
        assertNull(handlerContext.getFile());
        assertNull(sessionAttributes.get(exportId));
    }

    @Test
    void downloadSearchExcel_rejectsNullExportFile() throws IOException {
        String exportId = "export-2";
        sessionAttributes.put(
                exportId,
                new JSONObject()
                        .put("search_parameters", SEARCH_PARAMETERS)
                        .put("sort", 5)
                        .put("indexing_group", 0)
                        .toString());

        ExtendedRequest request = mockRequest(Map.of("id", exportId));
        ExtendedResponse response = mock(ExtendedResponse.class);
        AuthorizationPoints authorizationPoints = mock(AuthorizationPoints.class);
        SearchDTO search = searchWithRecords(1);

        when(request.getAuthorizationPoints()).thenReturn(authorizationPoints);
        when(request.getLanguage()).thenReturn("pt-BR");
        when(paginableRecordBO.search(any(), eq(authorizationPoints), eq(5), eq(0)))
                .thenReturn(search);
        when(searchResultsExcelExporter.export(any(), eq("pt-BR"))).thenReturn(null);

        handler.downloadSearchExcel(request, response);

        assertEquals(ActionResult.WARNING, handlerContext.getMessage().getLevel());
        assertEquals(
                "cataloging.error.no_records_found", handlerContext.getMessage().getText());
        assertNull(handlerContext.getFile());
    }

    @Test
    void downloadSearchExcel_setsFileAndClearsSessionOnSuccess() throws IOException {
        String exportId = "export-3";
        sessionAttributes.put(
                exportId,
                new JSONObject()
                        .put("search_parameters", SEARCH_PARAMETERS)
                        .put("sort", 5)
                        .put("indexing_group", 2)
                        .toString());

        ExtendedRequest request = mockRequest(Map.of("id", exportId));
        ExtendedResponse response = mock(ExtendedResponse.class);
        AuthorizationPoints authorizationPoints = mock(AuthorizationPoints.class);
        SearchDTO search = searchWithRecords(1);
        DiskFile exportFile =
                new DiskFile(
                        File.createTempFile("biblivre_search_export_test_", ".xlsx"),
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        when(request.getAuthorizationPoints()).thenReturn(authorizationPoints);
        when(request.getLanguage()).thenReturn("pt-BR");
        when(paginableRecordBO.search(any(), eq(authorizationPoints), eq(5), eq(2)))
                .thenReturn(search);
        when(searchResultsExcelExporter.export(any(), eq("pt-BR"))).thenReturn(exportFile);

        handler.downloadSearchExcel(request, response);

        assertNull(handlerContext.getMessage().getText());
        assertEquals(exportFile, handlerContext.getFile());
        assertNotNull(handlerContext.getCallback());
        assertNull(sessionAttributes.get(exportId));
        verify(paginableRecordBO, never()).paginateSearch(any(), any());
    }

    @Test
    void downloadSearchExcel_paginatesRemainingPages() throws IOException {
        String exportId = "export-4";
        sessionAttributes.put(
                exportId,
                new JSONObject()
                        .put("search_parameters", SEARCH_PARAMETERS)
                        .put("sort", 5)
                        .put("indexing_group", 0)
                        .toString());

        ExtendedRequest request = mockRequest(Map.of("id", exportId));
        ExtendedResponse response = mock(ExtendedResponse.class);
        AuthorizationPoints authorizationPoints = mock(AuthorizationPoints.class);
        SearchDTO search = searchWithRecords(1);
        search.getPaging().setRecordCount(25);
        search.getPaging().setRecordsPerPage(10);

        DiskFile exportFile =
                new DiskFile(
                        File.createTempFile("biblivre_search_export_test_", ".xlsx"),
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        when(request.getAuthorizationPoints()).thenReturn(authorizationPoints);
        when(request.getLanguage()).thenReturn("pt-BR");
        when(paginableRecordBO.search(any(), eq(authorizationPoints), eq(5), eq(0)))
                .thenReturn(search);
        when(searchResultsExcelExporter.export(any(), eq("pt-BR"))).thenReturn(exportFile);

        handler.downloadSearchExcel(request, response);

        ArgumentCaptor<SearchDTO> searchCaptor = ArgumentCaptor.forClass(SearchDTO.class);
        verify(paginableRecordBO, times(2))
                .paginateSearch(searchCaptor.capture(), eq(authorizationPoints));

        List<SearchDTO> paginatedSearches = searchCaptor.getAllValues();
        assertEquals(2, paginatedSearches.get(0).getPaging().getPage());
        assertEquals(3, paginatedSearches.get(1).getPaging().getPage());
        assertEquals(exportFile, handlerContext.getFile());
    }

    @Test
    void downloadSearchExcel_handlesExporterIOException() throws IOException {
        String exportId = "export-5";
        sessionAttributes.put(
                exportId,
                new JSONObject()
                        .put("search_parameters", SEARCH_PARAMETERS)
                        .put("sort", 5)
                        .put("indexing_group", 0)
                        .toString());

        ExtendedRequest request = mockRequest(Map.of("id", exportId));
        ExtendedResponse response = mock(ExtendedResponse.class);
        AuthorizationPoints authorizationPoints = mock(AuthorizationPoints.class);
        SearchDTO search = searchWithRecords(1);

        when(request.getAuthorizationPoints()).thenReturn(authorizationPoints);
        when(request.getLanguage()).thenReturn("pt-BR");
        when(paginableRecordBO.search(any(), eq(authorizationPoints), eq(5), eq(0)))
                .thenReturn(search);
        doThrow(new IOException("disk full"))
                .when(searchResultsExcelExporter)
                .export(any(), eq("pt-BR"));

        handler.downloadSearchExcel(request, response);

        assertEquals(ActionResult.ERROR, handlerContext.getMessage().getLevel());
        assertEquals("error.runtime_error", handlerContext.getMessage().getText());
        assertNull(handlerContext.getFile());
    }

    private ExtendedRequest mockRequest(Map<String, String> parameters) {
        ExtendedRequest request = mock(ExtendedRequest.class);

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            when(request.getRequestParameter(entry.getKey())).thenReturn(entry.getValue());
        }

        when(request.getInteger(any(), any())).thenCallRealMethod();
        when(request.getString(any())).thenCallRealMethod();
        when(request.getString(any(), any())).thenCallRealMethod();

        doAnswer(
                        invocation -> {
                            sessionAttributes.put(
                                    invocation.getArgument(0), invocation.getArgument(1));
                            return null;
                        })
                .when(request)
                .setScopedSessionAttribute(any(), any());

        when(request.getScopedSessionAttribute(any()))
                .thenAnswer(invocation -> sessionAttributes.get(invocation.getArgument(0)));

        return request;
    }

    private static SearchDTO searchWithRecords(int recordCount) {
        SearchDTO search = new SearchDTO(RecordType.BIBLIO);
        PagingDTO paging = new PagingDTO();
        paging.setRecordsPerPage(10);
        paging.setRecordCount(recordCount);
        search.setPaging(paging);

        for (int i = 0; i < recordCount; i++) {
            search.add(new BiblioRecordDTO());
        }

        return search;
    }

    private static final class TestHandler extends PaginableCatalogingHandler {
        TestHandler(
                PaginableRecordBO paginableRecordBO,
                SearchResultsExcelExporter searchResultsExcelExporter) {
            super(paginableRecordBO, MaterialType.BOOK, searchResultsExcelExporter);
        }

        @Override
        protected RecordDTO createRecordDTO(ExtendedRequest request) {
            return new BiblioRecordDTO();
        }
    }
}
