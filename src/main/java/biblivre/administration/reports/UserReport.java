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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

import biblivre.administration.reports.dto.UserReportDto;
import biblivre.administration.usertype.UserTypeBO;
import biblivre.administration.usertype.UserTypeDTO;
import biblivre.circulation.lending.LendingBO;
import biblivre.circulation.lending.LendingDTO;
import biblivre.circulation.lending.LendingFineBO;
import biblivre.circulation.lending.LendingInfoDTO;
import biblivre.circulation.user.UserBO;
import biblivre.circulation.user.UserDTO;
import biblivre.circulation.user.UserFieldDTO;
import biblivre.circulation.user.UserFields;
import biblivre.core.JavascriptCacheableList;


public class UserReport extends BaseBiblivreReport<UserReportDto> {
	
	public static final DateFormat dd_MM_yyyy = new SimpleDateFormat("dd/MM/yyyy");

	@Override
	protected UserReportDto getReportData(ReportsDTO dto) {
		UserReportDto urdto = new UserReportDto();
		UserBO ubo = UserBO.getInstance(this.getSchema());
		Integer userId = Integer.valueOf(dto.getUserId());
		
		UserDTO user = ubo.get(userId);
		urdto.setUser(user);

		LendingBO lbo = LendingBO.getInstance(this.getSchema());
		LendingFineBO lfbo = LendingFineBO.getInstance(this.getSchema());
		
		List<LendingDTO> history = lbo.listHistory(user);
		
		List<LendingInfoDTO> historyInfo = lbo.populateLendingInfo(history);
		List<String[]> returnedLendings = new ArrayList<String[]>();
		for (LendingInfoDTO lidto : historyInfo) {
			String[] data = new String[3];
			data[0] = dd_MM_yyyy.format(lidto.getLending().getCreated());
			data[1] = lidto.getBiblio().getTitle();
			data[2] = lidto.getBiblio().getAuthor();
			returnedLendings.add(data);
		}
		urdto.setReturnedLendings(returnedLendings);

		List<String[]> currentLendings = new ArrayList<String[]>();
		List<String[]> lateLendings = new ArrayList<String[]>();
		
		List<LendingDTO> currentLendingsList = lbo.listUserLendings(user);
		List<LendingInfoDTO> currentLendingsInfo = lbo.populateLendingInfo(currentLendingsList);
		for (LendingInfoDTO lidto : currentLendingsInfo) {
			String[] data = new String[3];
			data[0] = dd_MM_yyyy.format(lidto.getLending().getCreated());
			data[1] = lidto.getBiblio().getTitle();
			data[2] = lidto.getBiblio().getAuthor();
			if (lfbo.isLateReturn(lidto.getLending())) {
				lateLendings.add(data);
			} else {
				currentLendings.add(data);
			}
		}
		urdto.setLendings(currentLendings);
		urdto.setLateLendings(lateLendings);

		return urdto;
	}

	@Override
	protected void generateReportBody(Document document, UserReportDto reportData) throws Exception {
		PdfPTable dataTable = createUserDataTable(reportData.getUser());
		document.add(dataTable);

		Paragraph p2 = new Paragraph(this.getText("administration.reports.field.user_data"));
		p2.setAlignment(Element.ALIGN_CENTER);
		document.add(p2);
		document.add(new Phrase("\n"));
		document.add(createDateTable(reportData.getUser()));

		Paragraph p3 = null;
		if (reportData.getLendings() != null && reportData.getLendings().size() > 0) {
			document.add(new Phrase("\n"));
			p3 = new Paragraph(this.getText("administration.reports.field.user_lendings"));
			p3.setAlignment(Element.ALIGN_CENTER);
			document.add(p3);
			document.add(new Phrase("\n"));
			document.add(createLendingsTable(reportData.getLendings()));
		}

		if (reportData.getLateLendings() != null && reportData.getLateLendings().size() > 0) {
			document.add(new Phrase("\n"));
			p3 = new Paragraph(this.getText("administration.reports.field.user_late_lendings"));
			p3.setAlignment(Element.ALIGN_CENTER);
			document.add(p3);
			document.add(new Phrase("\n"));
			document.add(createLendingsTable(reportData.getLateLendings()));
		}

		if (reportData.getReturnedLendings() != null && reportData.getReturnedLendings().size() > 0) {
			document.add(new Phrase("\n"));
			p3 = new Paragraph(this.getText("administration.reports.field.user_returned_lendings"));
			p3.setAlignment(Element.ALIGN_CENTER);
			document.add(p3);
			document.add(new Phrase("\n"));
			document.add(createLendingsTable(reportData.getReturnedLendings()));
		}
	}


