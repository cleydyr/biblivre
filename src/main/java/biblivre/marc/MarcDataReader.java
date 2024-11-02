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

import biblivre.cataloging.BriefTabFieldDTO;
import biblivre.cataloging.BriefTabFieldFormatDTO;
import biblivre.cataloging.RecordAttachmentDTO;
import biblivre.core.utils.CharPool;
import biblivre.core.utils.StringPool;
import java.util.*;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

@Getter
public final class MarcDataReader {
    private final Record record;
    private final Map<String, List<DataField>> cache;

    public MarcDataReader(Record record) {
        this.record = record;
        this.cache = this.readDataFieldMap();
    }

    public List<BriefTabFieldDTO> getFieldList(List<BriefTabFieldFormatDTO> dataFieldFormats) {
        List<BriefTabFieldDTO> list = new ArrayList<>();

        for (BriefTabFieldFormatDTO briefTabFieldFormatDTO : dataFieldFormats) {
            String tag = briefTabFieldFormatDTO.getDatafieldTag();
            List<DataField> fields = this.getDataFields(tag);

            String formattedField =
                    this.formatDataField(briefTabFieldFormatDTO.getFormat(), fields);

            if (StringUtils.isNotBlank(formattedField)) {
                list.add(
                        new BriefTabFieldDTO(
                                briefTabFieldFormatDTO.getDatafieldTag(), formattedField));
            }
        }

        return list;
    }

    public String getFieldValue(boolean listAll, BriefTabFieldFormatDTO... dataFieldFormats) {
        return this.getFieldValue(listAll, "\n", dataFieldFormats);
    }

    public String getFieldValue(String separator, BriefTabFieldFormatDTO... dataFieldFormats) {
        return this.getFieldValue(true, separator, dataFieldFormats);
    }

    public String getFieldValue(
            boolean listAll, String separator, BriefTabFieldFormatDTO... dataFieldFormats) {
        List<String> formattedFields = new ArrayList<>();

        for (BriefTabFieldFormatDTO dto : dataFieldFormats) {
            String tag = dto.getDatafieldTag();
            List<DataField> fields = this.getDataFields(tag);

            for (DataField datafield : fields) {
                String formattedField = this.formatDataField(dto.getFormat(), datafield);

                if (StringUtils.isNotBlank(formattedField)) {
                    if (!listAll) {
                        return formattedField;
                    }

                    formattedFields.add(formattedField);
                }
            }
        }

        return !formattedFields.isEmpty() ? StringUtils.join(formattedFields, separator) : null;
    }

    public List<RecordAttachmentDTO> getAttachments() {
        List<DataField> fields = this.getDataFields(MarcConstants.ELECTRONIC_LOCATION);

        return fields.stream()
                .map(
                        field -> {
                            RecordAttachmentDTO recordAttachmentDTO =
                                    new RecordAttachmentDTO(
                                            getFirstSubfieldData(field, 'f'),
                                            getFirstSubfieldData(field, 'y'),
                                            getFirstSubfieldData(field, 'd'),
                                            getFirstSubfieldData(field, 'u'));

                            recordAttachmentDTO.normalizeFileAndName();

                            return recordAttachmentDTO;
                        })
                .toList();
    }

    public String getAuthor(boolean listAll) {
        return this.getFieldValue(
                listAll,
                new BriefTabFieldFormatDTO(MarcConstants.AUTHOR_PERSONAL_NAME, "${a}"),
                new BriefTabFieldFormatDTO(MarcConstants.AUTHOR_CORPORATION_NAME, "${a}"),
                new BriefTabFieldFormatDTO(MarcConstants.AUTHOR_CONGRESS_NAME, "${a}"),
                new BriefTabFieldFormatDTO(MarcConstants.AUTHOR_OTHER_PERSONAL_NAMES, "${a}"),
                new BriefTabFieldFormatDTO(MarcConstants.AUTHOR_OTHER_CORPORATION_NAMES, "${a}"),
                new BriefTabFieldFormatDTO(MarcConstants.AUTHOR_OTHER_CONGRESS_NAMES, "${a}"),
                new BriefTabFieldFormatDTO(MarcConstants.SECONDARY_AUTHOR_PERSONAL_NAME, "${a}"),
                new BriefTabFieldFormatDTO(MarcConstants.SECONDARY_AUTHOR_CORPORATION_NAME, "${a}"),
                new BriefTabFieldFormatDTO(MarcConstants.SECONDARY_AUTHOR_CONGRESS_NAME, "${a}"));
    }

