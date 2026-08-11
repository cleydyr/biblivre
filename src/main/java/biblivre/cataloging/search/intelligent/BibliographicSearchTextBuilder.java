package biblivre.cataloging.search.intelligent;

import biblivre.cataloging.RecordDTO;
import biblivre.marc.MarcDataReader;
import biblivre.marc.MarcUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.marc.Record;
import org.springframework.stereotype.Component;

@Component
public class BibliographicSearchTextBuilder {
    public String build(RecordDTO dto) {
        if (dto == null || dto.getIso2709() == null || dto.getIso2709().length == 0) {
            return "";
        }

        Record record = MarcUtils.iso2709ToRecord(dto.getIso2709());
        if (record == null) {
            return "";
        }

        MarcDataReader reader = new MarcDataReader(record);
        List<String> parts = new ArrayList<>();
        add(parts, reader.getTitle(true));
        add(parts, reader.getAuthor(true));
        add(parts, reader.getSubject(true));
        add(parts, reader.getEditor());
        add(parts, reader.getPublicationYear());
        add(parts, reader.getIsbn());
        add(parts, reader.getIssn());

        return String.join(" | ", parts);
    }

    private static void add(List<String> parts, String value) {
        if (StringUtils.isNotBlank(value)) {
            parts.add(value.trim());
        }
    }
}
