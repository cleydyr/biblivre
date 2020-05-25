package biblivre.marc;

import java.util.List;

import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;

import biblivre.cataloging.BriefTabFieldDTO;
import biblivre.cataloging.BriefTabFieldFormatDTO;
import biblivre.cataloging.RecordAttachmentDTO;

public interface MarcDataReader {

	Record getRecord();

	List<BriefTabFieldDTO> getFieldList(List<BriefTabFieldFormatDTO> dataFieldFormats);

	List<RecordAttachmentDTO> getAttachments();

	String getAuthor();

	String getAuthors();

	String getAuthorName();

	String getAuthorNames();

	String getAuthorOtherNames();

	String getTitle();

	String getIsbn();

	String getIssn();

	String getIsrc();

	String getPublicationYear();

	String getShelfLocation();

	String getHoldingLocation();

	String getLocation();

	String getLocationB();

	String getLocationC();

	String getLocationD();

	String getDDCN();

	String getSourceAcquisitionDate();

	String getSubjects();

	String getEditor();

	String getEdition();

	String getAccessionNumber();

	List<DataField> getDataFields(String tag);

	String getFirstSubfieldData(String tag, char subfield);

	String getFirstSubfieldData(DataField datafield, char subfield);
}