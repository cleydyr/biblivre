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
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;

import biblivre.administration.reports.dto.RequestsByDateReportDto;
import biblivre.core.utils.CharPool;

public class RequestsByDateReport extends BaseBiblivreReport<RequestsByDateReportDto> {

	@Override
	protected RequestsByDateReportDto getReportData(ReportsDTO dto) {
		ReportsDAO dao = ReportsDAO.getInstance(this.getSchema());
		String initialDate = format(dto.getInitialDate());
		String finalDate = format(dto.getFinalDate());
		return dao.getRequestsByDateReportData(initialDate, finalDate);
	}

	@Override
	protected void generateReportBody(Document document, RequestsByDateReportDto reportData)
		throws Exception {

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

		if (reportData.getData() != null) {
			PdfPTable table = createTable(reportData.getData());
			document.add(table);
			document.add(new Phrase("\n"));
		}
	}

	private PdfPTable createTable(List<String[]> dataList) {
		PdfPTable table = new PdfPTable(7);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.setWidthPercentage(100f);

		_insertHeader(table);

		_insertBody(table, dataList);

		return table;
	}

	private void _insertBody(PdfPTable table, List<String[]> dataList) {
		String lastQuotationId = "0";
		String requester = null;
		String title = null;
		String quantity = null;
		String unit_value = null;
		String total_value = null;

		for (String[] data : dataList) {
			if (!data[0].equals(lastQuotationId)) {
				if (!lastQuotationId.equals("0")) {
					ReportUtil.insertChunkedTextCellWithStrategy(
							table, ReportUtil::getNormalChunk,
							ReportUtil.LEFT_TOP.with(ReportUtil.COLSPAN.apply(2)), requester);
					ReportUtil.insertChunkedTextCellWithStrategy(
							table, ReportUtil::getNormalChunk,
							ReportUtil.LEFT_TOP.with(ReportUtil.COLSPAN.apply(2)), title);
					ReportUtil.insertChunkedTextCellWithStrategy(
							table, ReportUtil::getNormalChunk, ReportUtil.CENTER_TOP, quantity);
					ReportUtil.insertChunkedTextCellWithStrategy(
							table, ReportUtil::getNormalChunk, ReportUtil.CENTER_TOP, unit_value);
					ReportUtil.insertChunkedTextCellWithStrategy(
							table, ReportUtil::getNormalChunk, ReportUtil.CENTER_TOP, total_value);
				}

				requester = "";
				title = "";
				quantity = "";
				unit_value = "";
				total_value = "";
			}

			lastQuotationId = data[0];
			requester = data[1] + "\n";
			title += data[2] + "\n";
			quantity += data[3] + "\n";
			unit_value += data[4] + "\n";
			total_value = (data[5] == null ? "-" : data[5]) + "\n";
		}
		
		if (!lastQuotationId.equals("0")) {
			ReportUtil.insertChunkedTextCellWithStrategy(
					table, ReportUtil::getNormalChunk,
					ReportUtil.LEFT_TOP.with(ReportUtil.COLSPAN.apply(2)), requester);
			ReportUtil.insertChunkedTextCellWithStrategy(
					table, ReportUtil::getNormalChunk,
					ReportUtil.LEFT_TOP.with(ReportUtil.COLSPAN.apply(2)), title);
			ReportUtil.insertChunkedTextCellWithStrategy(
					table, ReportUtil::getNormalChunk, ReportUtil.CENTER_TOP, quantity);
			ReportUtil.insertChunkedTextCellWithStrategy(
					table, ReportUtil::getNormalChunk, ReportUtil.CENTER_TOP, unit_value);
			ReportUtil.insertChunkedTextCellWithStrategy(
					table, ReportUtil::getNormalChunk, ReportUtil.CENTER_TOP, total_value);
		}
	}
	
	private void _insertHeader(PdfPTable table) {
		ReportUtil.insertChunkedTextCellWithStrategy(
				table, ReportUtil::getHeaderChunk,
				ReportUtil.BORDER_BACKGROUND_CENTER_MIDDLE.with(ReportUtil.COLSPAN.apply(2)),
				getText("administration.reports.field.requester"));
		ReportUtil.insertChunkedTextCellWithStrategy(
				table, ReportUtil::getHeaderChunk,
				ReportUtil.BORDER_BACKGROUND_CENTER_MIDDLE.with(ReportUtil.COLSPAN.apply(2)),
				getText("administration.reports.field.title"));
		ReportUtil.insertChunkedTextCellWithStrategy(
				table, ReportUtil::getHeaderChunk, ReportUtil.BORDER_BACKGROUND_CENTER_MIDDLE,
				getText("administration.reports.field.amount"));
		ReportUtil.insertChunkedTextCellWithStrategy(
				table, ReportUtil::getHeaderChunk, ReportUtil.BORDER_BACKGROUND_CENTER_MIDDLE,
				getText("administration.reports.field.unit_value"));
		ReportUtil.insertChunkedTextCellWithStrategy(
				table, ReportUtil::getHeaderChunk, ReportUtil.BORDER_BACKGROUND_CENTER_MIDDLE,
				getText("administration.reports.field.paid_value"));
	}

	@Override
	protected String getTitle() {
		return this.getText("administration.reports.title.orders_by_date");
	}

}
