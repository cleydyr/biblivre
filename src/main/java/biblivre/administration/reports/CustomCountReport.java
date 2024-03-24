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

import biblivre.administration.indexing.IndexingGroupBO;
import biblivre.administration.reports.dto.BaseReportDto;
import biblivre.administration.reports.dto.CustomCountDto;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.bibliographic.BiblioRecordBO;
import biblivre.cataloging.enums.RecordDatabase;
import biblivre.cataloging.enums.RecordType;
import biblivre.cataloging.search.SearchDTO;
import biblivre.marc.MarcDataReader;
import biblivre.marc.MarcUtils;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import java.util.*;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomCountReport extends BaseBiblivreReport {

    private Integer index;
    private String datafield;
    private String subfield;
    private BiblioRecordBO biblioRecordBO;

    private IndexingGroupBO indexingGroupBO;

    @Override
    protected BaseReportDto getReportData(ReportsDTO dto) {

        String marcField = dto.getMarcField();
        if (StringUtils.isNotBlank(marcField)) {
            datafield = marcField.split("\\_")[0];
            subfield = marcField.split("\\_")[1];
        }

        String order = "1";
        if (StringUtils.isNotBlank(dto.getCountOrder())) {
            order = dto.getCountOrder();
        }
        if (order.equals("2")) {
            index = 1; // field count
        } else {
            index = 0; // marc field
        }

        return getCustomCountData(dto);
    }

    private CustomCountDto getCustomCountData(ReportsDTO reportsDto) {
        CustomCountDto dto = new CustomCountDto();

        Map<String, Integer> subfieldCounter = new HashMap<>();

        int page = 0;
        int limit = 100;
        int offset = 0;

        String marcField = reportsDto.getMarcField();
        String field = marcField.split("_")[0];
        String subfield = marcField.split("_")[1];

        if (reportsDto.getSearchId() != null && reportsDto.getSearchId() != 0) {

            boolean hasMore = true;
            while (hasMore) {
                SearchDTO search = biblioRecordBO.getSearch(reportsDto.getSearchId());

                search.setSort(indexingGroupBO.getDefaultSortableGroupId(RecordType.BIBLIO));
                search.setIndexingGroup(0);
                search.getPaging().setRecordsPerPage(limit);
                search.getPaging().setPage(++page);
                biblioRecordBO.paginateSearch(search);

                if (search.isEmpty()) {
                    hasMore = false;
                } else {
                    countSubfieldValue(search, subfieldCounter, field, subfield);
                }
            }

        } else {
            RecordDatabase database = reportsDto.getDatabase();
            if (database == null) {
                database = RecordDatabase.MAIN;
            }

            boolean hasMore = true;
            while (hasMore) {
                List<RecordDTO> records = biblioRecordBO.list(offset, limit, database);
                if (records == null || records.isEmpty()) {
                    hasMore = false;
                } else {
                    offset = limit * ++page;
                    countSubfieldValue(records, subfieldCounter, field, subfield);
                }
            }
        }

        List<String[]> data = new ArrayList<>();

        for (Entry<String, Integer> entry : subfieldCounter.entrySet()) {
            String[] valuePair = new String[2];
            valuePair[0] = entry.getKey();
            valuePair[1] = String.valueOf(entry.getValue());
            data.add(valuePair);
        }

        dto.setData(data);

        return dto;
    }

    private void countSubfieldValue(
            Collection<RecordDTO> records,
            Map<String, Integer> subfieldCounter,
            String field,
            String subfield) {

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

    @Override
    protected void generateReportBody(Document document, BaseReportDto reportData) {
        CustomCountDto dto = (CustomCountDto) reportData;
        String text =
                getText("administration.reports.title.custom_count")
                        + ":\n"
                        + datafield
                        + " ("
                        + getText("marc.bibliographic.datafield." + datafield)
                        + ")\n$"
                        + subfield
                        + " ("
                        + getText(
                                "marc.bibliographic.datafield."
                                        + datafield
                                        + ".subfield."
                                        + subfield)
                        + ")";
        Paragraph p2 = new Paragraph(text);
        p2.setAlignment(Element.ALIGN_CENTER);
        document.add(p2);
        document.add(new Phrase("\n"));
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100f);
        createHeader(
                table,
                getText("marc.bibliographic.datafield." + datafield + ".subfield." + subfield));
        dto.getData()
                .sort(
                        (o1, o2) -> {
                            if (o1 == null) {
                                return 0;
                            }

                            if (o2 == null) {
                                return 0;
                            }

                            if (o1[index] == null && o2[index] == null) {
                                return 0;
                            }

                            if (index == 1) {
                                return Integer.valueOf(o2[index])
                                        .compareTo(Integer.valueOf(o1[index]));
                            }

                            return o1[index].compareTo(o2[index]);
                        });

        PdfPCell cell;
        int total = 0;
        for (String[] data : dto.getData()) {
            cell = new PdfPCell(new Paragraph(getSmallFontChunk(data[0])));
            cell.setColspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            if (data[1] != null && StringUtils.isNumeric(data[1])) {
                total += Integer.parseInt(data[1]);
            }
            cell = new PdfPCell(new Paragraph(getSmallFontChunk(data[1])));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
        }

        if (total != 0) {
            cell = new PdfPCell(new Paragraph(getBoldChunk("Total")));
            cell.setColspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(getBoldChunk(String.valueOf(total))));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
        }

        document.add(table);
    }

    private void createHeader(PdfPTable table, String title) {
        PdfPCell cell;
        cell = new PdfPCell(new Paragraph(getBoldChunk(title)));
        cell.setBackgroundColor(HEADER_BG_COLOR);
        cell.setColspan(2);
        cell.setBorderWidth(HEADER_BORDER_WIDTH);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
        cell =
                new PdfPCell(
                        new Paragraph(getBoldChunk(getText("administration.reports.field.total"))));
        cell.setBackgroundColor(HEADER_BG_COLOR);
        cell.setBorderWidth(HEADER_BORDER_WIDTH);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    @Override
    public ReportType getReportType() {
        return ReportType.CUSTOM_COUNT;
    }

    @Autowired
    public void setBiblioRecordBO(BiblioRecordBO biblioRecordBO) {
        this.biblioRecordBO = biblioRecordBO;
    }

    @Autowired
    public void setIndexingGroupBO(IndexingGroupBO indexingGroupBO) {
        this.indexingGroupBO = indexingGroupBO;
    }
}
