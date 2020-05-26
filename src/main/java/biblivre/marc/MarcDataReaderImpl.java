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
package biblivre.marc;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

import biblivre.cataloging.BriefTabFieldDTO;
import biblivre.cataloging.BriefTabFieldFormatDTO;
import biblivre.cataloging.RecordAttachmentDTO;
import biblivre.core.utils.StringPool;
import biblivre.core.utils.TextUtils;

public class MarcDataReaderImpl implements MarcDataReader {
	private static final BriefTabFieldFormatDTO ACCESSION_NUMBER_BTFF = new BriefTabFieldFormatDTO(MarcConstants.ACCESSION_NUMBER, "${a}");
	private static final BriefTabFieldFormatDTO EDITION_BTFF = new BriefTabFieldFormatDTO(MarcConstants.EDITION, "${a}");
	private static final BriefTabFieldFormatDTO EDITOR_BTFF = new BriefTabFieldFormatDTO(MarcConstants.PUBLICATION, "${b}");
	private static final BriefTabFieldFormatDTO SUBJECT_ADDED_ENTRY_LOCAL_BTFF = new BriefTabFieldFormatDTO(MarcConstants.SUBJECT_ADDED_ENTRY_LOCAL, "${a}_{ - }${x}_{ - }${y}_{ - }${z}");
	private static final BriefTabFieldFormatDTO SUBJECT_ADDED_ENTRY_GEOGRAPHIC_NAME_BTFF = new BriefTabFieldFormatDTO(MarcConstants.SUBJECT_ADDED_ENTRY_GEOGRAPHIC_NAME, "${a}_{ - }${x}_{ - }${y}_{ - }${z}");
	private static final BriefTabFieldFormatDTO SUBJECT_ADDED_ENTRY_TOPICAL_TERM_BTFF = new BriefTabFieldFormatDTO(MarcConstants.SUBJECT_ADDED_ENTRY_TOPICAL_TERM, "${a}_{ - }${x}_{ - }${y}_{ - }${z}");
	private static final BriefTabFieldFormatDTO SUBJECT_ADDED_ENTRY_UNIFORM_TITLE_BTFF = new BriefTabFieldFormatDTO(MarcConstants.SUBJECT_ADDED_ENTRY_UNIFORM_TITLE, "${a}_{ - }${x}_{ - }${y}_{ - }${z}");
	private static final BriefTabFieldFormatDTO SUBJECT_ADDED_ENTRY_MEETING_NAME_BTFF = new BriefTabFieldFormatDTO(MarcConstants.SUBJECT_ADDED_ENTRY_MEETING_NAME, "${a}_{ - }${x}_{ - }${y}_{ - }${z}");
	private static final BriefTabFieldFormatDTO SUBJECT_ADDED_ENTRY_CORPORATE_NAME_BTFF = new BriefTabFieldFormatDTO(MarcConstants.SUBJECT_ADDED_ENTRY_CORPORATE_NAME, "${a}_{ - }${x}_{ - }${y}_{ - }${z}");
	private static final BriefTabFieldFormatDTO SUBJECT_ADDED_ENTRY_PERSONAL_NAME_BTFF = new BriefTabFieldFormatDTO(MarcConstants.SUBJECT_ADDED_ENTRY_PERSONAL_NAME, "${a}_{ - }${x}_{ - }${y}_{ - }${z}");
	private static final BriefTabFieldFormatDTO SOURCE_ACQUISITION_NOTES_BTFF = new BriefTabFieldFormatDTO(MarcConstants.SOURCE_ACQUISITION_NOTES, "${d}");
	private static final BriefTabFieldFormatDTO DDCN_BTFF = new BriefTabFieldFormatDTO(MarcConstants.DDCN, "${a}");
	private static final BriefTabFieldFormatDTO LOCATION_C_BTFF = new BriefTabFieldFormatDTO(MarcConstants.SHELF_LOCATION, "${c}");
	private static final BriefTabFieldFormatDTO LOCATION_B_BTFF = new BriefTabFieldFormatDTO(MarcConstants.SHELF_LOCATION, "${b}");
	private static final BriefTabFieldFormatDTO LOCATION_BTFF = new BriefTabFieldFormatDTO(MarcConstants.SHELF_LOCATION, "${a}");
	private static final BriefTabFieldFormatDTO HOLDING_LOCATION_BTFF = new BriefTabFieldFormatDTO(MarcConstants.SHELF_LOCATION, "${d}");
	private static final BriefTabFieldFormatDTO SHELF_LOCATION_BTFF = new BriefTabFieldFormatDTO(MarcConstants.SHELF_LOCATION, "${a}_{ }${b}_{ }${c}");
	private static final BriefTabFieldFormatDTO PUBLICATION_FUNCTIONS_BTFF = new BriefTabFieldFormatDTO(MarcConstants.PUBLICATION_FUNCTIONS, "${c}");
	private static final BriefTabFieldFormatDTO PUBLICATION_BTFF = new BriefTabFieldFormatDTO(MarcConstants.PUBLICATION, "${c}");
	private static final BriefTabFieldFormatDTO ISRC_BTFF = new BriefTabFieldFormatDTO(MarcConstants.ISRC, "${a}");
	private static final BriefTabFieldFormatDTO ISSN_BTFF = new BriefTabFieldFormatDTO(MarcConstants.ISSN, "${a}");
	private static final BriefTabFieldFormatDTO ISBN_BTFF = new BriefTabFieldFormatDTO(MarcConstants.ISBN, "${a}");
	private static final BriefTabFieldFormatDTO SECONDARY_INPUT_SERIAL_TITLE_BTFF = new BriefTabFieldFormatDTO(MarcConstants.SECONDARY_INPUT_SERIAL_TITLE, "${a}_{ }${v}");
	private static final BriefTabFieldFormatDTO ADDED_ANALYTICAL_TITLE_BTFF = new BriefTabFieldFormatDTO(MarcConstants.ADDED_ANALYTICAL_TITLE, "${a}_{ }${n}_{ }${p}");
	private static final BriefTabFieldFormatDTO ADDED_UNIFORM_TITLE_BTFF = new BriefTabFieldFormatDTO(MarcConstants.ADDED_UNIFORM_TITLE, "${a}");
	private static final BriefTabFieldFormatDTO UNIFORM_TITLE_BTFF = new BriefTabFieldFormatDTO(MarcConstants.UNIFORM_TITLE, "${a}");
	private static final BriefTabFieldFormatDTO COLLECTIVE_UNIFORM_TITLE_BTFF = new BriefTabFieldFormatDTO(MarcConstants.COLLECTIVE_UNIFORM_TITLE, "${a}_{ }${f}");
	private static final BriefTabFieldFormatDTO TITLE_BTFF = new BriefTabFieldFormatDTO(MarcConstants.TITLE, "${a}_{: }${b}");
	private static final BriefTabFieldFormatDTO SECONDARY_AUTHOR_CONGRESS_NAME_BTFF = new BriefTabFieldFormatDTO(MarcConstants.SECONDARY_AUTHOR_CONGRESS_NAME, "${a}");
	private static final BriefTabFieldFormatDTO SECONDARY_AUTHOR_CORPORATION_NAME_BTFF = new BriefTabFieldFormatDTO(MarcConstants.SECONDARY_AUTHOR_CORPORATION_NAME, "${a}");
	private static final BriefTabFieldFormatDTO SECONDARY_AUTHOR_PERSONAL_NAME_BTFF = new BriefTabFieldFormatDTO(MarcConstants.SECONDARY_AUTHOR_PERSONAL_NAME, "${a}");
	private static final BriefTabFieldFormatDTO AUTHOR_OTHER_CONGRESS_NAMES_BTFF = new BriefTabFieldFormatDTO(MarcConstants.AUTHOR_OTHER_CONGRESS_NAMES, "${a}");
	private static final BriefTabFieldFormatDTO AUTHOR_OTHER_CORPORATION_NAMES_BTFF = new BriefTabFieldFormatDTO(MarcConstants.AUTHOR_OTHER_CORPORATION_NAMES, "${a}");
	private static final BriefTabFieldFormatDTO AUTHOR_OTHER_PERSONAL_NAMES_BTFF = new BriefTabFieldFormatDTO(MarcConstants.AUTHOR_OTHER_PERSONAL_NAMES, "${a}");
	private static final BriefTabFieldFormatDTO AUTHOR_CONGRESS_NAME_BTFF = new BriefTabFieldFormatDTO(MarcConstants.AUTHOR_CONGRESS_NAME, "${a}");
	private static final BriefTabFieldFormatDTO AUTHOR_CORPORATION_NAME_BTFF = new BriefTabFieldFormatDTO(MarcConstants.AUTHOR_CORPORATION_NAME, "${a}");
	private static final BriefTabFieldFormatDTO AUTHOR_PERSONAL_NAME_BTFF = new BriefTabFieldFormatDTO(MarcConstants.AUTHOR_PERSONAL_NAME, "${a}");

