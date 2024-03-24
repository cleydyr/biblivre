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
import biblivre.administration.reports.dto.LendingsByDateReportDto;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LendingsByDateReport extends BaseBiblivreReport {

    private ReportsDAO reportsDAO;

    @Override
    protected BaseReportDto getReportData(ReportsDTO dto) {
        ReportsDAO dao = reportsDAO;
        String initialDate = dateFormat.format(dto.getInitialDate());
        String finalDate = dateFormat.format(dto.getFinalDate());
        return dao.getLendingsByDateReportData(initialDate, finalDate);
    }

    @Override
    protected void generateReportBody(Document document, BaseReportDto reportData) {
        LendingsByDateReportDto dto = (LendingsByDateReportDto) reportData;
        Paragraph p1 = new Paragraph(getText("administration.reports.title.lendings_by_date"));
        p1.setAlignment(Element.ALIGN_CENTER);
        document.add(p1);
        document.add(new Phrase("\n"));
        String header =
                getText("administration.reports.field.date_from")
                        + " "
                        + dto.getInitialDate()
                        + " "
                        + getText("administration.reports.field.date_to")
                        + " "
                        + dto.getFinalDate();
        Paragraph p2 = new Paragraph(getHeaderChunk(header));
        p2.setAlignment(Element.ALIGN_LEFT);
        document.add(p2);
        document.add(new Phrase("\n"));

        Paragraph p3 =
                new Paragraph(
                        getHeaderChunk(
                                getText("administration.reports.field.lendings_count")
                                        + ":  "
                                        + dto.getTotals()[0]));
        p2.setAlignment(Element.ALIGN_LEFT);
        document.add(p3);
        Paragraph p4 =
                new Paragraph(
                        getHeaderChunk(
                                getText("administration.reports.field.lendings_current")
                                        + ":  "
                                        + dto.getTotals()[1]));
        p2.setAlignment(Element.ALIGN_LEFT);
        document.add(p4);
        Paragraph p5 =
                new Paragraph(
                        getHeaderChunk(
                                getText("administration.reports.field.lendings_late")
                                        + ":  "
                                        + dto.getTotals()[2]));
        p2.setAlignment(Element.ALIGN_LEFT);
        document.add(p5);
        document.add(new Phrase("\n"));

        document.add(new Phrase(getText("administration.reports.field.lendings_top")));
        document.add(new Phrase("\n"));

        PdfPTable table = createTable(dto);
        document.add(table);
        document.add(new Phrase("\n"));
    }

    private PdfPTable createTable(LendingsByDateReportDto dto) {
        PdfPTable table = new PdfPTable(5);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.setWidthPercentage(100f);
        PdfPCell cell;
        cell =
                new PdfPCell(
                        new Paragraph(
                                getHeaderChunk(getText("administration.reports.field.lendings"))));
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
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
        cell =
                new PdfPCell(
                        new Paragraph(
                                getHeaderChunk(getText("administration.reports.field.author"))));
        cell.setBackgroundColor(HEADER_BG_COLOR);
        cell.setColspan(2);
        cell.setBorderWidth(HEADER_BORDER_WIDTH);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
        // Table body
        if (dto.getData() == null || dto.getData().isEmpty()) return table;
        for (String[] data : dto.getData()) {
            cell = new PdfPCell(new Paragraph(getNormalChunk(data[0])));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(getNormalChunk(data[1])));
            cell.setColspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(getNormalChunk(data[2])));
            cell.setColspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
        }
        return table;
    }

    @Override
    public ReportType getReportType() {
        return ReportType.LENDINGS_BY_DATE;
    }

    @Autowired
    public void setReportsDAO(ReportsDAO reportsDAO) {
        this.reportsDAO = reportsDAO;
    }
}
