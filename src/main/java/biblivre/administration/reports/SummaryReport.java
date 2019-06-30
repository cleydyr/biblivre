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

import org.apache.commons.lang3.StringUtils;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfPTable;

import biblivre.administration.reports.dto.SummaryReportDto;

public class SummaryReport extends BaseBiblivreReport<SummaryReportDto> {
	private static int[] COLSPANS = new int[] {2, 2, 1, 1, 1, 1, 1};

	private static String[] HEADER_TEXTS = new String[] {
		"administration.reports.field.dewey",
		"administration.reports.field.title",
		"administration.reports.field.author",
		"administration.reports.field.isbn",
		"administration.reports.field.editor",
		"administration.reports.field.year",
		"administration.reports.field.edition",
		"administration.reports.field.holdings_count"
	};

	private static int[] DATA_ORDER = {6, 0, 1, 2, 3, 4, 5, 7};

	private Integer index;

	@Override
	protected SummaryReportDto getReportData(ReportsDTO dto) {
		Integer order = 1;
		if (StringUtils.isNotBlank(dto.getOrder()) &&
				StringUtils.isNumeric(dto.getOrder().trim())) {

			order = Integer.valueOf(dto.getOrder().trim());
		}
		switch (order) {
			case 1 : this.index = 6; break; //dewey
			case 2 : this.index = 0; break; //title
			case 3 : this.index = 1; break; //author
			default : this.index = 6; //dewey
		}
		return ReportsDAO.getInstance(this.getSchema()).getSummaryReportData(dto.getDatabase());
	}

	@Override
	protected void generateReportBody(Document document, SummaryReportDto reportData)
			throws Exception {

		PdfPTable table = new PdfPTable(10);

		table.setWidthPercentage(100f);

		_insertHeader(table);

		_sortReportData(reportData);

		_insertBody(table, reportData);

		document.add(table);
	}

	private void _insertBody(PdfPTable table, SummaryReportDto reportData) {
		for (String[] data : reportData.getData()) {
			for (int i = 0; i < COLSPANS.length; i++) {
				ReportUtil.insertChunkedCenterTextCell(
						table, ReportUtil::getSmallFontChunk, data[DATA_ORDER[i]], COLSPANS[i]);
			}
		}
	}

	private void _sortReportData(SummaryReportDto reportData) {
		Collections.sort(reportData.getData(), (o1, o2) -> {
			if (o1 == null || o1[this.index] == null) {
				return -1;
			}

			if (o2 == null || o2[this.index] == null) {
				return 1;
			}

			return o1[this.index].compareTo(o2[this.index]);
		});
	}

	private void _insertHeader(PdfPTable table) {
		for (int i = 0; i < COLSPANS.length; i++) {
			ReportUtil.insertChunkedCenterTextCellWithBackgroundAndBorder(
					table, ReportUtil::getBoldChunk, getText(HEADER_TEXTS[i]), COLSPANS[i]);
		}
	}

	@Override
	protected String getTitle() {
		return this.getText("administration.reports.title.summary");
	}

}