    public String getAuthorName(boolean listAll) {
        return this.getFieldValue(
                listAll,
                new BriefTabFieldFormatDTO(MarcConstants.AUTHOR_PERSONAL_NAME, "${a}"),
                new BriefTabFieldFormatDTO(MarcConstants.AUTHOR_CORPORATION_NAME, "${a}"),
                new BriefTabFieldFormatDTO(MarcConstants.AUTHOR_CONGRESS_NAME, "${a}"));
    }

    public String getAuthorOtherName(boolean listAll) {
        return this.getFieldValue(
                listAll,
                new BriefTabFieldFormatDTO(MarcConstants.AUTHOR_OTHER_PERSONAL_NAMES, "${a}"),
                new BriefTabFieldFormatDTO(MarcConstants.AUTHOR_OTHER_CORPORATION_NAMES, "${a}"),
                new BriefTabFieldFormatDTO(MarcConstants.AUTHOR_OTHER_CONGRESS_NAMES, "${a}"));
    }

    public String getTitle(boolean listAll) {
        return this.getFieldValue(
                listAll,
                new BriefTabFieldFormatDTO(MarcConstants.TITLE, "${a}_{: }${b}"),
                new BriefTabFieldFormatDTO(MarcConstants.COLLECTIVE_UNIFORM_TITLE, "${a}_{ }${f}"),
                new BriefTabFieldFormatDTO(MarcConstants.UNIFORM_TITLE, "${a}"),
                new BriefTabFieldFormatDTO(MarcConstants.ADDED_UNIFORM_TITLE, "${a}"),
                new BriefTabFieldFormatDTO(
                        MarcConstants.ADDED_ANALYTICAL_TITLE, "${a}_{ }${n}_{ }${p}"),
                new BriefTabFieldFormatDTO(
                        MarcConstants.SECONDARY_INPUT_SERIAL_TITLE, "${a}_{ }${v}"));
    }

    public String getIsbn() {
        return this.getFieldValue(" ", new BriefTabFieldFormatDTO(MarcConstants.ISBN, "${a}"));
    }

    public String getIssn() {
        return this.getFieldValue(" ", new BriefTabFieldFormatDTO(MarcConstants.ISSN, "${a}"));
    }

    public String getIsrc() {
        return this.getFieldValue(" ", new BriefTabFieldFormatDTO(MarcConstants.ISRC, "${a}"));
    }

    public String getPublicationYear() {
        return this.getFieldValue(
                false,
                new BriefTabFieldFormatDTO(MarcConstants.PUBLICATION, "${c}"),
                new BriefTabFieldFormatDTO(MarcConstants.PUBLICATION_FUNCTIONS, "${c}"));
    }

    public String getShelfLocation() {
        return this.getFieldValue(
                false,
                new BriefTabFieldFormatDTO(MarcConstants.SHELF_LOCATION, "${a}_{ }${b}_{ }${c}"));
    }

    public String getHoldingLocation() {
        return this.getFieldValue(
                false, new BriefTabFieldFormatDTO(MarcConstants.SHELF_LOCATION, "${d}"));
    }

    public String getLocation() {
        return this.getFieldValue(
                false, new BriefTabFieldFormatDTO(MarcConstants.SHELF_LOCATION, "${a}"));
    }

