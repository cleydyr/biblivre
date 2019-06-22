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

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

import biblivre.administration.reports.dto.BaseReportDto;
import biblivre.core.file.DiskFile;
import biblivre.core.translations.TranslationsMap;

public abstract class BaseBiblivreReport<T extends BaseReportDto> extends PdfPageEventHelper implements IBiblivreReport {


	protected final Logger logger = Logger.getLogger(this.getClass().getName());

	protected static final Float HEADER_BORDER_WIDTH = 0.8f;
	protected static final Float SMALL_FONT_SIZE = 8f;
	protected static final Float REPORT_FONT_SIZE = 10f;
	protected static final String ARIAL_FONT_NAME = "Arial";
	protected static final Float PAGE_NUMBER_FONT_SIZE = 8f;

	protected static final Color HEADER_BACKGROUND_COLOR = new Color(239, 239, 239);
	protected static final Font SMALL_FONT = FontFactory.getFont(ARIAL_FONT_NAME, SMALL_FONT_SIZE, Font.NORMAL, Color.BLACK);
	protected static final Font TEXT_FONT = FontFactory.getFont(ARIAL_FONT_NAME, REPORT_FONT_SIZE, Font.NORMAL, Color.BLACK);
	protected static final Font BOLD_FONT = FontFactory.getFont(ARIAL_FONT_NAME, SMALL_FONT_SIZE, Font.BOLD, Color.BLACK);
	protected static final Font HEADER_FONT = FontFactory.getFont(ARIAL_FONT_NAME, REPORT_FONT_SIZE, Font.BOLD, Color.BLACK);
	protected static final Font FOOTER_FONT = FontFactory.getFont(FontFactory.COURIER, PAGE_NUMBER_FONT_SIZE, Font.BOLD, Color.BLACK);

	protected TranslationsMap i18n;
	private PdfWriter writer;
	private Date generationDate;
	protected DateFormat dateFormat;
	
	protected String schema; 

	@Override
	public DiskFile generateReport(ReportsDTO dto) throws IOException {
		this.generationDate = new Date();
		this.dateFormat = new SimpleDateFormat(this.getText("format.datetime"));
		T reportData = getReportData(dto);
		String fileName = this.getFileName(dto);
		return generateReportFile(reportData, fileName);
	}

	protected abstract T getReportData(ReportsDTO dto);

	protected DiskFile generateReportFile(T reportData, String fileName) throws IOException {
		Document document = new Document(PageSize.A4);
		DiskFile report = null;

		File file = File.createTempFile(fileName, ".pdf");

		try (FileOutputStream out = new FileOutputStream(file)) {
			this.writer = PdfWriter.getInstance(document, out);
			this.writer.setPageEvent(this);
			this.writer.setFullCompression();
			document.open();
			generateReportBody(document, reportData);
			this.writer.flush();
			document.close();
			report = new DiskFile(file, "application/pdf");
			report.setName(fileName);
		} catch (Exception e) {
			this.logger.error(e.getMessage(), e);
		}

		return report;
	}

	private String getFileName(ReportsDTO dto) {
		String reportName = dto.getType().getName();
		Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DATE);
		int month = c.get(Calendar.MONTH) + 1;
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		int second = c.get(Calendar.SECOND);
		return reportName + day + month + hour + minute + "_" + second;
	}

	protected abstract void generateReportBody(Document document, T reportData) throws Exception;

	@Override
	public final void setI18n(TranslationsMap i18n) {
		this.i18n = i18n;
	}

	protected final String getText(String key) {
		String[] params = key.split(":");
		
		if (params.length == 1) {
			return this.i18n.getText(key);
		}
		
		String text = this.i18n.getText(params[0]);
		for (int i = 1; i < params.length; i++) {
			String replacement = params[i];
			text = text.replace("{" + (i - 1) + "}", replacement);
		}
		return text;
	}


	@Override
	public void onEndPage(PdfWriter writer, Document document) {
		try {
			Rectangle page = document.getPageSize();
			
			PdfPTable head = new PdfPTable(1);
			PdfPCell cell = new PdfPCell(new Paragraph(this.getText("administration.reports.biblivre_report_header")));
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			cell.setBorder(Rectangle.BOTTOM);
			head.addCell(cell);
			head.setTotalWidth( (page.getWidth() / 2) - document.leftMargin());
			head.writeSelectedRows(0, -1, document.leftMargin(), page.getHeight() - document.topMargin() + head.getTotalHeight(), writer.getDirectContent());

			PdfPTable date = new PdfPTable(1);
			PdfPCell dateCell = new PdfPCell(new Paragraph(this.dateFormat.format(this.generationDate)));
			dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			dateCell.setVerticalAlignment(Element.ALIGN_CENTER);
			dateCell.setBorder(Rectangle.BOTTOM);
			date.addCell(dateCell);
			date.setTotalWidth( (page.getWidth() / 2) - document.rightMargin());
			date.writeSelectedRows(0, -1, (page.getWidth() / 2), page.getHeight() - document.topMargin() + head.getTotalHeight(), writer.getDirectContent());


			PdfPTable foot = new PdfPTable(1);
			Chunk pageNumber = new Chunk(String.valueOf(document.getPageNumber()));
			pageNumber.setFont(BaseBiblivreReport.FOOTER_FONT);
			cell = new PdfPCell(new Paragraph(pageNumber));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			cell.setBorder(Rectangle.TOP);
			foot.addCell(cell);
			foot.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
			foot.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin(), writer.getDirectContent());
		}
		catch (Exception e) {
			throw new ExceptionConverter(e);
		}
	}

	protected Chunk getNormalChunk(String text) {
		Chunk chunk = new Chunk(StringUtils.defaultIfEmpty(text, ""));
		chunk.setFont(BaseBiblivreReport.TEXT_FONT);
		return chunk;
	}

	protected Chunk getBoldChunk(String text) {
		Chunk chunk = new Chunk(StringUtils.defaultIfEmpty(text, ""));
		chunk.setFont(BaseBiblivreReport.BOLD_FONT);
		return chunk;
	}

	protected Chunk getSmallFontChunk(String text) {
		Chunk chunk = new Chunk(StringUtils.defaultIfEmpty(text, ""));
		chunk.setFont(BaseBiblivreReport.SMALL_FONT);
		return chunk;
	}

	protected Chunk getHeaderChunk(String text) {
		Chunk chunk = new Chunk(StringUtils.defaultIfEmpty(text, ""));
		chunk.setFont(BaseBiblivreReport.HEADER_FONT);
		return chunk;
	}

	protected PdfWriter getWriter() {
		return this.writer;
	}

	public String getSchema() {
		return this.schema;
	}

	@Override
	public void setSchema(String schema) {
		this.schema = schema;
	}
}
