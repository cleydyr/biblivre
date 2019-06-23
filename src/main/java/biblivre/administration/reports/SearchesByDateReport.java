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

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

import biblivre.administration.reports.dto.SearchesByDateReportDto;

public class SearchesByDateReport extends BaseBiblivreReport<SearchesByDateReportDto> {

	@Override
	protected SearchesByDateReportDto getReportData(ReportsDTO dto) {
		String initialDate = format(dto.getInitialDate());
		String finalDate = format(dto.getFinalDate());
		return ReportsDAO.getInstance(this.getSchema()).getSearchesByDateReportData(initialDate, finalDate);
	}

	@Override
	protected void generateReportBody(Document document, SearchesByDateReportDto reportData) throws Exception {
		StringBuilder p2Builder = new StringBuilder();
		p2Builder.append(this.getText("administration.reports.field.date_from") + " ");
		p2Builder.append(reportData.getInitialDate());
		p2Builder.append(" " + this.getText("administration.reports.field.date_to") + " ");
		p2Builder.append(reportData.getFinalDate());
		Paragraph p2 = new Paragraph(ReportUtil.getHeaderChunk(p2Builder.toString()));
		p2.setAlignment(Element.ALIGN_LEFT);
		document.add(p2);
		document.add(new Phrase("\n"));

		PdfPTable table = createTable(reportData);
		document.add(table);
		document.add(new Phrase("\n"));
	}

	private PdfPTable createTable(SearchesByDateReportDto dto) {
		PdfPTable table = new PdfPTable(2);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		//Table header
		PdfPCell cell;
		cell = new PdfPCell(new Paragraph(ReportUtil.getHeaderChunk(this.getText("administration.reports.field.date"))));
		cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
		cell.setBorderWidth(HEADER_BORDER_WIDTH);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(ReportUtil.getHeaderChunk(this.getText("administration.reports.field.total"))));
		cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
		cell.setColspan(2);
		cell.setBorderWidth(HEADER_BORDER_WIDTH);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		//Table body
		if (dto.getData() == null || dto.getData().isEmpty()) return table;
		for (String[] data : dto.getData()) {
			cell = new PdfPCell(new Paragraph(ReportUtil.getNormalChunk(data[1])));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(ReportUtil.getNormalChunk(data[0])));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
		}
		return table;
	}

	@Override
	protected String getTitle() {
		return this.getText("administration.reports.title.searches_by_date");
	}
	
}
