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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

import biblivre.administration.indexing.IndexingGroups;
import biblivre.administration.reports.dto.BaseReportDto;
import biblivre.administration.reports.dto.CustomCountDto;
import biblivre.cataloging.RecordBO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.bibliographic.BiblioRecordDAO;
import biblivre.cataloging.enums.RecordDatabase;
import biblivre.cataloging.enums.RecordType;
import biblivre.cataloging.search.SearchDTO;
import biblivre.marc.MarcDataReader;
import biblivre.marc.MarcUtils;

public class CustomCountReport extends BaseBiblivreReport implements Comparator<String[]> {

	public CustomCountReport(ReportsDAO reportsDAO) {
		super(reportsDAO);
	}

	private Integer index;
	private String marcField;
	private String datafield;
	private String subfield;

	@Override
	protected BaseReportDto getReportData(ReportsDTO dto) {
		
		this.marcField = dto.getMarcField();
		if (StringUtils.isNotBlank(this.marcField)) {
			this.datafield = this.marcField.split("\\_")[0];
			this.subfield = this.marcField.split("\\_")[1];
		}
		
		String order = "1";
		if (StringUtils.isNotBlank(dto.getCountOrder())) {
			order = dto.getCountOrder();
		}
		if (order.equals("2")) {
			this.index = 1; //field count
		} else {
			this.index = 0; //marc field
		}
		return this.getCustomCountData(dto);
	}

	@Override
	protected void generateReportBody(Document document, BaseReportDto reportData) throws Exception {
		CustomCountDto dto = (CustomCountDto)reportData;
		StringBuilder text = new StringBuilder();
		text.append(this.getText("administration.reports.title.custom_count") + ":\n");
		text.append(this.datafield).append(" (");
		text.append(this.getText("marc.bibliographic.datafield." + this.datafield));
		text.append(")\n$").append(this.subfield);
		text.append(" (");
		text.append(this.getText("marc.bibliographic.datafield." + this.datafield + ".subfield." + this.subfield));
		text.append(")");
		Paragraph p2 = new Paragraph(text.toString());
		p2.setAlignment(Element.ALIGN_CENTER);
		document.add(p2);
		document.add(new Phrase("\n"));
		PdfPTable table = new PdfPTable(3);
		table.setWidthPercentage(100f);
		createHeader(table, this.getText("marc.bibliographic.datafield." + this.datafield + ".subfield." + this.subfield));
		Collections.sort(dto.getData(), this);
		PdfPCell cell;
		int total = 0;
		for (String[] data : dto.getData()) {
			cell = new PdfPCell(new Paragraph(this.getSmallFontChunk(data[0])));
			cell.setColspan(2);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			if (data[1] != null && StringUtils.isNumeric(data[1])) {
				total += Integer.valueOf(data[1]);
			}
			cell = new PdfPCell(new Paragraph(this.getSmallFontChunk(data[1])));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
		}
		
		if (total != 0) {
			cell = new PdfPCell(new Paragraph(this.getBoldChunk("Total")));
			cell.setColspan(2);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(this.getBoldChunk(String.valueOf(total))));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
		}
		
		document.add(table);
	}

	private void createHeader(PdfPTable table, String title) {
		PdfPCell cell;
		cell = new PdfPCell(new Paragraph(this.getBoldChunk(title)));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setColspan(2);
		cell.setBorderWidth(this.headerBorderWidth);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(this.getBoldChunk(this.getText("administration.reports.field.total"))));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setBorderWidth(this.headerBorderWidth);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
	}


	@Override
	public int compare(String[] o1, String[] o2) {
		if (o1 == null) {
			return 0;
		}
		
		if (o2 == null) {
			return 0;
		}
		
		if (o1[this.index] == null && o2[this.index] == null) {
			return 0;
		}
		
		switch (this.index) {
		case 0:
			return o1[this.index].compareTo(o2[this.index]);
		case 1:
			return (Integer.valueOf(o2[this.index]).compareTo(Integer.valueOf(o1[this.index])));
		default:
			return o1[this.index].compareTo(o2[this.index]);
		}
			
		
	}

	public CustomCountDto getCustomCountData(ReportsDTO reportsDto) {
		CustomCountDto dto = new CustomCountDto();
		
		Map<String, Integer> subfieldCounter = new HashMap<String, Integer>();
		
		int page = 0;
		int limit = 100;
		int offset = limit * page;
		
		String marcField = reportsDto.getMarcField();
		String field = marcField.split("_")[0];
		String subfield = marcField.split("_")[1];
		
		if (reportsDto.getSearchId() != null && reportsDto.getSearchId() != 0) {
			
			RecordBO bo = RecordBO.getInstance(this.getSchema(), RecordType.BIBLIO);

			boolean hasMore = true;
			while (hasMore) {
				SearchDTO search = bo.getSearch(reportsDto.getSearchId());
				search.setSort(IndexingGroups.getDefaultSortableGroupId(this.getSchema(), RecordType.BIBLIO));
				search.setIndexingGroup(0);
				search.getPaging().setRecordsPerPage(limit);
				search.getPaging().setPage(++page);
				bo.paginateSearch(search);
				
				if (search == null || search.size() == 0) {
					hasMore = false;
				} else {
					countSubfieldValue(search, subfieldCounter, field, subfield);
				}
			}
			
		} else {
			BiblioRecordDAO bdao = BiblioRecordDAO.getInstance(this.getSchema());
			
			RecordDatabase database = reportsDto.getDatabase();
			if (database == null) {
				database = RecordDatabase.MAIN;
			}
			
			boolean hasMore = true;
			while (hasMore) {
				List<RecordDTO> records = bdao.list(offset, limit, database);
				if (records == null || records.size() == 0) {
					hasMore = false;
				} else {
					offset = limit * ++page;
					countSubfieldValue(records, subfieldCounter, field, subfield);
				}
			}
		}
		
		List<String[]> data = new ArrayList<String[]>();
		
		for (String key : subfieldCounter.keySet()) {
			String[] valuePair = new String[2];
			valuePair[0] = key;
			valuePair[1] = String.valueOf(subfieldCounter.get(key));
			data.add(valuePair);
		}
		
		dto.setData(data);
		
		return dto;
	}

	private void countSubfieldValue(Collection<RecordDTO> records, Map<String, Integer> subfieldCounter, String field, String subfield) {
		
		for (RecordDTO biblio : records) {
			Record record = MarcUtils.iso2709ToRecord(biblio.getIso2709());
			MarcDataReader reader = new MarcDataReader(record);
			List<DataField> datafields = reader.getDataFields(field);
			
			for (DataField df : datafields) {
				List<Subfield> subfields = df.getSubfields(subfield.charAt(0));
				
				for (Subfield sf : subfields) {
					String value = sf.getData();
					Integer count = subfieldCounter.get(value);
					if (count == null) {
						count = 0;
					}
					subfieldCounter.put(value, ++count);
				}
				
			}
		}
	}
}