    public String getLocationB() {
        return this.getFieldValue(
                false, new BriefTabFieldFormatDTO(MarcConstants.SHELF_LOCATION, "${b}"));
    }

    public String getLocationC() {
        return this.getFieldValue(
                false, new BriefTabFieldFormatDTO(MarcConstants.SHELF_LOCATION, "${c}"));
    }

    public String getLocationD() {
        return this.getFieldValue(
                false, new BriefTabFieldFormatDTO(MarcConstants.SHELF_LOCATION, "${d}"));
    }

    public String getDDCN() {
        return this.getFieldValue(false, new BriefTabFieldFormatDTO(MarcConstants.DDCN, "${a}"));
    }

    public String getSourceAcquisitionDate() {
        return this.getFieldValue(
                false, new BriefTabFieldFormatDTO(MarcConstants.SOURCE_ACQUISITION_NOTES, "${d}"));
    }

    public String getSubject(boolean listAll) {
        return this.getFieldValue(
                listAll,
                new BriefTabFieldFormatDTO(
                        MarcConstants.SUBJECT_ADDED_ENTRY_PERSONAL_NAME,
                        "${a}_{ - }${x}_{ - }${y}_{ - }${z}"),
                new BriefTabFieldFormatDTO(
                        MarcConstants.SUBJECT_ADDED_ENTRY_CORPORATE_NAME,
                        "${a}_{ - }${x}_{ - }${y}_{ - }${z}"),
                new BriefTabFieldFormatDTO(
                        MarcConstants.SUBJECT_ADDED_ENTRY_MEETING_NAME,
                        "${a}_{ - }${x}_{ - }${y}_{ - }${z}"),
                new BriefTabFieldFormatDTO(
                        MarcConstants.SUBJECT_ADDED_ENTRY_UNIFORM_TITLE,
                        "${a}_{ - }${x}_{ - }${y}_{ - }${z}"),
                new BriefTabFieldFormatDTO(
                        MarcConstants.SUBJECT_ADDED_ENTRY_TOPICAL_TERM,
                        "${a}_{ - }${x}_{ - }${y}_{ - }${z}"),
                new BriefTabFieldFormatDTO(
                        MarcConstants.SUBJECT_ADDED_ENTRY_GEOGRAPHIC_NAME,
                        "${a}_{ - }${x}_{ - }${y}_{ - }${z}"),
                new BriefTabFieldFormatDTO(
                        MarcConstants.SUBJECT_ADDED_ENTRY_LOCAL,
                        "${a}_{ - }${x}_{ - }${y}_{ - }${z}"));
    }

    public String getEditor() {
        return this.getFieldValue(
                false, new BriefTabFieldFormatDTO(MarcConstants.PUBLICATION, "${b}"));
    }

    public String getEdition() {
        return this.getFieldValue(false, new BriefTabFieldFormatDTO(MarcConstants.EDITION, "${a}"));
    }

    public String getAccessionNumber() {
        return this.getFieldValue(
                false, new BriefTabFieldFormatDTO(MarcConstants.ACCESSION_NUMBER, "${a}"));
    }

    public List<DataField> getDataFields(String tag) {
        Record record = this.getRecord();

        if (record == null || StringUtils.isBlank(tag)) {
            return new ArrayList<>();
        }

        List<DataField> list = this.getCache().get(tag);

        if (list != null) {
            return list;
        }

        return new ArrayList<>();
    }

    public Subfield getFirstSubfield(String tag, char subfield) {
        List<DataField> dataFields = this.getDataFields(tag);

        for (DataField field : dataFields) {
            Subfield sf = field.getSubfield(subfield);

            if (sf != null) {
                return sf;
            }
        }

        return null;
    }

    public String getFirstSubfieldData(String tag, char subfield) {
        Subfield sf = this.getFirstSubfield(tag, subfield);

        return sf != null ? sf.getData() : "";
    }