	private PdfPTable createLendingsTable(List<String[]> lendings) {
		PdfPTable table = new PdfPTable(5);
		table.setWidthPercentage(100f);

		PdfPCell cell;
		cell = new PdfPCell(new Paragraph(ReportUtil.getHeaderChunk(this.getText("administration.reports.field.date"))));
		cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
		cell.setBorderWidth(HEADER_BORDER_WIDTH);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(ReportUtil.getHeaderChunk(this.getText("administration.reports.field.title"))));
		cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
		cell.setColspan(2);
		cell.setBorderWidth(HEADER_BORDER_WIDTH);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(ReportUtil.getHeaderChunk(this.getText("administration.reports.field.author"))));
		cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
		cell.setColspan(2);
		cell.setBorderWidth(HEADER_BORDER_WIDTH);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);

		for (String[] lending : lendings) {
			cell = new PdfPCell(new Paragraph(ReportUtil.getNormalChunk(lending[0])));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(ReportUtil.getNormalChunk(lending[1])));
			cell.setColspan(2);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(ReportUtil.getNormalChunk(lending[2])));
			cell.setColspan(2);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
		}
		return table;
	}

	private PdfPTable createUserDataTable(UserDTO user) {
		PdfPTable table = new PdfPTable(4);
		table.setWidthPercentage(100f);
		PdfPCell cell = new PdfPCell(new Paragraph(ReportUtil.getHeaderChunk(this.getText("administration.reports.field.user_name"))));
		cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(ReportUtil.getNormalChunk(user.getName())));
		cell.setColspan(3);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph(ReportUtil.getHeaderChunk(this.getText("administration.reports.field.user_id"))));
		cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(ReportUtil.getNormalChunk(String.valueOf(user.getId()))));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph(ReportUtil.getHeaderChunk(this.getText("administration.reports.field.user_signup"))));
		cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		Date signupDate = user.getCreated();
		cell = new PdfPCell(new Paragraph(ReportUtil.getNormalChunk(signupDate != null ? format(signupDate) : "")));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		
		cell = new PdfPCell(new Paragraph(ReportUtil.getHeaderChunk(this.getText("administration.reports.field.user_type"))));
		cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		UserTypeBO utbo = UserTypeBO.getInstance(this.getSchema());
		UserTypeDTO usdto = utbo.get(user.getType());
		cell = new PdfPCell(new Paragraph(ReportUtil.getNormalChunk(usdto.getDescription())));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph(ReportUtil.getHeaderChunk(this.getText("administration.reports.field.user_status"))));
		cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(ReportUtil.getNormalChunk(this.getText("administration.reports.field.user_status." + user.getStatus().toString()))));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);

		return table;
	}

	private PdfPTable createDateTable(UserDTO user) {
		PdfPTable table = new PdfPTable(4);
		table.setWidthPercentage(100f);
		PdfPCell cell;
		JavascriptCacheableList<UserFieldDTO> fields = UserFields.getFields(this.getSchema());

		for (UserFieldDTO field : fields) {
			String fieldKey = field.getKey();
			cell = new PdfPCell(new Paragraph(ReportUtil.getHeaderChunk(this.getText("circulation.custom.user_field." + fieldKey))));
			cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(ReportUtil.getNormalChunk(user.getFields().get(fieldKey))));
			cell.setColspan(3);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
		}		

		return table;
	}

	@Override
	protected String getTitle() {
		return this.getText("administration.reports.title.user");
	}
	
}
