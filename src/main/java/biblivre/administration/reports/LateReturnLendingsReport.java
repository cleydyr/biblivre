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
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfPTable;

import biblivre.administration.reports.dto.LateLendingsDto;
import biblivre.core.utils.CharPool;

public class LateReturnLendingsReport extends BaseBiblivreReport<LateLendingsDto> {

	@Override
	protected LateLendingsDto getReportData(ReportsDTO dto) {
		return ReportsDAO.getInstance(this.getSchema()).getLateReturnLendingsReportData();
	}

	@Override
	protected void generateReportBody(Document document, LateLendingsDto reportData)
		throws Exception {

		if (!reportData.getData().isEmpty()) {
			String lateLendingsCount =
				new StringBuilder(4)
					.append(getText("administration.reports.field.late_lendings_count"))
					.append(CharPool.COLON)
					.append(CharPool.SPACE)
					.append(reportData.getData().size())
					.toString();

			ReportUtil.insertChunkedTextParagraph(
					document, ReportUtil::getHeaderChunk, ReportUtil.ParagraphAlignment.LEFT,
					lateLendingsCount);

			ReportUtil.insertNewLine(document);

			_insertTable(document, reportData.getData());
		}
	}

	private void _insertTable(Document document, List<String[]> lendings) throws DocumentException {
		PdfPTable table = new PdfPTable(6);

		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.setWidthPercentage(100f);
		
		_insertHeader(table);

		for (String[] lending : lendings) {
			ReportUtil.insertChunkedTextCellWithStrategy(
					table, ReportUtil::getNormalChunk, ReportUtil.CENTER_MIDDLE, lending[0]);
			ReportUtil.insertChunkedTextCellWithStrategy(
					table, ReportUtil::getNormalChunk,
					ReportUtil.LEFT_MIDDLE.with(ReportUtil.COLSPAN.apply(2)), lending[1]);
			ReportUtil.insertChunkedTextCellWithStrategy(
					table, ReportUtil::getNormalChunk,
					ReportUtil.LEFT_MIDDLE.with(ReportUtil.COLSPAN.apply(2)), lending[2]);
			ReportUtil.insertChunkedTextCellWithStrategy(
					table, ReportUtil::getNormalChunk, ReportUtil.CENTER_MIDDLE, lending[3]);
		}

		document.add(table);

		ReportUtil.insertNewLine(document);
	}

	private void _insertHeader(PdfPTable table) {
		ReportUtil.insertChunkedTextCellWithStrategy(
				table, ReportUtil::getHeaderChunk, ReportUtil.BORDER_BACKGROUND_CENTER_MIDDLE,
				getText("administration.reports.field.user_id"));

		ReportUtil.insertChunkedTextCellWithStrategy(
				table, ReportUtil::getHeaderChunk,
				ReportUtil.BORDER_BACKGROUND_LEFT_MIDDLE.with(ReportUtil.COLSPAN.apply(2)),
				getText("administration.reports.field.user_name"));

		ReportUtil.insertChunkedTextCellWithStrategy(
				table, ReportUtil::getHeaderChunk,
				ReportUtil.BORDER_BACKGROUND_LEFT_MIDDLE.with(ReportUtil.COLSPAN.apply(2)),
				getText("administration.reports.field.title"));

		ReportUtil.insertChunkedTextCellWithStrategy(
				table, ReportUtil::getHeaderChunk, ReportUtil.BORDER_BACKGROUND_CENTER_MIDDLE,
				getText("administration.reports.field.expected_date"));
	}

	@Override
	protected String getTitle() {
		return this.getText("administration.reports.title.late_lendings");
	}

}
