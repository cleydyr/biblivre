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

import biblivre.administration.reports.dto.SearchesByDateReportDto;
import biblivre.core.utils.CharPool;

public class SearchesByDateReport extends BaseBiblivreReport<SearchesByDateReportDto> {

	@Override
	protected SearchesByDateReportDto getReportData(ReportsDTO dto) {
		String initialDate = format(dto.getInitialDate());
		String finalDate = format(dto.getFinalDate());
		return ReportsDAO.getInstance(this.getSchema()).getSearchesByDateReportData(initialDate, finalDate);
	}

	@Override
	protected void generateReportBody(Document document, SearchesByDateReportDto reportData) throws Exception {
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

		_insertTable(document, reportData);
	}

	private void _insertTable(Document document, SearchesByDateReportDto reportData) throws DocumentException {
		PdfPTable table = createTable(reportData);

		document.add(table);

		ReportUtil.insertNewLine(document);
	}

	private PdfPTable createTable(SearchesByDateReportDto dto) {
		PdfPTable table = new PdfPTable(2);

		table.setHorizontalAlignment(Element.ALIGN_CENTER);

		_insertHeader(table);

		_insertBody(table, dto);

		return table;
	}

	private void _insertBody(PdfPTable table, SearchesByDateReportDto dto) {
		if (dto.getData() == null || dto.getData().isEmpty()) return;

		for (String[] data : dto.getData()) {
			ReportUtil.insertChunkedTextCellWithStrategy(
					table, ReportUtil::getNormalChunk,
					ReportUtil.CENTER_MIDDLE, data[1]);

			ReportUtil.insertChunkedTextCellWithStrategy(
					table, ReportUtil::getNormalChunk,
					ReportUtil.CENTER_MIDDLE, data[0]);
		}
	}

	private void _insertHeader(PdfPTable table) {
		ReportUtil.insertChunkedTextCellWithStrategy(
				table, ReportUtil::getHeaderChunk,
				ReportUtil.BORDER_BACKGROUND_CENTER_MIDDLE,
				getText("administration.reports.field.date"));
		ReportUtil.insertChunkedTextCellWithStrategy(
				table, ReportUtil::getHeaderChunk,
				ReportUtil.BORDER_BACKGROUND_CENTER_MIDDLE,
				getText("administration.reports.field.total"));
	}

	@Override
	protected String getTitle() {
		return this.getText("administration.reports.title.searches_by_date");
	}
	
}
