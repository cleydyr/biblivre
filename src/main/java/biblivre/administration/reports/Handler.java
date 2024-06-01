/*******************************************************************************
 * Este arquivo é parte do Biblivre5.
 *
 * Biblivre5 é um software livre; você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da Licença Pública Geral GNU como
 * publicada pela Fundação do Software Livre (FSF); na versão 3 da
 * Licença, ou (caso queira) qualquer versão posterior.
 *
 * Este programa é distribuído na esperança de que possa ser  útil,
 * mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 * MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
 * Licença Pública Geral GNU para maiores detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 * com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
 *
 * @author Alberto Wagner <alberto@biblivre.org.br>
 * @author Danniel Willian <danniel@biblivre.org.br>
 ******************************************************************************/
package biblivre.administration.reports;

import biblivre.cataloging.enums.RecordDatabase;
import biblivre.circulation.lending.LendingListDTO;
import biblivre.circulation.user.PagedUserSearchWebHelper;
import biblivre.circulation.user.UserBO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractHandler;
import biblivre.core.DTOCollection;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.enums.ActionResult;
import biblivre.core.file.DiskFile;
import biblivre.core.utils.TextUtils;
import biblivre.search.SearchException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Handler extends AbstractHandler {
    private ReportsBO reportsBO;
    @Autowired private PagedUserSearchWebHelper pagedUserSearchWebHelper;
    @Autowired private UserBO userBO;

    public void userSearch(ExtendedRequest request, ExtendedResponse response) {
        try {
            var pagedSearchDTO = pagedUserSearchWebHelper.getPagedUserSearchDTO(request);

            DTOCollection<UserDTO> userList = userBO.search(pagedSearchDTO);

            if (userList.isEmpty()) {
                this.setMessage(ActionResult.WARNING, "circulation.error.no_users_found");
                return;
            }

            DTOCollection<LendingListDTO> list = new DTOCollection<>();
            list.setPaging(userList.getPaging());

            put("search", list.toJSONObject());
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        } catch (SearchException e) {
            this.setMessage(ActionResult.ERROR, "error.internal_error");
        }
    }

    public void generate(ExtendedRequest request, ExtendedResponse response) {
        ReportsDTO dto;

        try {
            dto = this.populateDto(request);
        } catch (Exception e) {
            this.setMessage(ActionResult.WARNING, "error.invalid_parameters");
            return;
        }

        DiskFile report = reportsBO.generateReport(dto, request.getTranslationsMap());

        if (report != null) {
            request.setScopedSessionAttribute(report.getName(), report);
            this.setMessage(ActionResult.SUCCESS, "administration.reports.success.generate");
        } else {
            this.setMessage(ActionResult.WARNING, "administration.reports.error.generate");
        }

        try {
            if (report != null) {
                put("file_name", report.getName());
            }
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        }
    }

    // http://localhost:8080/Biblivre5/?controller=download&module=cataloging.export&action=download_report&file_name={export_id}
    public void downloadReport(ExtendedRequest request, ExtendedResponse response) {

        String report_name = request.getString("file_name");

        final DiskFile report = (DiskFile) request.getScopedSessionAttribute(report_name);

        this.setFile(report);

        this.setCallback(report::delete);
    }

    private ReportsDTO populateDto(ExtendedRequest request) throws Exception {
        ReportsDTO dto = new ReportsDTO();

        String reportId = request.getString("report");
        ReportType type = ReportType.getById(reportId);
        dto.setType(type);

        if (type != null && type.isTimePeriod()) {
            dto.setInitialDate(TextUtils.parseDate(request.getString("start")));
            dto.setFinalDate(TextUtils.parseDate(request.getString("end")));
        }

        dto.setDatabase(RecordDatabase.fromString(request.getString("database")));
        dto.setOrder(request.getString("order"));
        dto.setCountOrder(request.getString("count_order"));
        dto.setSearchId(request.getInteger("search_id"));
        dto.setMarcField(request.getString("marc_field"));
        dto.setUserId(request.getString("user_id"));
        dto.setRecordIds(request.getString("recordIds"));
        dto.setAuthorName(request.getString("authorName"));
        dto.setDatafield(request.getString("datafield"));
        dto.setDigits(request.getInteger("digits"));

        return dto;
    }

    @Autowired
    public void setReportsBO(ReportsBO reportsBO) {
        this.reportsBO = reportsBO;
    }
}
