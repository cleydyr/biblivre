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
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfPTable;

import biblivre.administration.reports.dto.LendingsByDateReportDto;
import biblivre.core.utils.CharPool;

public class LendingsByDateReport extends BaseBiblivreReport<LendingsByDateReportDto> {

	@Override
	protected LendingsByDateReportDto getReportData(ReportsDTO dto) {
		ReportsDAO dao = ReportsDAO.getInstance(this.getSchema());
		String initialDate = format(dto.getInitialDate());
		String finalDate = format(dto.getFinalDate());
		return dao.getLendingsByDateReportData(initialDate, finalDate);
	}

	@Override
	protected void generateReportBody(Document document, LendingsByDateReportDto reportData) throws Exception {
		String header =
			new StringBuilder(7)
				.append(getText("administration.reports.field.date_from"))
				.append(CharPool.SPACE)
				.append(reportData.getInitialDate())
				.append(CharPool.SPACE)
				.append(getText("administration.reports.field.date_to"))
				.append(CharPool.SPACE)
				.append(reportData.getFinalDate())
				.toString();

		ReportUtil.insertChunkedTextParagraph(
				document, ReportUtil::getHeaderChunk, ReportUtil.ParagraphAlignment.LEFT, header);

		ReportUtil.insertNewLine(document);

		String[] totals = reportData.getTotals();

		ReportUtil.insertChunkedTextParagraph(
				document, ReportUtil::getHeaderChunk, ReportUtil.ParagraphAlignment.LEFT,
				getText("administration.reports.field.lendings_count") + ":  " + totals[0]);

		ReportUtil.insertChunkedTextParagraph(
				document, ReportUtil::getHeaderChunk, ReportUtil.ParagraphAlignment.LEFT,
				getText("administration.reports.field.lendings_current") + ":  " + totals[1]);

		ReportUtil.insertChunkedTextParagraph(
				document, ReportUtil::getHeaderChunk, ReportUtil.ParagraphAlignment.LEFT,
				getText("administration.reports.field.lendings_late") + ":  " + totals[2]);

		ReportUtil.insertNewLine(document);

		createTable(document, reportData);

		ReportUtil.insertNewLine(document);
	}

	private void createTable(Document document, LendingsByDateReportDto dto) throws DocumentException {
		if (dto.getData() == null || dto.getData().isEmpty()) {
			return;
		}

		PdfPTable table = new PdfPTable(5);

		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.setWidthPercentage(100f);

		_insertHeader(table);

		_insertBody(table, dto);

		document.add(table);
	}

	private void _insertBody(PdfPTable table, LendingsByDateReportDto dto) {
		for (String[] data : dto.getData()) {
			ReportUtil.insertChunkedTextCellWithStrategy(
					table, ReportUtil::getNormalChunk, ReportUtil.CENTER_MIDDLE,
					data[0]);
			ReportUtil.insertChunkedTextCellWithStrategy(
					table, ReportUtil::getNormalChunk, ReportUtil.LEFT_MIDDLE,
					data[1]);
			ReportUtil.insertChunkedTextCellWithStrategy(
					table, ReportUtil::getNormalChunk, ReportUtil.LEFT_MIDDLE,
					data[2]);
		}
	}

	private void _insertHeader(PdfPTable table) {
		ReportUtil.insertChunkedTextCellWithStrategy(
				table, ReportUtil::getHeaderChunk, ReportUtil.BORDER_BACKGROUND_CENTER_MIDDLE,
				getText("administration.reports.field.lendings"));
		ReportUtil.insertChunkedTextCellWithStrategy(
				table, ReportUtil::getHeaderChunk,
				ReportUtil.BORDER_BACKGROUND_LEFT_MIDDLE.with(ReportUtil.COLSPAN.apply(2)),
				getText("administration.reports.field.title"));
		ReportUtil.insertChunkedTextCellWithStrategy(
				table, ReportUtil::getHeaderChunk,
				ReportUtil.BORDER_BACKGROUND_LEFT_MIDDLE.with(ReportUtil.COLSPAN.apply(2)),
				getText("administration.reports.field.author"));
	}

	@Override
	protected String getTitle() {
		return this.getText("administration.reports.title.lendings_by_date");
	}

	
}
