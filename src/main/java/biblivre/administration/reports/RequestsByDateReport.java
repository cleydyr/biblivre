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
import biblivre.administration.reports.dto.RequestsByDateReportDto;
import biblivre.core.utils.CharPool;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RequestsByDateReport extends BaseBiblivreReport {

    private ReportsDAO reportsDAO;

    @Override
    protected BaseReportDto getReportData(ReportsDTO dto) {
        ReportsDAO dao = reportsDAO;
        String initialDate = dateFormat.format(dto.getInitialDate());
        String finalDate = dateFormat.format(dto.getFinalDate());
        return dao.getRequestsByDateReportData(initialDate, finalDate);
    }

    @Override
    protected void generateReportBody(Document document, BaseReportDto reportData) {
        RequestsByDateReportDto dto = (RequestsByDateReportDto) reportData;
        Paragraph p1 = new Paragraph(getText("administration.reports.title.orders_by_date"));
        p1.setAlignment(Element.ALIGN_CENTER);
        document.add(p1);
        document.add(new Phrase(CharPool.NEW_LINE));
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
        document.add(new Phrase(CharPool.NEW_LINE));
        if (dto.getData() != null) {
            PdfPTable table = createTable(dto.getData());
            document.add(table);
            document.add(new Phrase(CharPool.NEW_LINE));
        }
    }

    private PdfPTable createTable(List<String[]> dataList) {
        PdfPTable table = new PdfPTable(7);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.setWidthPercentage(100f);
        createHeader(table);
        PdfPCell cell;
        String lastQuotationId = "0";
        String requester = null;
        StringBuilder title = new StringBuilder();
        StringBuilder quantity = new StringBuilder();
        StringBuilder unit_value = new StringBuilder();
        String total_value = null;
        for (String[] data : dataList) {
            if (!data[0].equals(lastQuotationId)) {
                if (!lastQuotationId.equals("0")) {
                    cell = new PdfPCell(new Paragraph(getNormalChunk(requester)));
                    cell.setColspan(2);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_TOP);
                    table.addCell(cell);
                    cell = new PdfPCell(new Paragraph(getNormalChunk(title.toString())));
                    cell.setColspan(2);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_TOP);
                    table.addCell(cell);
                    cell = new PdfPCell(new Paragraph(getNormalChunk(quantity.toString())));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_TOP);
                    table.addCell(cell);
                    cell = new PdfPCell(new Paragraph(getNormalChunk(unit_value.toString())));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_TOP);
                    table.addCell(cell);
                    cell = new PdfPCell(new Paragraph(getNormalChunk(total_value)));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_TOP);
                    table.addCell(cell);
                }
            }

            lastQuotationId = data[0];
            requester = data[1] + CharPool.NEW_LINE;
            title.append(data[2]).append(CharPool.NEW_LINE);
            quantity.append(data[3]).append(CharPool.NEW_LINE);
            unit_value.append(data[4]).append(CharPool.NEW_LINE);
            total_value = (data[5] == null ? "-" : data[5]) + CharPool.NEW_LINE;
        }

        if (!lastQuotationId.equals("0")) {
            cell = new PdfPCell(new Paragraph(getNormalChunk(requester)));
            cell.setColspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(getNormalChunk(title.toString())));
            cell.setColspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(getNormalChunk(quantity.toString())));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(getNormalChunk(unit_value.toString())));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(getNormalChunk(total_value)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);
        }
        return table;
    }

    private void createHeader(PdfPTable table) {
        PdfPCell cell;
        cell =
                new PdfPCell(
                        new Paragraph(
                                getHeaderChunk(getText("administration.reports.field.requester"))));
        cell.setBackgroundColor(HEADER_BG_COLOR);
        cell.setColspan(2);
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
                                getHeaderChunk(getText("administration.reports.field.amount"))));
        cell.setBackgroundColor(HEADER_BG_COLOR);
        cell.setBorderWidth(HEADER_BORDER_WIDTH);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
        cell =
                new PdfPCell(
                        new Paragraph(
                                getHeaderChunk(
                                        getText("administration.reports.field.unit_value"))));
        cell.setBackgroundColor(HEADER_BG_COLOR);
        cell.setBorderWidth(HEADER_BORDER_WIDTH);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
        cell =
                new PdfPCell(
                        new Paragraph(
                                getHeaderChunk(
                                        getText("administration.reports.field.paid_value"))));
        cell.setBackgroundColor(HEADER_BG_COLOR);
        cell.setBorderWidth(HEADER_BORDER_WIDTH);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    @Override
    public ReportType getReportType() {
        return ReportType.ACQUISITION;
    }

    @Autowired
    public void setReportsDAO(ReportsDAO reportsDAO) {
        this.reportsDAO = reportsDAO;
    }
}
