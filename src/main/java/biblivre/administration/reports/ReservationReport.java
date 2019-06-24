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
import com.lowagie.text.DocumentException;
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
		boolean hasBiblioData = reportData.getBiblioReservations() != null && 
				!reportData.getBiblioReservations().isEmpty();

		boolean hasHoldingData = reportData.getHoldingReservations() != null &&
				!reportData.getHoldingReservations().isEmpty();

		String message = null;

		if (hasBiblioData) {
			message = getText("administration.reports.field.biblio_reservation");
		}
		if (hasHoldingData) {
			message = getText("administration.reports.field.holding_reservation");
		}
		if (!hasBiblioData && !hasHoldingData) {
			message = getText(
					"administration.reports.field.no_data") + " - " +
					DateUtils.now(i18n.getLanguage());
		}

		_insertTable(document, reportData, message);

		ReportUtil.insertNewLine(document);
	}

	private void _insertTable(Document document, ReservationReportDto reportData, String subtitle)
		throws DocumentException {

		PdfPTable table = new PdfPTable(7);

		table.setWidthPercentage(100f);

		ReportUtil.insertChunkedTextCellWithStrategy(
				table, ReportUtil::getHeaderChunk,
				ReportUtil.BORDER_BACKGROUND_CENTER_MIDDLE.with(ReportUtil.COLSPAN.apply(7)),
				subtitle);

		_insertHeader(table);

		_insertBody(table, reportData.getBiblioReservations());

		document.add(table);
	}

	private void _insertHeader(PdfPTable table) {
		ReportUtil.insertChunkedTextCellWithStrategy(
				table, ReportUtil::getHeaderChunk,
				ReportUtil.BORDER_BACKGROUND_CENTER_MIDDLE,
				getText("administration.reports.field.user_id"));

		ReportUtil.insertChunkedTextCellWithStrategy(
				table, ReportUtil::getHeaderChunk,
				ReportUtil.BORDER_BACKGROUND_CENTER_MIDDLE.with(ReportUtil.COLSPAN.apply(2)),
				getText("administration.reports.field.user_name"));

		ReportUtil.insertChunkedTextCellWithStrategy(
				table, ReportUtil::getHeaderChunk,
				ReportUtil.BORDER_BACKGROUND_CENTER_MIDDLE.with(ReportUtil.COLSPAN.apply(2)),
				getText("administration.reports.field.title"));

		ReportUtil.insertChunkedTextCellWithStrategy(
				table, ReportUtil::getHeaderChunk,
				ReportUtil.BORDER_BACKGROUND_CENTER_MIDDLE,
				getText("administration.reports.field.author"));

		ReportUtil.insertChunkedTextCellWithStrategy(
				table, ReportUtil::getHeaderChunk,
				ReportUtil.BORDER_BACKGROUND_CENTER_MIDDLE,
				getText("administration.reports.field.reservation_date"));
	}

	private void _insertBody(PdfPTable table, List<String[]> dataList) {
		if (dataList == null || dataList.isEmpty()) {
			return;
		}

		for (String[] data : dataList) {
			ReportUtil.insertChunkedTextCellWithStrategy(
					table, ReportUtil::getNormalChunk, ReportUtil.CENTER_MIDDLE, data[1]);
			ReportUtil.insertChunkedTextCellWithStrategy(
					table, ReportUtil::getNormalChunk,
					ReportUtil.CENTER_MIDDLE.with(ReportUtil.COLSPAN.apply(2)), data[0]);
			ReportUtil.insertChunkedTextCellWithStrategy(
					table, ReportUtil::getNormalChunk,
					ReportUtil.CENTER_MIDDLE.with(ReportUtil.COLSPAN.apply(2)), data[2]);
			ReportUtil.insertChunkedTextCellWithStrategy(
					table, ReportUtil::getNormalChunk, ReportUtil.CENTER_MIDDLE, data[3]);
			ReportUtil.insertChunkedTextCellWithStrategy(
					table, ReportUtil::getNormalChunk, ReportUtil.CENTER_MIDDLE, data[4]);
		}

	}

	@Override
	protected String getTitle() {
		return this.getText("administration.reports.title.reservation");
	}

}
