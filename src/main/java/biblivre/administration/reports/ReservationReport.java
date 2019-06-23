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

import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

import biblivre.administration.reports.dto.ReservationReportDto;
import biblivre.core.utils.DateUtils;

public class ReservationReport extends BaseBiblivreReport<ReservationReportDto> {

	@Override
	protected ReservationReportDto getReportData(ReportsDTO dto) {
		return ReportsDAO.getInstance(this.getSchema()).getReservationReportData();
	}

	@Override
	protected void generateReportBody(Document document, ReservationReportDto reportData) throws Exception {
		Paragraph p1 = new Paragraph(this.getText("administration.reports.title.reservation"));
		p1.setAlignment(Element.ALIGN_CENTER);
		document.add(p1);
		document.add(new Phrase("\n\n"));
		boolean hasBiblioData = reportData.getBiblioReservations() != null && !reportData.getBiblioReservations().isEmpty();
		boolean hasHoldingData = reportData.getHoldingReservations() != null && !reportData.getHoldingReservations().isEmpty();
		if (hasBiblioData) {
			PdfPTable biblioTable = new PdfPTable(7);
			biblioTable.setWidthPercentage(100f);
			PdfPCell cell = new PdfPCell(new Paragraph(ReportUtil.getHeaderChunk(this.getText("administration.reports.field.biblio_reservation"))));
			cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
			cell.setBorderWidth(HEADER_BORDER_WIDTH);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setColspan(7);
			biblioTable.addCell(cell);
			createHeader(biblioTable);
			createBody(biblioTable, reportData.getBiblioReservations());
			document.add(biblioTable);
			document.add(new Phrase("\n"));
		}
		if (hasHoldingData) {
			PdfPTable holdingTable = new PdfPTable(7);
			holdingTable.setWidthPercentage(100f);
			PdfPCell cell = new PdfPCell(new Paragraph(ReportUtil.getHeaderChunk(this.getText("administration.reports.field.holding_reservation"))));
			cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
			cell.setBorderWidth(HEADER_BORDER_WIDTH);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setColspan(7);
			holdingTable.addCell(cell);
			createHeader(holdingTable);
			createBody(holdingTable, reportData.getHoldingReservations());
			document.add(holdingTable);
		}
		if (!hasBiblioData && !hasHoldingData) {
			PdfPTable noDataTable = new PdfPTable(1);
			noDataTable.setWidthPercentage(100f);
			String message = this.getText("administration.reports.field.no_data") + " - " + DateUtils.now(i18n.getLanguage());
			PdfPCell cell = new PdfPCell(new Paragraph(ReportUtil.getHeaderChunk(message)));
			cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
			cell.setBorderWidth(HEADER_BORDER_WIDTH);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			noDataTable.addCell(cell);
			document.add(noDataTable);
		}
	}

	private void createHeader(PdfPTable table) {
		PdfPCell cell;
		cell = new PdfPCell(new Paragraph(ReportUtil.getHeaderChunk(this.getText("administration.reports.field.user_id"))));
		cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
		cell.setBorderWidth(HEADER_BORDER_WIDTH);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(ReportUtil.getHeaderChunk(this.getText("administration.reports.field.user_name"))));
		cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
		cell.setColspan(2);
		cell.setBorderWidth(HEADER_BORDER_WIDTH);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(ReportUtil.getHeaderChunk(this.getText("administration.reports.field.title"))));
		cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
		cell.setColspan(2);
		cell.setBorderWidth(HEADER_BORDER_WIDTH);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(ReportUtil.getHeaderChunk(this.getText("administration.reports.field.author"))));
		cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
		cell.setBorderWidth(HEADER_BORDER_WIDTH);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(ReportUtil.getHeaderChunk(this.getText("administration.reports.field.reservation_date"))));
		cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
		cell.setBorderWidth(HEADER_BORDER_WIDTH);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
	}

	private void createBody(PdfPTable table, List<String[]> dataList) {
		if (dataList == null || dataList.isEmpty()) {
			return;
		}
		for (String[] data : dataList) {
			PdfPCell cell;
			cell = new PdfPCell(new Paragraph(ReportUtil.getNormalChunk(data[1])));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(ReportUtil.getNormalChunk(data[0])));
			cell.setColspan(2);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(ReportUtil.getNormalChunk(data[2])));
			cell.setColspan(2);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(ReportUtil.getNormalChunk(data[3])));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(ReportUtil.getNormalChunk(data[4])));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
		}
	}

}