	private Record record;
	private Map<String, List<DataField>> cache;
	
	public MarcDataReaderImpl(Record record) {
		this.record = record;
		this.cache = _readDataFieldMap(record);
	}
	
	@Override
	public Record getRecord() {
		return record;
	}

	@Override
	public List<BriefTabFieldDTO> getFieldList(List<BriefTabFieldFormatDTO> dataFieldFormats) {
		if (dataFieldFormats == null) {
			throw new IllegalArgumentException("dataFieldFormats can't be null");
		}

		return dataFieldFormats.stream()
			.filter(format -> StringUtils.isNotBlank(_formatDataField(format)))
			.map(format -> new BriefTabFieldDTO(format.getDatafieldTag(), _formatDataField(format)))
			.collect(Collectors.toList());
	}

	@Override
	public List<RecordAttachmentDTO> getAttachments() {
		return getDataFields(MarcConstants.ELECTRONIC_LOCATION).stream()
			.map(datafield -> {
				String file = getFirstSubfieldData(datafield, 'f');
				String uri = getFirstSubfieldData(datafield, 'u');
				String name = getFirstSubfieldData(datafield, 'y');

				if (StringUtils.isBlank(file)) {
					file = name;
				}

				if (StringUtils.isBlank(file)) {
					file = uri;
				}

				if (StringUtils.isBlank(file)) {
					return null;
				}

				if (StringUtils.isBlank(name)) {
					name = file;
				}

				String path = getFirstSubfieldData(datafield, 'd');

				RecordAttachmentDTO attachment = new RecordAttachmentDTO();

				attachment.setPath(path);
				attachment.setFile(file);
				attachment.setName(name);
				attachment.setUri(uri);

				return attachment;
			})
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}
	
