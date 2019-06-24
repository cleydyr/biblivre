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

import org.apache.commons.lang3.StringUtils;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfPTable;

import biblivre.administration.reports.dto.DeweyReportDto;

public class DeweyReport extends BaseBiblivreReport<DeweyReportDto> {

	private Integer index = 0;

	private static int[] COLSPANS = new int[] {2, 2, 2};

	private static String[] HEADER_TEXTS = new String[] {
			"administration.reports.field.dewey",
			"administration.reports.field.number_of_titles",
			"administration.reports.field.number_of_holdings"
	};

	@Override
	protected DeweyReportDto getReportData(ReportsDTO dto) {
		return ReportsDAO.getInstance(this.getSchema()).getDeweyReportData(dto.getDatabase(), dto.getDatafield(), dto.getDigits());
	}

	@Override
	protected void generateReportBody(Document document, DeweyReportDto reportData) throws Exception {
		PdfPTable table = new PdfPTable(6);

		table.setHorizontalAlignment(Element.ALIGN_CENTER);

		_createHeader(table);

		int totalRecords = 0;

		int totalHoldings = 0;

		List<String[]> dataList = reportData.getData();

		_sortReportData(dataList);

		for (String[] data : dataList) {
			if (StringUtils.isBlank(data[0])) {
				data[0] = this.getText("administration.reports.field.unclassified");
			}

			totalRecords += Integer.parseInt(data[1]);

			totalHoldings += Integer.parseInt(data[2]);
		}

		if (totalRecords > 0) {
			dataList.add(new String[]{
				this.getText("administration.reports.field.total"),
				String.valueOf(totalRecords),
				String.valueOf(totalHoldings)
			});
		}

		for (String[] data : reportData.getData()) {
			for (int i = 0; i < COLSPANS.length; i++) {
				ReportUtil.insertChunkedCenterTextCell(
						table, ReportUtil::getNormalChunk, data[i], COLSPANS[i]);
			}
		}

		document.add(table);
	}

	private void _sortReportData(List<String[]> dataList) {
		Collections.sort(dataList, (o1, o2) -> {
			if (o1 == null || o1[this.index] == null) {
				return -1;
			}

			if (o2 == null || o2[this.index] == null) {
				return 1;
			}

			return o1[this.index].compareTo(o2[this.index]);
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
		return this.getText("administration.reports.title.dewey");
	}
}
