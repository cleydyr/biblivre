package biblivre.cataloging;

import biblivre.cataloging.enums.RecordType;
import java.util.List;
import java.util.Map;

public interface TabFieldsDAO {

    List<BriefTabFieldFormatDTO> listBriefFormats(RecordType recordType);

    boolean insertBriefFormat(BriefTabFieldFormatDTO dto, RecordType recordType, int loggedUser);

    boolean updateBriefFormats(
            List<BriefTabFieldFormatDTO> briefFormats, RecordType recordType, int loggedUser);

    boolean deleteBriefFormat(String datafield, RecordType recordType);

    boolean deleteFormTabDatafield(String datafield, RecordType recordType);

    List<FormTabDatafieldDTO> listFields(RecordType recordType);

    boolean updateFormTabDatafield(
            Map<String, FormTabDatafieldDTO> formDatafields, RecordType recordType, int loggedUser);
}