	@Override
	public String getIsbn() {
		return _getAllFieldValues(StringPool.SPACE, ISBN_BTFF);
	}

	@Override
	public String getIssn() {
		return _getAllFieldValues(StringPool.SPACE, ISSN_BTFF);
	}

	@Override
	public String getIsrc() {
		return _getAllFieldValues(StringPool.SPACE, ISRC_BTFF);
	}

	@Override
	public String getPublicationYear() {
		return _getFirstFieldValue(PUBLICATION_BTFF, PUBLICATION_FUNCTIONS_BTFF);
	}

	@Override
	public String getShelfLocation() {
		return _getFirstFieldValue(SHELF_LOCATION_BTFF);
	}

	@Override
	public String getHoldingLocation() {
		return _getFirstFieldValue(HOLDING_LOCATION_BTFF);
	}
	
	@Override
	public String getLocation() {
		return _getFirstFieldValue(LOCATION_BTFF);
	}
	
	@Override
	public String getLocationB() {
		return _getFirstFieldValue(LOCATION_B_BTFF);
	}
	
	@Override
	public String getLocationC() {
		return _getFirstFieldValue(LOCATION_C_BTFF);
	}
	
	@Override
	public String getLocationD() {
		return _getFirstFieldValue(HOLDING_LOCATION_BTFF);
	}
	
	@Override
	public String getDDCN() {
		return _getFirstFieldValue(DDCN_BTFF);
	}
	
	@Override
	public String getSourceAcquisitionDate() {
		return _getFirstFieldValue(SOURCE_ACQUISITION_NOTES_BTFF);
	}

	@Override
	public String getEditor() {
		return _getFirstFieldValue(EDITOR_BTFF);
	}
	
	@Override
	public String getEdition() {
		return _getFirstFieldValue(EDITION_BTFF);
	}

    @Override
	public String getAccessionNumber() {
		return _getFirstFieldValue(ACCESSION_NUMBER_BTFF);
    }
	
	@Override
	public List<DataField> getDataFields(String tag) {
		if (this.record == null || StringUtils.isBlank(tag)) {
			return Collections.emptyList();
		}

		return cache.getOrDefault(tag, Collections.emptyList());
	}

