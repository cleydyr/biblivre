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

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import biblivre.administration.reports.dto.AssetHoldingDto;
import biblivre.core.utils.NaturalOrderComparator;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.Barcode39;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public class AssetHoldingFullReport extends BaseBiblivreReport<AssetHoldingDto> {

	private static final int THIRD_COLUMN_SPAN = 11;
	private static final int SECOND_COLUMN_SPAN = 3;
	private static final int FIRST_COLUMN_SPAN = 6;

	private Boolean topographic;

	public AssetHoldingFullReport(Boolean topographic) {
		this.topographic = topographic;
	}

	@Override
	protected AssetHoldingDto getReportData(ReportsDTO dto) {
		return ReportsDAO.getInstance(this.getSchema()).getAssetHoldingFullReportData();
	}

	@Override
	protected void generateReportBody(Document document, AssetHoldingDto reportData) throws Exception {
		PdfPTable table = new PdfPTable(20);

		table.setWidthPercentage(100f);

		_createHeader(table);

		List<String[]> dataList = reportData.getData();

		_sortReportData(dataList);

		for (String[] data : dataList) {
			PdfContentByte cb = getWriter().getDirectContent();

			String paddedHoldingSerial = StringUtils.leftPad(_getHoldingSerial(data), 10, "0");

			Image image39 = _toBarcodeImage(cb, paddedHoldingSerial);

			ReportUtil.insertValueCenter(
					table, (__) -> new Chunk(image39, 0, 0), null, FIRST_COLUMN_SPAN);

			ReportUtil.insertValueCenter(
					table, ReportUtil::getSmallFontChunk, _getAccesionNumber(data),
					SECOND_COLUMN_SPAN);

			_insertOtherHoldingData(table, data);
		}
		document.add(table);
	}

	private void _insertOtherHoldingData(PdfPTable table, String[] data) {
		Paragraph para = new Paragraph();
		para.add(new Phrase(ReportUtil.getSmallFontChunk(_getTitle(data) + "\n")));
		para.add(new Phrase(ReportUtil.getSmallFontChunk(StringUtils.defaultString(_getAuthorName(data)) + "\n")));

		if (StringUtils.isNotBlank(_getLocation(data))) {
			para.add(new Phrase(ReportUtil.getBoldChunk(this.getText("administration.reports.field.location") + ": ")));
			para.add(new Phrase(ReportUtil.getSmallFontChunk(_getLocation(data) + " ")));
		}

		if (StringUtils.isNotBlank(_getEdition(data))) {
			para.add(new Phrase(ReportUtil.getBoldChunk(this.getText("administration.reports.field.edition") + ": ")));
			para.add(new Phrase(ReportUtil.getSmallFontChunk(_getEdition(data) + " ")));
		}

		if (StringUtils.isNotBlank(_getPublishedYear(data))) {
			para.add(new Phrase(ReportUtil.getBoldChunk(this.getText("administration.reports.field.date") + ": ")));
			para.add(new Phrase(ReportUtil.getSmallFontChunk(_getPublishedYear(data))));
		}

		PdfPCell cell = new PdfPCell(para);

		cell.setColspan(THIRD_COLUMN_SPAN);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_TOP);
		cell.setPaddingTop(5f);
		cell.setPaddingLeft(7f);
		cell.setPaddingBottom(4f);

		table.addCell(cell);
	}

	private static String _getPublishedYear(String[] data) {
		return data[6];
	}

	private static String _getEdition(String[] data) {
		return data[5];
	}

	private static String _getLocation(String[] data) {
		return data[4];
	}

	private static String _getAuthorName(String[] data) {
		return data[3];
	}

	private static String _getTitle(String[] data) {
		return data[2];
	}

	private static String _getAccesionNumber(String[] data) {
		return data[1];
	}

	private static String _getHoldingSerial(String[] data) {
		return data[0];
	}

	private Image _toBarcodeImage(PdfContentByte cb, String holdingSerial) {
		Barcode39 code39 = new Barcode39();

		code39.setExtended(true);
		code39.setCode(holdingSerial);
		code39.setStartStopText(false);

		Image image39 = code39.createImageWithBarcode(cb, null, null);

		image39.scalePercent(100f);
		return image39;
	}

	private void _sortReportData(List<String[]> dataList) {
		Collections.sort(dataList, (o1, o2) -> {
			if (o1 == null) {
				return -1;
			}

			if (o2 == null) {
				return 1;
			}

			if (this.topographic) {
				return NaturalOrderComparator.NUMERICAL_ORDER.compare(_getLocation(o1), _getLocation(o2));
			} else {
				return NaturalOrderComparator.NUMERICAL_ORDER.compare(_getAccesionNumber(o1), _getAccesionNumber(o2));
			}
		});
	}

	private void _createHeader(PdfPTable table) {
		ReportUtil.insertTextWithBorder(
				table, ReportUtil::getBoldChunk,
				getText("administration.reports.field.id"), FIRST_COLUMN_SPAN);
		ReportUtil.insertTextWithBorder(
				table, ReportUtil::getBoldChunk,
				getText("administration.reports.field.accession_number"), SECOND_COLUMN_SPAN);
		ReportUtil.insertTextWithBorder(
				table, ReportUtil::getBoldChunk,
				getText("administration.reports.field.holdings_count"), THIRD_COLUMN_SPAN);
	}

	@Override
	protected String getTitle() {
		if (this.topographic) {
			return this.getText("administration.reports.title.topographic");
		} else {
			return this.getText("administration.reports.title.holdings_full");
		}
	}
}
