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
import com.lowagie.text.pdf.PdfPTable;

import biblivre.administration.reports.dto.AssetHoldingByDateDto;

public class AssetHoldingByDateReport extends BaseBiblivreReport<AssetHoldingByDateDto> {
	
	@Override
	protected AssetHoldingByDateDto getReportData(ReportsDTO dto) {
		String initialDate = format(dto.getInitialDate());
		String finalDate = format(dto.getFinalDate());
		return ReportsDAO.getInstance(this.getSchema()).getAssetHoldingByDateReportData(initialDate, finalDate);
	}

	@Override
	protected void generateReportBody(Document document, AssetHoldingByDateDto reportData) throws Exception {
		PdfPTable table = new PdfPTable(6);

		table.setWidthPercentage(100f);

		_createHeader(table);

		_insertDataRows(table, reportData);

		document.add(table);
	}

	private void _insertDataRows(PdfPTable table, AssetHoldingByDateDto reportData) {
		List<String[]> dataList = reportData.getData();

		for (String[] data : dataList) {
			for (int i = 0; i <= 5; i++) {
				ReportUtil.insertChunkedCenterTextCell(table, ReportUtil::getSmallFontChunk, data[i]);
			}
		}
	}

	private void _createHeader(PdfPTable table) {
		ReportUtil.insertChunkedCenterTextCellWithBackgroundAndBorder(
				table, ReportUtil::getBoldChunk, getText("administration.reports.field.created"));
		ReportUtil.insertChunkedCenterTextCellWithBackgroundAndBorder(
				table, ReportUtil::getBoldChunk, getText("administration.reports.field.accession_number"));
		ReportUtil.insertChunkedCenterTextCellWithBackgroundAndBorder(
				table, ReportUtil::getBoldChunk, getText("administration.reports.field.title"));
		ReportUtil.insertChunkedCenterTextCellWithBackgroundAndBorder(
				table, ReportUtil::getBoldChunk, getText("administration.reports.field.author"));
		ReportUtil.insertChunkedCenterTextCellWithBackgroundAndBorder(
				table, ReportUtil::getBoldChunk, getText("administration.reports.field.date"));
		ReportUtil.insertChunkedCenterTextCellWithBackgroundAndBorder(
				table, ReportUtil::getBoldChunk, getText("administration.reports.field.acquisition"));
	}

	@Override
	protected String getTitle() {
		return this.getText("administration.reports.title.holdings_by_date");
	}

}
