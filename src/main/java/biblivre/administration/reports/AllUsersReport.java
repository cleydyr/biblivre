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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

import biblivre.administration.reports.dto.AllUsersReportDto;

public class AllUsersReport extends BaseBiblivreReport<AllUsersReportDto> {

	private static final String USER_DATA_SEPARATOR = "\t";

	@Override
	protected AllUsersReportDto getReportData(ReportsDTO dto) {
		ReportsDAO dao = ReportsDAO.getInstance(this.getSchema());
		return dao.getAllUsersReportData();
	}

	@Override
	protected void generateReportBody(Document document, AllUsersReportDto allUsers)
			throws Exception {

		_insertUserCountByTypeHeader(document);

		_insertSummaryTable(document, allUsers);

		_insertUserListByTypeHeader(document);

		_insertAllUsersTable(document, allUsers);
	}

	private void _insertAllUsersTable(Document document, AllUsersReportDto allUsers)
			throws DocumentException {

		List<PdfPTable> listTable = createListTable(allUsers.getData());

		for (PdfPTable table : listTable) {
			document.add(table);

			ReportUtil.insertNewLine(document);
		}
	}

	private void _insertUserListByTypeHeader(Document document) throws DocumentException {
		ReportUtil.insertChunkedTextParagraph(
				document, ReportUtil::getHeaderChunk, Element.ALIGN_LEFT,
				this.getText("administration.reports.field.user_list_by_type"));

		ReportUtil.insertNewLine(document);
	}

	private void _insertSummaryTable(Document document, AllUsersReportDto allUsers)
			throws DocumentException {

		PdfPTable summaryTable = createSummaryTable(allUsers.getTypesMap());

		document.add(summaryTable);

		ReportUtil.insertNewLine(document);
	}

	private void _insertUserCountByTypeHeader(Document document) throws DocumentException {
		ReportUtil.insertChunkedTextParagraph(
				document, ReportUtil::getHeaderChunk, Element.ALIGN_LEFT,
				this.getText("administration.reports.field.user_count_by_type"));

		ReportUtil.insertNewLine(document);
	}

	private final PdfPTable createSummaryTable(Map<String, Integer> types) {
		PdfPTable table = new PdfPTable(3);

		table.setWidthPercentage(50f);
		table.setHorizontalAlignment(Element.ALIGN_LEFT);

		types.forEach((description, quantity) -> {
			_insertDescription(table, description.toUpperCase());

			_insertQuantity(table, quantity);
		});

		_insertTotalLabel(table);

		int total = types
				.values()
				.stream()
				.mapToInt(Integer::intValue)
				.sum();

		_insertTotalValue(table, total);

		return table;
	}

	private void _insertQuantity(PdfPTable table, int total) {
		ReportUtil.insertLeftTextCell(table, String.valueOf(total));
	}

	private void _insertDescription(PdfPTable table, String description) {
		ReportUtil.insertHeaderCellWithBackgroundColspan2(table, description);
	}

	private void _insertTotalLabel(PdfPTable table) {
		ReportUtil.insertHeaderCellWithBackgroundColspan2(table, this.getText("administration.reports.field.total"));
	}

	private void _insertTotalValue(PdfPTable table, int total) {
		ReportUtil.insertLeftTextCell(table,  String.valueOf(total));
	}

	private final List<PdfPTable> createListTable(Map<String, List<String>> data) {
		try {
			List<PdfPTable> tables = new ArrayList<PdfPTable>();

			data.forEach((description, userDataList) -> {
				PdfPTable table = new PdfPTable(4);

				table.setWidthPercentage(100f);

				_insertUserTableDescription(description, table);

				_insertUserTableLabels(table);

				userDataList.forEach(userData -> {
					String[] userDataArray = userData.split(USER_DATA_SEPARATOR);

					ReportUtil.insertLeftTextCell(table, _getUserName(userDataArray));

					ReportUtil.insertValueCenter(table, _getUserId(userDataArray));

					ReportUtil.insertValueCenter(table, _getUserCreatedDate(userDataArray));

					ReportUtil.insertValueCenter(table, _getUserUpdateDate(userDataArray));
				});

				tables.add(table);
			});

			return tables;
		} catch (Exception e) {
			this.logger.error(e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	private void _insertUserTableLabels(PdfPTable table) {
		String userName = this.getText("administration.reports.field.user_name");

		ReportUtil.insertHeaderCellWithBackgroundAndBorder(table, userName);

		String userId = this.getText("administration.reports.field.user_id");

		ReportUtil.insertHeaderCellWithBackgroundAndBorder(table, userId);

		String created = this.getText("administration.reports.field.creation_date");

		ReportUtil.insertHeaderCellWithBackgroundAndBorder(table, created);

		String modified = this.getText("administration.reports.field.modified");

		ReportUtil.insertHeaderCellWithBackgroundAndBorder(table, modified);
	}

	public void _insertUserTableDescription(String description, PdfPTable table) {
		PdfPCell cell = new PdfPCell(
				new Paragraph(ReportUtil.getHeaderChunk(description.toUpperCase())));

		cell.setColspan(4);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

		table.addCell(cell);
	}

	private static String _getUserUpdateDate(String[] userData) {
		return userData[3];
	}

	private static String _getUserCreatedDate(String[] userData) {
		return userData[2];
	}

	private static String _getUserId(String[] dados) {
		return dados[1];
	}

	private static String _getUserName(String[] dados) {
		return dados[0];
	}

	@Override
	protected String getTitle() {
		return this.getText("administration.reports.title.all_users");
	}
	
}
