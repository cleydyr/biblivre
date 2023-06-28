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

import biblivre.administration.reports.dto.AllUsersReportDto;
import biblivre.administration.reports.dto.BaseReportDto;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AllUsersReport extends BaseBiblivreReport {

    private ReportsDAO reportsDAO;

    @Override
    protected BaseReportDto getReportData(ReportsDTO dto) {
        return reportsDAO.getAllUsersReportData();
    }

    @Override
    protected void generateReportBody(Document document, BaseReportDto reportData) {
        AllUsersReportDto dto = (AllUsersReportDto) reportData;
        Paragraph p1 = new Paragraph(this.getText("administration.reports.title.all_users"));
        p1.setAlignment(Element.ALIGN_CENTER);
        document.add(p1);
        document.add(new Phrase("\n"));
        Paragraph p2 =
                new Paragraph(
                        this.getHeaderChunk(
                                this.getText("administration.reports.field.user_count_by_type")));
        p2.setAlignment(Element.ALIGN_LEFT);
        document.add(p2);
        document.add(new Phrase("\n"));
        PdfPTable summaryTable = createSummaryTable(dto.getTypesMap());
        document.add(summaryTable);
        document.add(new Phrase("\n"));

        List<PdfPTable> listTable = createListTable(dto.getData());

        Paragraph p3 =
                new Paragraph(
                        this.getHeaderChunk(
                                this.getText("administration.reports.field.user_list_by_type")));
        p3.setAlignment(Element.ALIGN_LEFT);
        document.add(p3);
        document.add(new Phrase("\n"));
        for (PdfPTable tabela : listTable) {
            document.add(tabela);
            document.add(new Phrase("\n"));
        }
    }

    private PdfPTable createSummaryTable(Map<String, Integer> tipos) {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(50f);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);

        int total = 0;
        PdfPCell cell;
        for (Entry<String, Integer> entry : tipos.entrySet()) {
            int count = entry.getValue();

            total += count;
            cell = new PdfPCell(new Paragraph(this.getHeaderChunk(entry.getKey().toUpperCase())));
            cell.setBackgroundColor(HEADER_BG_COLOR);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setColspan(2);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(this.getNormalChunk(String.valueOf(count))));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
        }
        cell =
                new PdfPCell(
                        new Paragraph(
                                this.getHeaderChunk(
                                        this.getText("administration.reports.field.total"))));
        cell.setBackgroundColor(HEADER_BG_COLOR);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setColspan(2);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getNormalChunk(String.valueOf(total))));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
        return table;
    }

    private List<PdfPTable> createListTable(Map<String, List<String>> data) {
        try {
            ArrayList<PdfPTable> tabelas = new ArrayList<>();
            PdfPTable table;
            PdfPCell cell;
            for (Entry<String, List<String>> entry : data.entrySet()) {
                table = new PdfPTable(4);
                table.setWidthPercentage(100f);
                String description = entry.getKey();
                cell = new PdfPCell(new Paragraph(this.getHeaderChunk(description.toUpperCase())));
                cell.setColspan(4);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell =
                        new PdfPCell(
                                new Paragraph(
                                        this.getHeaderChunk(
                                                this.getText(
                                                        "administration.reports.field.user_name"))));
                cell.setBackgroundColor(HEADER_BG_COLOR);
                cell.setBorderWidth(HEADER_BORDER_WIDTH);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell =
                        new PdfPCell(
                                new Paragraph(
                                        this.getHeaderChunk(
                                                this.getText(
                                                        "administration.reports.field.user_id"))));
                cell.setBackgroundColor(HEADER_BG_COLOR);
                cell.setBorderWidth(HEADER_BORDER_WIDTH);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell =
                        new PdfPCell(
                                new Paragraph(
                                        this.getHeaderChunk(
                                                this.getText(
                                                        "administration.reports.field.creation_date"))));
                cell.setBackgroundColor(HEADER_BG_COLOR);
                cell.setBorderWidth(HEADER_BORDER_WIDTH);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell =
                        new PdfPCell(
                                new Paragraph(
                                        this.getHeaderChunk(
                                                this.getText(
                                                        "administration.reports.field.modified"))));
                cell.setBackgroundColor(HEADER_BG_COLOR);
                cell.setBorderWidth(HEADER_BORDER_WIDTH);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                for (String line : entry.getValue()) {
                    String[] dados = line.split("\t");
                    // Nome
                    cell = new PdfPCell(new Paragraph(this.getNormalChunk(dados[0])));
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    table.addCell(cell);

                    // Matricula
                    cell = new PdfPCell(new Paragraph(this.getNormalChunk(dados[1])));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    table.addCell(cell);

                    // Data de Inclusao
                    cell = new PdfPCell(new Paragraph(this.getNormalChunk(dados[2])));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    table.addCell(cell);

                    // Data de Cancelamento/Alteracao
                    cell = new PdfPCell(new Paragraph(this.getNormalChunk(dados[3])));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    table.addCell(cell);
                }
                tabelas.add(table);
            }
            return tabelas;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public ReportType getReportType() {
        return ReportType.ALL_USERS;
    }

    @Autowired
    public void setReportsDAO(ReportsDAO reportsDAO) {
        this.reportsDAO = reportsDAO;
    }
}
