package biblivre.administration.reports;

import biblivre.administration.indexing.IndexingGroups;
import biblivre.administration.reports.dto.CustomCountDto;
import biblivre.cataloging.RecordDAO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.bibliographic.BiblioRecordBO;
import biblivre.cataloging.enums.RecordDatabase;
import biblivre.cataloging.enums.RecordType;
import biblivre.cataloging.search.SearchDTO;
import biblivre.core.AbstractBO;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.file.DiskFile;
import biblivre.core.translations.TranslationsMap;
import biblivre.marc.MarcDataReader;
import biblivre.marc.MarcUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

public class ReportsBO extends AbstractBO {
    private ReportsDAO reportsDAO;
    private RecordDAO recordDAO;
    private BiblioRecordBO biblioRecordBO;

    public static ReportsBO getInstance() {
        ReportsBO bo = AbstractBO.getInstance(ReportsBO.class);

        if (bo.reportsDAO == null) {
            bo.reportsDAO = ReportsDAO.getInstance();
        }

        return bo;
    }

    public DiskFile generateReport(ReportsDTO dto, TranslationsMap i18n) {
        ReportType type = dto.getType();
        IBiblivreReport report = BiblivreReportFactory.getBiblivreReport(type);
        report.setI18n(i18n);

        String schema = SchemaThreadLocal.get();

        report.setSchema(schema);
        return report.generateReport(dto);
    }

    public TreeMap<String, Set<Integer>> searchAuthors(String author, RecordDatabase database) {
        return this.reportsDAO.searchAuthors(author, database);
    }

    public CustomCountDto getCustomCountData(ReportsDTO reportsDto) {
        CustomCountDto dto = new CustomCountDto();

        Map<String, Integer> subfieldCounter = new HashMap<>();

        int page = 0;
        int limit = 100;
        int offset = limit * page;

        String marcField = reportsDto.getMarcField();
        String field = marcField.split("_")[0];
        String subfield = marcField.split("_")[1];

        if (reportsDto.getSearchId() != null && reportsDto.getSearchId() != 0) {

            boolean hasMore = true;
            while (hasMore) {
                SearchDTO search = biblioRecordBO.getSearch(reportsDto.getSearchId());

                search.setSort(IndexingGroups.getDefaultSortableGroupId(RecordType.BIBLIO));
                search.setIndexingGroup(0);
                search.getPaging().setRecordsPerPage(limit);
                search.getPaging().setPage(++page);
                biblioRecordBO.paginateSearch(search);

                if (search == null || search.size() == 0) {
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
                List<RecordDTO> records =
                        recordDAO.list(offset, limit, database, RecordType.BIBLIO);
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

	public void setReportsDAO(ReportsDAO reportsDAO) {
		this.reportsDAO = reportsDAO;
	}

	public void setRecordDAO(RecordDAO recordDAO) {
		this.recordDAO = recordDAO;
	}

	public void setBiblioRecordBO(BiblioRecordBO biblioRecordBO) {
		this.biblioRecordBO = biblioRecordBO;
	}
}
