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

import biblivre.administration.reports.dto.BaseReportDto;
import biblivre.administration.reports.dto.UserReportDto;
import biblivre.administration.usertype.UserTypeBO;
import biblivre.administration.usertype.UserTypeDTO;
import biblivre.circulation.lending.LendingBO;
import biblivre.circulation.lending.LendingDTO;
import biblivre.circulation.lending.LendingFineBO;
import biblivre.circulation.lending.LendingInfoDTO;
import biblivre.circulation.user.UserBO;
import biblivre.circulation.user.UserDTO;
import biblivre.circulation.user.UserFieldBO;
import biblivre.circulation.user.UserFieldDTO;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserReport extends BaseBiblivreReport {
    public static final DateFormat dd_MM_yyyy = new SimpleDateFormat("dd/MM/yyyy");
    private UserBO userBO;
    private LendingBO lendingBO;
    private LendingFineBO lendingFineBO;
    private UserTypeBO userTypeBO;
    private UserFieldBO userFieldBO;

    @Override
    protected BaseReportDto getReportData(ReportsDTO dto) {
        UserReportDto urdto = new UserReportDto();
        int userId = Integer.parseInt(dto.getUserId());

        UserDTO user = userBO.get(userId);
        urdto.setUser(user);

        Collection<LendingDTO> history = lendingBO.listHistory(user);

        Collection<LendingInfoDTO> historyInfo = lendingBO.populateLendingInfo(history);
        List<String[]> returnedLendings = new ArrayList<>();
        for (LendingInfoDTO lidto : historyInfo) {
            String[] data = new String[3];
            data[0] = dd_MM_yyyy.format(lidto.getLending().getCreated());
            data[1] = lidto.getBiblio().getTitle();
            data[2] = lidto.getBiblio().getAuthor();
            returnedLendings.add(data);
        }
        urdto.setReturnedLendings(returnedLendings);

        List<String[]> currentLendings = new ArrayList<>();
        List<String[]> lateLendings = new ArrayList<>();

        List<LendingDTO> currentLendingsList = lendingBO.listUserLendings(user);
        Collection<LendingInfoDTO> currentLendingsInfo =
                lendingBO.populateLendingInfo(currentLendingsList);
        for (LendingInfoDTO lidto : currentLendingsInfo) {
            String[] data = new String[3];
            data[0] = dd_MM_yyyy.format(lidto.getLending().getCreated());
            data[1] = lidto.getBiblio().getTitle();
            data[2] = lidto.getBiblio().getAuthor();
            if (lendingFineBO.isLateReturn(lidto.getLending())) {
                lateLendings.add(data);
            } else {
                currentLendings.add(data);
            }
        }
        urdto.setLendings(currentLendings);
        urdto.setLateLendings(lateLendings);

        return urdto;
    }

    @Override
    protected void generateReportBody(Document document, BaseReportDto reportData) {
        UserReportDto dto = (UserReportDto) reportData;
        Paragraph p1 = new Paragraph(getText("administration.reports.title.user"));
        p1.setAlignment(Element.ALIGN_CENTER);
        document.add(p1);
        document.add(new Phrase("\n"));

        PdfPTable dataTable = createUserDataTable(dto.getUser());
        document.add(dataTable);

        Paragraph p2 = new Paragraph(getText("administration.reports.field.user_data"));
        p2.setAlignment(Element.ALIGN_CENTER);
        document.add(p2);
        document.add(new Phrase("\n"));
        document.add(createDateTable(dto.getUser()));

        Paragraph p3;
        if (dto.getLendings() != null && dto.getLendings().size() > 0) {
            document.add(new Phrase("\n"));
            p3 = new Paragraph(getText("administration.reports.field.user_lendings"));
            p3.setAlignment(Element.ALIGN_CENTER);
            document.add(p3);
            document.add(new Phrase("\n"));
            document.add(createLendingsTable(dto.getLendings()));
        }

        if (dto.getLateLendings() != null && dto.getLateLendings().size() > 0) {
            document.add(new Phrase("\n"));
            p3 = new Paragraph(getText("administration.reports.field.user_late_lendings"));
            p3.setAlignment(Element.ALIGN_CENTER);
            document.add(p3);
            document.add(new Phrase("\n"));
            document.add(createLendingsTable(dto.getLateLendings()));
        }

        if (dto.getReturnedLendings() != null && dto.getReturnedLendings().size() > 0) {
            document.add(new Phrase("\n"));
            p3 = new Paragraph(getText("administration.reports.field.user_returned_lendings"));
            p3.setAlignment(Element.ALIGN_CENTER);
            document.add(p3);
            document.add(new Phrase("\n"));
            document.add(createLendingsTable(dto.getReturnedLendings()));
        }
    }

    private PdfPTable createLendingsTable(List<String[]> lendings) {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100f);

        PdfPCell cell;
        cell =
                new PdfPCell(
                        new Paragraph(
                                getHeaderChunk(getText("administration.reports.field.date"))));
        cell.setBackgroundColor(HEADER_BG_COLOR);
        cell.setBorderWidth(HEADER_BORDER_WIDTH);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
        cell =
                new PdfPCell(
                        new Paragraph(
                                getHeaderChunk(getText("administration.reports.field.title"))));
        cell.setBackgroundColor(HEADER_BG_COLOR);
        cell.setColspan(2);
        cell.setBorderWidth(HEADER_BORDER_WIDTH);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
        cell =
                new PdfPCell(
                        new Paragraph(
                                getHeaderChunk(getText("administration.reports.field.author"))));
        cell.setBackgroundColor(HEADER_BG_COLOR);
        cell.setColspan(2);
        cell.setBorderWidth(HEADER_BORDER_WIDTH);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);

        for (String[] lending : lendings) {
            cell = new PdfPCell(new Paragraph(getNormalChunk(lending[0])));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(getNormalChunk(lending[1])));
            cell.setColspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(getNormalChunk(lending[2])));
            cell.setColspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
        }
        return table;
    }

    private PdfPTable createUserDataTable(UserDTO user) {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100f);
        PdfPCell cell =
                new PdfPCell(
                        new Paragraph(
                                getHeaderChunk(getText("administration.reports.field.user_name"))));
        cell.setBackgroundColor(HEADER_BG_COLOR);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(getNormalChunk(user.getName())));
        cell.setColspan(3);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);

        cell =
                new PdfPCell(
                        new Paragraph(
                                getHeaderChunk(getText("administration.reports.field.user_id"))));
        cell.setBackgroundColor(HEADER_BG_COLOR);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(getNormalChunk(String.valueOf(user.getId()))));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);

        cell =
                new PdfPCell(
                        new Paragraph(
                                getHeaderChunk(
                                        getText("administration.reports.field.user_signup"))));
        cell.setBackgroundColor(HEADER_BG_COLOR);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
        Date signupDate = user.getCreated();
        cell =
                new PdfPCell(
                        new Paragraph(
                                getNormalChunk(
                                        signupDate != null ? dateFormat.format(signupDate) : "")));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);

        cell =
                new PdfPCell(
                        new Paragraph(
                                getHeaderChunk(getText("administration.reports.field.user_type"))));
        cell.setBackgroundColor(HEADER_BG_COLOR);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
        UserTypeDTO usdto = userTypeBO.get(user.getType());
        cell = new PdfPCell(new Paragraph(getNormalChunk(usdto.getDescription())));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);

        cell =
                new PdfPCell(
                        new Paragraph(
                                getHeaderChunk(
                                        getText("administration.reports.field.user_status"))));
        cell.setBackgroundColor(HEADER_BG_COLOR);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
        cell =
                new PdfPCell(
                        new Paragraph(
                                getNormalChunk(
                                        getText(
                                                "administration.reports.field.user_status."
                                                        + user.getStatus().toString()))));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);

        return table;
    }

    private PdfPTable createDateTable(UserDTO user) {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100f);
        PdfPCell cell;

        for (UserFieldDTO field : userFieldBO.getFields()) {
            String fieldKey = field.getKey();
            cell =
                    new PdfPCell(
                            new Paragraph(
                                    getHeaderChunk(
                                            getText("circulation.custom.user_field." + fieldKey))));
            cell.setBackgroundColor(HEADER_BG_COLOR);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(getNormalChunk(user.getFields().get(fieldKey))));
            cell.setColspan(3);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
        }

        return table;
    }

    @Autowired
    public void setUserBO(UserBO userBO) {
        this.userBO = userBO;
    }

    @Autowired
    public void setLendingBO(LendingBO lendingBO) {
        this.lendingBO = lendingBO;
    }

    @Autowired
    public void setLendingFineBO(LendingFineBO lendingFineBO) {
        this.lendingFineBO = lendingFineBO;
    }

    @Autowired
    public void setUserTypeBO(UserTypeBO userTypeBO) {
        this.userTypeBO = userTypeBO;
    }

    @Autowired
    public void setUserFieldBO(UserFieldBO userFieldBO) {
        this.userFieldBO = userFieldBO;
    }

    @Override
    public ReportType getReportType() {
        return ReportType.USER;
    }
}
