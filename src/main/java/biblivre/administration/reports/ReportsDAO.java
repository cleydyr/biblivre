package biblivre.administration.reports;

import biblivre.administration.reports.dto.AllUsersReportDto;
import biblivre.administration.reports.dto.AssetHoldingByDateDto;
import biblivre.administration.reports.dto.AssetHoldingDto;
import biblivre.administration.reports.dto.BibliographyReportDto;
import biblivre.administration.reports.dto.DeweyReportDto;
import biblivre.administration.reports.dto.HoldingCreationByDateReportDto;
import biblivre.administration.reports.dto.LateLendingsDto;
import biblivre.administration.reports.dto.LendingsByDateReportDto;
import biblivre.administration.reports.dto.RequestsByDateReportDto;
import biblivre.administration.reports.dto.ReservationReportDto;
import biblivre.administration.reports.dto.SearchesByDateReportDto;
import biblivre.administration.reports.dto.SummaryReportDto;
import biblivre.cataloging.enums.RecordDatabase;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.TreeMap;

public interface ReportsDAO {

    DateFormat dd_MM_yyyy = new SimpleDateFormat("dd/MM/yyyy");

    SummaryReportDto getSummaryReportData(RecordDatabase database);

    DeweyReportDto getDeweyReportData(RecordDatabase db, String datafield, int digits);

    AssetHoldingDto getAssetHoldingReportData();

    AssetHoldingDto getAssetHoldingFullReportData();

    AssetHoldingByDateDto getAssetHoldingByDateReportData(String initialDate, String finalDate);

    HoldingCreationByDateReportDto getHoldingCreationByDateReportData(
            String initialDate, String finalDate);

    LendingsByDateReportDto getLendingsByDateReportData(String initialDate, String finalDate);

    LateLendingsDto getLateReturnLendingsReportData();

    SearchesByDateReportDto getSearchesByDateReportData(String initialDate, String finalDate);

    AllUsersReportDto getAllUsersReportData();

    RequestsByDateReportDto getRequestsByDateReportData(String initialDate, String finalDate);

    TreeMap<String, Set<Integer>> searchAuthors(String authorName, RecordDatabase database);

    BibliographyReportDto getBibliographyReportData(String authorName, Integer[] recordIdArray);

    ReservationReportDto getReservationReportData();
}
