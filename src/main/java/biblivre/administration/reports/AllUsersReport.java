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

	@Override
	protected AllUsersReportDto getReportData(ReportsDTO dto) {
		ReportsDAO dao = ReportsDAO.getInstance(this.getSchema());
		return dao.getAllUsersReportData();
	}

	@Override
	protected void generateReportBody(Document document, AllUsersReportDto allUsers)
			throws Exception {

		_insertReportTitle(document);

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
		ReportUtil.insertChunkText(
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
		ReportUtil.insertChunkText(
				document, ReportUtil::getHeaderChunk, Element.ALIGN_LEFT,
				this.getText("administration.reports.field.user_count_by_type"));

		ReportUtil.insertNewLine(document);
	}

	private void _insertReportTitle(Document document) throws DocumentException {
		Paragraph p1 = new Paragraph(this.getText("administration.reports.title.all_users"));

		p1.setAlignment(Element.ALIGN_CENTER);

		document.add(p1);

		ReportUtil.insertNewLine(document);
	}

	private final PdfPTable createSummaryTable(Map<String, Integer> types) {
		PdfPTable table = new PdfPTable(3);

		table.setWidthPercentage(50f);
		table.setHorizontalAlignment(Element.ALIGN_LEFT);

		types.forEach((description, quantity) -> {
			_insertDescription(table, description);

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

	private void _insertQuantity(PdfPTable table, Integer quantity) {
		PdfPCell quantityCell = new PdfPCell(
				new Paragraph(ReportUtil.getNormalChunk(String.valueOf(quantity))));

		quantityCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		quantityCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

		table.addCell(quantityCell);
	}

	private void _insertDescription(PdfPTable table, String description) {
		PdfPCell descriptionLabelCell = new PdfPCell(
				new Paragraph(ReportUtil.getHeaderChunk(description.toUpperCase())));

		descriptionLabelCell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
		descriptionLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		descriptionLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		descriptionLabelCell.setColspan(2);

		table.addCell(descriptionLabelCell);
	}

	private void _insertTotalLabel(PdfPTable table) {
		PdfPCell totalCell = new PdfPCell(
				new Paragraph(
						ReportUtil.getHeaderChunk(
								this.getText("administration.reports.field.total"))));

		totalCell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
		totalCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		totalCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		totalCell.setColspan(2);

		table.addCell(totalCell);
	}

	private void _insertTotalValue(PdfPTable table, int total) {
		PdfPCell cell2 = new PdfPCell(
				new Paragraph(ReportUtil.getNormalChunk(String.valueOf(total))));

		cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);

		table.addCell(cell2);
	}

	private final List<PdfPTable> createListTable(Map<String, List<String>> data) {
		try {
			List<PdfPTable> tables = new ArrayList<PdfPTable>();
			PdfPTable table = null;
			PdfPCell cell;
			for (String description : data.keySet()) {
				table = new PdfPTable(4);
				table.setWidthPercentage(100f);
				cell = new PdfPCell(new Paragraph(ReportUtil.getHeaderChunk(description.toUpperCase())));
				cell.setColspan(4);
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				table.addCell(cell);

				cell = new PdfPCell(new Paragraph(ReportUtil.getHeaderChunk(this.getText("administration.reports.field.user_name"))));
				cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
				cell.setBorderWidth(HEADER_BORDER_WIDTH);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				table.addCell(cell);

				cell = new PdfPCell(new Paragraph(ReportUtil.getHeaderChunk(this.getText("administration.reports.field.user_id"))));
				cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
				cell.setBorderWidth(HEADER_BORDER_WIDTH);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				table.addCell(cell);

				cell = new PdfPCell(new Paragraph(ReportUtil.getHeaderChunk(this.getText("administration.reports.field.creation_date"))));
				cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
				cell.setBorderWidth(HEADER_BORDER_WIDTH);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				table.addCell(cell);

				cell = new PdfPCell(new Paragraph(ReportUtil.getHeaderChunk(this.getText("administration.reports.field.modified"))));
				cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
				cell.setBorderWidth(HEADER_BORDER_WIDTH);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				table.addCell(cell);

				for (String line : data.get(description)) {
					String[] dados = line.split("\t");
					//Nome
					cell = new PdfPCell(new Paragraph(ReportUtil.getNormalChunk(dados[0])));
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					table.addCell(cell);

					//Matricula
					cell = new PdfPCell(new Paragraph(ReportUtil.getNormalChunk(dados[1])));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					table.addCell(cell);

					//Data de Inclusao
					cell = new PdfPCell(new Paragraph(ReportUtil.getNormalChunk(dados[2])));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					table.addCell(cell);

					//Data de Cancelamento/Alteracao
					cell = new PdfPCell(new Paragraph(ReportUtil.getNormalChunk(dados[3])));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					table.addCell(cell);
				}
				tables.add(table);
			}
			return tables;
		} catch (Exception e) {
			this.logger.error(e.getMessage(), e);
			return Collections.emptyList();
		}
	}
	
}
