package biblivre.administration.reports;

import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public class ReportUtil {
	enum ParagraphAlignment {
		LEFT(Element.ALIGN_LEFT),
		RIGHT(Element.ALIGN_RIGHT),
		CENTER(Element.ALIGN_CENTER);

		private int value;

		private ParagraphAlignment(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	public static final Float SMALL_FONT_SIZE = 8f;
	public static final Float REPORT_FONT_SIZE = 10f;
	public static final Float PAGE_NUMBER_FONT_SIZE = 8f;
	public static final String ARIAL_FONT_NAME = "Arial";
	public static final Font SMALL_FONT = FontFactory.getFont(ARIAL_FONT_NAME, SMALL_FONT_SIZE, Font.NORMAL, Color.BLACK);
	public static final Font TEXT_FONT = FontFactory.getFont(ARIAL_FONT_NAME, REPORT_FONT_SIZE, Font.NORMAL, Color.BLACK);
	public static final Font BOLD_FONT = FontFactory.getFont(ARIAL_FONT_NAME, SMALL_FONT_SIZE, Font.BOLD, Color.BLACK);
	public static final Font HEADER_FONT = FontFactory.getFont(ARIAL_FONT_NAME, REPORT_FONT_SIZE, Font.BOLD, Color.BLACK);
	public static final Font FOOTER_FONT = FontFactory.getFont(FontFactory.COURIER, PAGE_NUMBER_FONT_SIZE, Font.BOLD, Color.BLACK);
	public static final Color HEADER_BACKGROUND_COLOR = new Color(239, 239, 239);
	public static final Float HEADER_BORDER_WIDTH = 0.8f;

	public static final CellFormattingStrategy BORDER = cell -> {
		cell.setBorderWidth(HEADER_BORDER_WIDTH);
	};

	public static final CellFormattingStrategy BACKGROUND = cell -> {
		cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
	};

	public static final Function<Integer, CellFormattingStrategy> COLSPAN =
			colspan ->  cell -> cell.setColspan(colspan);

	private static final Function<Integer, CellFormattingStrategy> HALIGN =
			halignment -> cell -> cell.setHorizontalAlignment(halignment);

	public static final CellFormattingStrategy CENTER = HALIGN.apply(Element.ALIGN_CENTER);

	public static final CellFormattingStrategy LEFT = HALIGN.apply(Element.ALIGN_LEFT);

	public static final CellFormattingStrategy RIGHT = HALIGN.apply(Element.ALIGN_RIGHT);

	private static final Function<Integer, CellFormattingStrategy> VALIGN =
			valignment -> cell -> cell.setVerticalAlignment(valignment);


	public static final CellFormattingStrategy TOP = VALIGN.apply(Element.ALIGN_TOP);

	public static final CellFormattingStrategy LEFT_TOP = TOP.with(LEFT);

	public static final CellFormattingStrategy CENTER_TOP = TOP.with(CENTER);

	public static final CellFormattingStrategy MIDDLE = VALIGN.apply(Element.ALIGN_MIDDLE);

	public static final CellFormattingStrategy CENTER_MIDDLE =
			ReportUtil.CENTER.with(ReportUtil.MIDDLE);

	public static final CellFormattingStrategy LEFT_MIDDLE =
			ReportUtil.LEFT.with(ReportUtil.MIDDLE);

	public static final CellFormattingStrategy BACKGROUND_LEFT_MIDDLE =
			ReportUtil.BACKGROUND.with(LEFT_MIDDLE);

	public static final CellFormattingStrategy BORDER_BACKGROUND_LEFT_MIDDLE =
			BACKGROUND_LEFT_MIDDLE.with(BORDER);

	public static final CellFormattingStrategy BACKGROUND_CENTER_MIDDLE =
			CENTER_MIDDLE.with(BACKGROUND);

	public static final CellFormattingStrategy BORDER_BACKGROUND_CENTER_MIDDLE =
			BACKGROUND_CENTER_MIDDLE.with(BORDER);

	public static Chunk getNormalChunk(String text) {
		Chunk chunk = new Chunk(StringUtils.defaultIfEmpty(text, ""));
		chunk.setFont(TEXT_FONT);
		return chunk;
	}

	public static Chunk getBoldChunk(String text) {
		Chunk chunk = new Chunk(StringUtils.defaultIfEmpty(text, ""));
		chunk.setFont(BOLD_FONT);
		return chunk;
	}

	public static Chunk getSmallFontChunk(String text) {
		Chunk chunk = new Chunk(StringUtils.defaultIfEmpty(text, ""));
		chunk.setFont(SMALL_FONT);
		return chunk;
	}

	public static Chunk getHeaderChunk(String text) {
		Chunk chunk = new Chunk(StringUtils.defaultIfEmpty(text, ""));
		chunk.setFont(HEADER_FONT);
		return chunk;
	}

	public static void insertNewLine(Document document) throws DocumentException {
		document.add(new Phrase("\n"));
	}

	public static void insertChunkedTextParagraph(
			Document document, Function<String, Chunk> chunker, int alignment, String text)
		throws DocumentException {

		Paragraph p2 = new Paragraph(chunker.apply(text));

		p2.setAlignment(alignment);

		document.add(p2);
	}

	public static void insertChunkedTextParagraph(
			Document document, Function<String, Chunk> chunker, ParagraphAlignment alignment, String text)
		throws DocumentException {

		Paragraph p2 = new Paragraph(chunker.apply(text));

		p2.setAlignment(alignment.value);

		document.add(p2);
	}

	public static void insertHeaderCellWithBackgroundColspan2(PdfPTable table, String text) {
		insertHeaderCellWithBackground(table, text, 2);
	}

	public static void insertHeaderCellWithBackground(PdfPTable table, String text, int colspan) {
		insertHeaderCenterTextCellWithBackgroundAndColspan(table, text, colspan);
	}

	public static void insertHeaderCenterTextCellWithBackgroundAndColspan(
			PdfPTable table, String text, int colspan) {
		CellFormattingStrategy strategy =
			BACKGROUND
				.with(CENTER)
				.with(MIDDLE)
				.with(COLSPAN.apply(colspan));

		insertChunkedTextCellWithStrategy(table, ReportUtil::getHeaderChunk, strategy, text);
	}

	public static void insertLeftTextCell(PdfPTable table, String value) {
		insertTextCell(table, value, Element.ALIGN_LEFT);
	}

	public static void insertCenterTextCell(PdfPTable table, String value) {
		insertTextCell(table, value, Element.ALIGN_CENTER);
	}

	public static void insertTextCell(PdfPTable table, String value, int halignment) {
		CellFormattingStrategy strategy = HALIGN.apply(halignment).with(MIDDLE);

		insertChunkedTextCellWithStrategy(table, ReportUtil::getNormalChunk, strategy, value);
	}

	public static void insertValueCenter(PdfPTable table, String value) {
		insertChunkedCenterTextCell(table, ReportUtil::getNormalChunk, value);
	}

	public static void insertChunkedCenterTextCell(
			PdfPTable table, Function<String, Chunk> chunker, String value, int colspan) {
		insertChunkedTextCell(table, chunker, value, colspan, Element.ALIGN_CENTER);
	}

	public static void insertChunkedTextCell(
			PdfPTable table, Function<String, Chunk> chunker, String value, int colspan,
			int halignment) {

		CellFormattingStrategy strategy =
			MIDDLE
				.with(COLSPAN.apply(colspan))
				.with(HALIGN.apply(halignment))
				.with(MIDDLE);

		insertChunkedTextCellWithStrategy(table, chunker, strategy, value);
	}

	public static void insertChunkedCenterTextCell(
			PdfPTable table, Function<String, Chunk> chunker, String value) {
		insertChunkedCenterTextCell(table, chunker, value, 1);
	}

	public static void insertHeaderCellWithBackgroundAndBorder(PdfPTable table, String value) {
		insertChunkedCenterTextCellWithBackgroundAndBorder(table, ReportUtil::getHeaderChunk, value, 1);
	}

	public static void insertChunkedCenterTextCellWithBackgroundAndBorder(
			PdfPTable table, String value, int colspan) {
		insertChunkedCenterTextCellWithBackgroundAndBorder(
				table, ReportUtil::getHeaderChunk, value, colspan);
	}

	public static void insertChunkedCenterTextCellWithBackgroundAndBorder(
			PdfPTable table, Function<String, Chunk> chunker, String value) {
		insertChunkedCenterTextCellWithBackgroundAndBorder(table, chunker, value, 1);
	}

	public static void insertChunkedCenterTextCellWithBackgroundAndBorder(
			PdfPTable table, Function<String, Chunk> chunker, String value, int colspan) {
		insertChunkedTextCellWithBackgroundAndBorder(
				table, chunker, value, colspan, Element.ALIGN_CENTER);
	}

	public static void insertChunkedTextCellWithBackgroundAndBorder(
			PdfPTable table, Function<String, Chunk> chunker, String value, int colspan,
			int halignment) {

		CellFormattingStrategy strategy =
			BORDER
			.with(BACKGROUND)
			.with(COLSPAN.apply(colspan))
			.with(HALIGN.apply(halignment))
			.with(MIDDLE);

		insertChunkedTextCellWithStrategy(table, chunker, strategy, value);
	}

	public static void insertChunkedTextCellWithStrategy(
			PdfPTable table, Function<String, Chunk> chunker, CellFormattingStrategy strategy,
			String value) {

		PdfPCell cell = new PdfPCell(new Paragraph(chunker.apply(value)));

		strategy.accept(cell);

		table.addCell(cell);
	}

	public static void insertHeaderCellWithBackground(PdfPTable table, String text) {
		insertHeaderCellWithBackground(table, text, 1);
	}

	interface CellFormattingStrategy extends Consumer<PdfPCell> {
		public default CellFormattingStrategy with(CellFormattingStrategy that) {
			return cell -> {
				this.accept(cell);
				that.accept(cell);
			};
		}
	}
}
