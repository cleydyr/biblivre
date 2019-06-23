package biblivre.administration.reports;

import java.awt.Color;

import org.apache.commons.lang3.StringUtils;

import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;

public class ReportUtil {
	
	public static final Float SMALL_FONT_SIZE = 8f;
	public static final Float REPORT_FONT_SIZE = 10f;
	public static final Float PAGE_NUMBER_FONT_SIZE = 8f;
	public static final String ARIAL_FONT_NAME = "Arial";
	public static final Font SMALL_FONT = FontFactory.getFont(ARIAL_FONT_NAME, SMALL_FONT_SIZE, Font.NORMAL, Color.BLACK);
	public static final Font TEXT_FONT = FontFactory.getFont(ARIAL_FONT_NAME, REPORT_FONT_SIZE, Font.NORMAL, Color.BLACK);
	public static final Font BOLD_FONT = FontFactory.getFont(ARIAL_FONT_NAME, SMALL_FONT_SIZE, Font.BOLD, Color.BLACK);
	public static final Font HEADER_FONT = FontFactory.getFont(ARIAL_FONT_NAME, REPORT_FONT_SIZE, Font.BOLD, Color.BLACK);
	public static final Font FOOTER_FONT = FontFactory.getFont(FontFactory.COURIER, PAGE_NUMBER_FONT_SIZE, Font.BOLD, Color.BLACK);

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

}
