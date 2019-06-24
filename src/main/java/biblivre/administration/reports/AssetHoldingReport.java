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
import com.lowagie.text.pdf.PdfPTable;

import biblivre.administration.reports.dto.AssetHoldingDto;
import biblivre.core.utils.NaturalOrderComparator;

public class AssetHoldingReport extends BaseBiblivreReport<AssetHoldingDto> {

	private static int[] COLSPANS = new int[] {1, 2, 2, 1, 1};
	private static String[] HEADER_TEXTS = new String[] {
		"administration.reports.field.accession_number",
		"administration.reports.field.author",
		"administration.reports.field.title",
		"administration.reports.field.edition",
		"administration.reports.field.date",
	};

	@Override
	protected AssetHoldingDto getReportData(ReportsDTO dto) {
		return ReportsDAO.getInstance(this.getSchema()).getAssetHoldingReportData();
	}

	@Override
	protected void generateReportBody(Document document, AssetHoldingDto reportData) throws Exception {
		PdfPTable table = new PdfPTable(7);

		table.setWidthPercentage(100f);

		_createHeader(table);

		List<String[]> dataList = reportData.getData();

		_sortReportData(dataList);

		for (String[] data : dataList) {
			for (int i = 0; i < data.length; i++) {
				ReportUtil.insertChunkedCenterTextCell(
						table, ReportUtil::getSmallFontChunk, data[i], COLSPANS[i]);
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

	private void _createHeader(PdfPTable table) {
		for (int i = 0; i < COLSPANS.length; i++) {
			ReportUtil.insertChunkedTextCellWithBackgroundAndBorder(
					table, ReportUtil::getBoldChunk, getText(HEADER_TEXTS[i]), COLSPANS[i]);
		}
	}

	@Override
	protected String getTitle() {
		return this.getText("administration.reports.title.holdings");
	}
}