	@Override
	public String getAuthor() {
		return _getFieldValue(
				AUTHOR_PERSONAL_NAME_BTFF,
				AUTHOR_CORPORATION_NAME_BTFF,
				AUTHOR_CONGRESS_NAME_BTFF,
				AUTHOR_OTHER_PERSONAL_NAMES_BTFF,
				AUTHOR_OTHER_CORPORATION_NAMES_BTFF,
				AUTHOR_OTHER_CONGRESS_NAMES_BTFF,
				SECONDARY_AUTHOR_PERSONAL_NAME_BTFF,
				SECONDARY_AUTHOR_CORPORATION_NAME_BTFF,
				SECONDARY_AUTHOR_CONGRESS_NAME_BTFF
		);
	}

	@Override
	public String getAuthors() {
		return _getAllFieldValues(
				AUTHOR_PERSONAL_NAME_BTFF,
				AUTHOR_CORPORATION_NAME_BTFF,
				AUTHOR_CONGRESS_NAME_BTFF,
				AUTHOR_OTHER_PERSONAL_NAMES_BTFF,
				AUTHOR_OTHER_CORPORATION_NAMES_BTFF,
				AUTHOR_OTHER_CONGRESS_NAMES_BTFF,
				SECONDARY_AUTHOR_PERSONAL_NAME_BTFF,
				SECONDARY_AUTHOR_CORPORATION_NAME_BTFF,
				SECONDARY_AUTHOR_CONGRESS_NAME_BTFF
		);
	}

	@Override
	public String getAuthorName() {
		return _getFieldValue(
				AUTHOR_PERSONAL_NAME_BTFF,
				AUTHOR_CORPORATION_NAME_BTFF,
				AUTHOR_CONGRESS_NAME_BTFF
		);
	}

	@Override
	public String getAuthorNames() {
		return _getAllFieldValues(
				AUTHOR_PERSONAL_NAME_BTFF,
				AUTHOR_CORPORATION_NAME_BTFF,
				AUTHOR_CONGRESS_NAME_BTFF
		);
	}

	@Override
	public String getAuthorOtherNames() {
		return _getAllFieldValues(
				AUTHOR_OTHER_PERSONAL_NAMES_BTFF,
				AUTHOR_OTHER_CORPORATION_NAMES_BTFF,
				AUTHOR_OTHER_CONGRESS_NAMES_BTFF
		);
	}

	@Override
	public String getTitle() {
		return _getFieldValue(
				TITLE_BTFF,
				COLLECTIVE_UNIFORM_TITLE_BTFF,
				UNIFORM_TITLE_BTFF,
				ADDED_UNIFORM_TITLE_BTFF,
				ADDED_ANALYTICAL_TITLE_BTFF,
				SECONDARY_INPUT_SERIAL_TITLE_BTFF
		);
	}

	@Override
	public String getSubjects() {
		return _getAllFieldValues(
				SUBJECT_ADDED_ENTRY_PERSONAL_NAME_BTFF,
				SUBJECT_ADDED_ENTRY_CORPORATE_NAME_BTFF,
				SUBJECT_ADDED_ENTRY_MEETING_NAME_BTFF,
				SUBJECT_ADDED_ENTRY_UNIFORM_TITLE_BTFF,
				SUBJECT_ADDED_ENTRY_TOPICAL_TERM_BTFF,
				SUBJECT_ADDED_ENTRY_GEOGRAPHIC_NAME_BTFF,
				SUBJECT_ADDED_ENTRY_LOCAL_BTFF
		);
	}

	@Override
	public String getFirstSubfieldData(String tag, char subfield) {
		return _getFirstSubfield(tag, subfield)
				.map(Subfield::getData)
				.orElse(StringPool.BLANK);
	}
	
	@Override
	public String getFirstSubfieldData(DataField datafield, char subfieldCode) {
		// Get a single subfield value
		if (datafield == null) {
			return StringPool.BLANK;
		}

		Subfield subfield = datafield.getSubfield(subfieldCode);

		if (subfield == null) {
			return StringPool.BLANK;
		}

		return subfield.getData();
	}

	private Stream<DataField> _getDataFields(String datafieldTag) {
		if (StringUtils.isBlank(datafieldTag)) {
			return Stream.empty();
		}

		return cache.getOrDefault(datafieldTag, Collections.emptyList()).stream();
	}

	private Optional<Subfield> _getFirstSubfield(String tag, char subfield) {
		Stream<DataField> stream = getDataFields(tag).stream();
		return stream.map(f -> f.getSubfield(subfield)).filter(Objects::nonNull).findFirst();
	}

	private static Map<String, List<DataField>> _readDataFieldMap(Record record) {
		if (record == null) {
			return Collections.emptyMap();
		}

		return record.getDataFields().stream()
			.collect(Collectors.groupingBy(DataField::getTag));
	}

