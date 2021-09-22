package biblivre.cataloging;

import java.util.HashMap;
import java.util.List;

import biblivre.cataloging.enums.RecordType;

public interface TabFieldsDAO {

	List<BriefTabFieldFormatDTO> listBriefFormats(RecordType recordType);

	boolean insertBriefFormat(BriefTabFieldFormatDTO dto, RecordType recordType, int loggedUser);

	boolean updateBriefFormats(List<BriefTabFieldFormatDTO> briefFormats, RecordType recordType, int loggedUser);

	boolean deleteBriefFormat(String datafield, RecordType recordType);

	boolean deleteFormTabDatafield(String datafield, RecordType recordType);

	List<FormTabDatafieldDTO> listFields(RecordType recordType);

	boolean updateFormTabDatafield(HashMap<String, FormTabDatafieldDTO> formDatafields, RecordType recordType,
			int loggedUser);

}