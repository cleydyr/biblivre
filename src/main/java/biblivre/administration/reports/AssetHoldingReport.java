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

import java.util.Collections;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

import biblivre.administration.reports.dto.AssetHoldingDto;
import biblivre.core.utils.NaturalOrderComparator;

public class AssetHoldingReport extends BaseBiblivreReport<AssetHoldingDto> {
	
	@Override
	protected AssetHoldingDto getReportData(ReportsDTO dto) {
		return ReportsDAO.getInstance(this.getSchema()).getAssetHoldingReportData();
	}

	@Override
	protected void generateReportBody(Document document, AssetHoldingDto reportData) throws Exception {
		PdfPTable table = new PdfPTable(7);

		table.setWidthPercentage(100f);

		createHeader(table);

		List<String[]> dataList = reportData.getData();

		_sortReportData(dataList);

		for (String[] data : dataList) {
			for (int i = 0; i <= 4; i++) {
				ReportUtil.insertValueCenter(table, ReportUtil::getSmallFontChunk, data[i]);
			}
		}

		document.add(table);
	}

	private void _sortReportData(List<String[]> dataList) {
		Collections.sort(dataList, (o1, o2) -> {
			if (o1 == null && o2 == null) {
				return 0;
			}

			return NaturalOrderComparator.NUMERICAL_ORDER.compare(o1[0], o2[0]);
		});
	}

	private void createHeader(PdfPTable table) {
		PdfPCell cell;
		cell = new PdfPCell(new Paragraph(ReportUtil.getBoldChunk(this.getText("administration.reports.field.accession_number"))));
		cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
		cell.setBorderWidth(HEADER_BORDER_WIDTH);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(ReportUtil.getBoldChunk(this.getText("administration.reports.field.author"))));
		cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
		cell.setColspan(2);
		cell.setBorderWidth(HEADER_BORDER_WIDTH);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(ReportUtil.getBoldChunk(this.getText("administration.reports.field.title"))));
		cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
		cell.setColspan(2);
		cell.setBorderWidth(HEADER_BORDER_WIDTH);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(ReportUtil.getBoldChunk(this.getText("administration.reports.field.edition"))));
		cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
		cell.setBorderWidth(HEADER_BORDER_WIDTH);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(ReportUtil.getBoldChunk(this.getText("administration.reports.field.date"))));
		cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
		cell.setBorderWidth(HEADER_BORDER_WIDTH);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
	}

	@Override
	protected String getTitle() {
		return this.getText("administration.reports.title.holdings");
	}
}