	private static String _formatDataField(String format, DataField datafield) {
		return _formatDataField(format, Collections.singleton(datafield));
	}

	private static String _formatDataField(String format, Collection<DataField> datafields) {
		if (datafields == null) {
			return StringPool.BLANK;
		}
		
		Matcher matcher = MarcConstants.DATAFIELD_FORMAT_PATTERN.matcher(format);
		
		StringBuilder result = new StringBuilder();

		for (DataField dataField : datafields) {
			if (dataField == null) {
				continue;
			}

			String lastSeparator = null;
			String lastValue = null;
			boolean newSeparator = false;
			boolean endsWithSeparator = false;
			boolean shouldAddStartParenthesis = false;

			while (matcher.find()) {
				String specialGroup = matcher.group(1);
		
				String element;
				String content;
				if (specialGroup == null) {
					element = matcher.group(2);
					content = matcher.group(3);
				} else {
					element = specialGroup;
					content = "";
				}
		
				if (element.equals("$")) {
					List<Subfield> subfields = dataField.getSubfields(content.charAt(0));
		
					String subfieldSeparator = (content.length() == 1) ? ", " : content.substring(1);
					endsWithSeparator = false;
		
					StringBuilder values = new StringBuilder();

					for (Subfield subfield : subfields) {
						String subfieldData = subfield.getData();
		
						if (StringUtils.isNotBlank(subfieldData)) {
							subfieldData = subfieldData.trim();
		
							values.append(subfieldData);
		
							if (TextUtils.endsInValidCharacter(subfieldData)) {
								values.append(subfieldSeparator);
								endsWithSeparator = true;
							} else {
								values.append(" ");
								endsWithSeparator = false;
							}
						}
					}

					String value = values.toString();
		
					if (endsWithSeparator) {
						value = value.substring(0, value.length() - subfieldSeparator.length());
					} else {
						value = value.trim();
					}
		
					if (StringUtils.isNotBlank(value)) {
						if (newSeparator) {
							newSeparator = false;
							
							if (StringUtils.isNotBlank(lastValue)) {
								if (TextUtils.endsInValidCharacter(lastValue)) {
									result.append(lastSeparator);
								} else {
									result.append(" ");
								}
							}
						}
		
						lastValue = value.trim();
						
						if (shouldAddStartParenthesis) {
							shouldAddStartParenthesis = false;
							result.append("(");
						}
		
						result.append(lastValue);
					}
					
				} else if (element.equals("_")) {
					lastSeparator = content;
					newSeparator = true;
					
				} else if (element.equals("(")) {
					shouldAddStartParenthesis = true;
					
				} else if (element.equals(")")) {
					if (result.toString().endsWith("(")) {
						result.deleteCharAt(result.length() - 1);
					} else if (!shouldAddStartParenthesis) {
						result.append(")");
						shouldAddStartParenthesis = false;
					}
				}
			}
		
			matcher.reset();
			result.append("\n");
		}
		
		return StringUtils.chomp(result.toString());
	}

	private String _formatDataField(BriefTabFieldFormatDTO format) {
		List<DataField> dataFieldList = getDataFields(format.getDatafieldTag());

		return _formatDataField(format.getFormat(), dataFieldList);
	}

	private String _getFieldValue(BriefTabFieldFormatDTO ... dataFieldFormats) {
		return _getAllFieldValues(StringPool.NEW_LINE, dataFieldFormats);
	}

	private String _getFirstFieldValue(BriefTabFieldFormatDTO... formats) {
		return Stream.of(formats)
			.flatMap(this::_getAllFormattedDataFields)
			.filter(StringUtils::isNotBlank)
			.findFirst()
			.orElseGet(() -> StringPool.BLANK);
	}

	private String _getAllFieldValues(BriefTabFieldFormatDTO ... formats) {
		return _getAllFieldValues(StringPool.NEW_LINE, formats);
	}

	private String _getAllFieldValues(String separator, BriefTabFieldFormatDTO ... formats) {
		return Stream.of(formats)
			.flatMap(this::_getAllFormattedDataFields)
			.filter(StringUtils::isNotBlank)
			.collect(Collectors.joining(separator));
	}

	private Stream<String> _getAllFormattedDataFields(BriefTabFieldFormatDTO format) {
		return _getDataFields(format.getDatafieldTag())
			.map(datafield ->
				_formatDataField(format.getFormat(), datafield)
			);
	}
}
