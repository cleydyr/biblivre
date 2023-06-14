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
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RequestsByDateReport extends BaseBiblivreReport {

    @Override
    protected BaseReportDto getReportData(ReportsDTO dto) {
        ReportsDAO dao = ReportsDAOImpl.getInstance();
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
        document.add(new Phrase("\n"));
        StringBuilder header = new StringBuilder();
        header.append(getText("administration.reports.field.date_from"));
        header.append(" ").append(dto.getInitialDate()).append(" ");
        header.append(getText("administration.reports.field.date_to"));
        header.append(" ").append(dto.getFinalDate());
        Paragraph p2 = new Paragraph(getHeaderChunk(header.toString()));
        p2.setAlignment(Element.ALIGN_LEFT);
        document.add(p2);
        document.add(new Phrase("\n"));
        if (dto.getData() != null) {
            PdfPTable table = createTable(dto.getData());
            document.add(table);
            document.add(new Phrase("\n"));
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
        String title = null;
        String quantity = null;
        String unit_value = null;
        String total_value = null;
        for (String[] data : dataList) {
            if (!data[0].equals(lastQuotationId)) {
                if (!lastQuotationId.equals("0")) {
                    cell = new PdfPCell(new Paragraph(getNormalChunk(requester)));
                    cell.setColspan(2);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_TOP);
                    table.addCell(cell);
                    cell = new PdfPCell(new Paragraph(getNormalChunk(title)));
                    cell.setColspan(2);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_TOP);
                    table.addCell(cell);
                    cell = new PdfPCell(new Paragraph(getNormalChunk(quantity)));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_TOP);
                    table.addCell(cell);
                    cell = new PdfPCell(new Paragraph(getNormalChunk(unit_value)));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_TOP);
                    table.addCell(cell);
                    cell = new PdfPCell(new Paragraph(getNormalChunk(total_value)));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_TOP);
                    table.addCell(cell);
                }

                requester = "";
                title = "";
                quantity = "";
                unit_value = "";
                total_value = "";
            }

            lastQuotationId = data[0];
            requester = data[1] + "\n";
            title += data[2] + "\n";
            quantity += data[3] + "\n";
            unit_value += data[4] + "\n";
            total_value = (data[5] == null ? "-" : data[5]) + "\n";
        }

        if (!lastQuotationId.equals("0")) {
            cell = new PdfPCell(new Paragraph(getNormalChunk(requester)));
            cell.setColspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(getNormalChunk(title)));
            cell.setColspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(getNormalChunk(quantity)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(getNormalChunk(unit_value)));
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
}