    public String getFirstSubfieldData(DataField datafield, char subfield) {
        // Get a single subfield value
        if (datafield == null) {
            return "";
        }

        Subfield sf = datafield.getSubfield(subfield);

        return sf != null ? sf.getData() : "";
    }

    private Map<String, List<DataField>> readDataFieldMap() {
        Map<String, List<DataField>> hash = new HashMap<>();
        Record record = this.getRecord();

        if (record == null) {
            return hash;
        }

        for (DataField field : record.getDataFields()) {
            String tag = field.getTag();

            List<DataField> fieldList = hash.computeIfAbsent(tag, ignored -> new ArrayList<>());

            fieldList.add(field);
        }

        return hash;
    }

    private String formatDataField(String format, DataField... datafields) {
        List<DataField> dataFieldList = Arrays.asList(datafields);

        return this.formatDataField(format, dataFieldList);
    }

    private String formatDataField(String format, List<DataField> dataFieldList) {
        if (dataFieldList == null || dataFieldList.isEmpty()) {
            return StringPool.BLANK;
        }

        List<String> formatItems = new ArrayList<>();

        Deque<List<String>> stack = new ArrayDeque<>();

        State state = State.F;

        for (int i = 0; i < format.length(); i++) {
            char c = format.charAt(i);

            switch (state) {
                case F:
                    if (c == CharPool.DOLLAR_SIGN) {
                        state = State.V0;
                    } else if (c == CharPool.UNDERSCORE) {
                        state = State.S0;
                    } else if (c == CharPool.OPEN_PARENTHESIS) {
                        stack.push(formatItems);

                        formatItems = new ArrayList<>();
                    } else if (c == CharPool.CLOSE_PARENTHESIS) {
                        if (stack.isEmpty()) {
                            throwException(format);
                        }

                        List<String> popped = stack.pop();

                        String aggregatedFormat = StringUtils.join(formatItems, StringPool.BLANK);

                        if (StringUtils.isNotBlank(aggregatedFormat)) {
                            popped.add(String.valueOf(CharPool.OPEN_PARENTHESIS));
                            popped.add(aggregatedFormat);
                            popped.add(String.valueOf(CharPool.CLOSE_PARENTHESIS));
                        }

                        formatItems = popped;
                    } else {
                        throwException(format);
                    }

                    break;
                case S0:
                    if (c == CharPool.OPEN_BRACE) {
                        state = State.S1;
                    } else {
                        throwException(format);
                    }
                    break;
                case S1:
                    int j = i;

                    while (j < format.length() && format.charAt(j) != CharPool.CLOSE_BRACE) {
                        j++;
                    }

                    if (j == format.length()) {
                        throwException(format);
                    }

                    String plainText = format.substring(i, j);

                    formatItems.add(plainText);

                    i = j;

                    state = State.F;
                    break;
                case V0:
                    if (c == CharPool.OPEN_BRACE) {
                        state = State.V1;
                    } else {
                        throwException(format);
                    }
                    break;
                case V1:
                    String result = processSubfield(c, dataFieldList);

                    if (result != null) {
                        formatItems.add(result);
                    } else if (!formatItems.isEmpty()) {
                        formatItems.removeLast();
                    }

                    state = State.V2;
                    break;
                case V2:
                    if (c == CharPool.CLOSE_BRACE) {
                        state = State.F;
                    } else {
                        throwException(format);
                    }
                    break;
                default:
                    break;
            }
        }

        return StringUtils.join(formatItems, StringPool.BLANK);
    }

    private String processSubfield(char c, List<DataField> dataFieldList) {
        return dataFieldList.stream()
                .map(dataField -> dataField.getSubfields(c))
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .map(Subfield::getData)
                .filter(StringUtils::isNotBlank)
                .findFirst()
                .orElse(null);
    }

    private static void throwException(String format) {
        throw new IllegalArgumentException("Invalid format string: " + format);
    }

    private enum State {
        F,
        S0,
        S1,
        V0,
        V1,
        V2
    }
}
