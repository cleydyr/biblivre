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
import biblivre.administration.reports.dto.DeweyReportDto;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeweyReport extends BaseBiblivreReport {
    private ReportsDAO reportsDAO;

    private final Integer index = 0;

    @Override
    protected BaseReportDto getReportData(ReportsDTO dto) {
        return reportsDAO.getDeweyReportData(
                dto.getDatabase(), dto.getDatafield(), dto.getDigits());
    }

    @Override
    protected void generateReportBody(Document document, BaseReportDto reportData) {
        DeweyReportDto dto = (DeweyReportDto) reportData;
        Paragraph p1 = new Paragraph(getText("administration.reports.title.dewey"));
        p1.setAlignment(Element.ALIGN_CENTER);
        document.add(p1);
        document.add(new Phrase("\n\n"));
        PdfPTable table = new PdfPTable(6);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        createHeader(table);
        PdfPCell cell;
        int totalRecords = 0;
        int totalHoldings = 0;
        List<String[]> dataList = dto.getData();
        dataList.sort(
                (o1, o2) -> {
                    if (o1 == null || o1[index] == null) {
                        return -1;
                    }

                    if (o2 == null || o2[index] == null) {
                        return 1;
                    }

                    return o1[index].compareTo(o2[index]);
                });
        for (String[] data : dataList) {
            if (StringUtils.isBlank(data[0])) {
                data[0] = getText("administration.reports.field.unclassified");
            }
            totalRecords += Integer.parseInt(data[1]);
            totalHoldings += Integer.parseInt(data[2]);
        }
        if (totalRecords > 0) {
            dataList.add(
                    new String[] {
                        getText("administration.reports.field.total"),
                        String.valueOf(totalRecords),
                        String.valueOf(totalHoldings)
                    });
        }

        for (String[] data : dataList) {
            cell = new PdfPCell(new Paragraph(getNormalChunk(data[0])));
            cell.setColspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(getNormalChunk(data[1])));
            cell.setColspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(getNormalChunk(data[2])));
            cell.setColspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
        }
        document.add(table);
    }

    private void createHeader(PdfPTable table) {
        PdfPCell cell;
        cell =
                new PdfPCell(
                        new Paragraph(
                                getHeaderChunk(getText("administration.reports.field.dewey"))));
        cell.setBackgroundColor(HEADER_BG_COLOR);
        cell.setColspan(2);
        cell.setBorderWidth(HEADER_BORDER_WIDTH);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
        cell =
                new PdfPCell(
                        new Paragraph(
                                getHeaderChunk(
                                        getText("administration.reports.field.number_of_titles"))));
        cell.setBackgroundColor(HEADER_BG_COLOR);
        cell.setColspan(2);
        cell.setBorderWidth(HEADER_BORDER_WIDTH);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
        cell =
                new PdfPCell(
                        new Paragraph(
                                getHeaderChunk(
                                        getText(
                                                "administration.reports.field.number_of_holdings"))));
        cell.setBackgroundColor(HEADER_BG_COLOR);
        cell.setColspan(2);
        cell.setBorderWidth(HEADER_BORDER_WIDTH);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    @Override
    public ReportType getReportType() {
        return ReportType.DEWEY;
    }

    @Autowired
    public void setReportsDAO(ReportsDAO reportsDAO) {
        this.reportsDAO = reportsDAO;
    }
}
