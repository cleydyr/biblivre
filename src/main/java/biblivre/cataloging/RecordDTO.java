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
package biblivre.cataloging;

import biblivre.cataloging.enums.RecordDatabase;
import biblivre.cataloging.enums.RecordType;
import biblivre.core.AbstractDTO;
import biblivre.core.utils.Constants;
import biblivre.marc.MarcConstants;
import biblivre.marc.MarcDataReader;
import biblivre.marc.MarcUtils;
import biblivre.marc.MaterialType;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.marc4j.MarcStreamWriter;
import org.marc4j.MarcWriter;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;

public class RecordDTO extends AbstractDTO {
    private static final String _CF008_FORMAT = "yyMMdd";

    private static final String DATE_OF_LAST_TRANSACTION_TAG = "005";

    private static final int INVALID_ID = -1;

    private static final String _COMPACT_ISO_FORMAT = "yyyyMMddHHmmss.SSS";

    private static final String CF001_FORMAT = "0000000";

    @Getter private Record record;
    private int id;
    private byte[] iso2709;
    @Setter private MaterialType materialType;
    @Setter private RecordDatabase recordDatabase;
    private Boolean isNew;

    private transient List<RecordAttachmentDTO> attachments;
    @Setter private transient List<BriefTabFieldDTO> fields;
    private transient JSONObject json;
    private transient String humanReadableMarc;

    private List<BriefTabFieldFormatDTO> briefTabFieldFormats;

    public RecordDTO() {
        this.record = MarcFactory.newInstance().newRecord();

        this.id = INVALID_ID;
    }

    public MaterialType getMaterialType() {
        if (this.materialType == null) {
            return MaterialType.fromRecord(this.record);
        }

        return this.materialType;
    }

    public RecordDatabase getRecordDatabase() {
        return this.recordDatabase == null ? RecordDatabase.MAIN : this.recordDatabase;
    }

    public int getId() {
        if (this.id == INVALID_ID) {
            this.id = Integer.parseInt(this.record.getControlNumber());
        }

        return this.id;
    }

    public void setId(int id) {
        nullifyDerivedFields();

        this.id = id;

        MarcFactory factory = MarcFactory.newInstance();

        ControlField field = factory.newControlField("001");

        Format formatter = new DecimalFormat(CF001_FORMAT);

        field.setData(formatter.format(id));

        record.addVariableField(field);

        nullifyDerivedFields();
    }

    public String getUTF8Iso2709() {
        return new String(getIso2709(), Constants.DEFAULT_CHARSET);
    }

    public byte[] getIso2709() {
        if (this.iso2709 == null) {
            _initIso2709();
        }

        return this.iso2709;
    }

    private void _initIso2709() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        MarcWriter writer = new MarcStreamWriter(os, Constants.DEFAULT_CHARSET_NAME);

        writer.write(record);

        writer.close();

        this.iso2709 = os.toByteArray();
    }

    public void setIso2709(byte[] iso2709) {
        nullifyDerivedFields();

        this.record = MarcUtils.iso2709ToRecord(iso2709);

        this.iso2709 = iso2709;
    }

    protected void nullifyDerivedFields() {
        this.isNew = null;
        this.id = INVALID_ID;
        this.iso2709 = null;
        this.json = null;
        this.attachments = null;
        this.fields = null;
        this.humanReadableMarc = null;
    }

    public List<RecordAttachmentDTO> getAttachments() {
        if (this.attachments == null) {
            MarcDataReader marcDataReader = new MarcDataReader(this.record);

            this.attachments = marcDataReader.getAttachments();
        }

        return this.attachments;
    }

    public void addAttachment(String uri, String name) {
        nullifyDerivedFields();

        MarcFactory factory = MarcFactory.newInstance();

        DataField field = factory.newDataField(MarcConstants.ELECTRONIC_LOCATION, ' ', ' ');

        Subfield pathSubfield = factory.newSubfield('d', uri.replaceAll("[^\\/]*$", ""));

        field.addSubfield(pathSubfield);

        Subfield eletronicNameSubfield = factory.newSubfield('f', uri.replaceAll(".*\\/", ""));

        field.addSubfield(eletronicNameSubfield);

        Subfield uriSubfield = factory.newSubfield('u', uri);

        field.addSubfield(uriSubfield);

        Subfield linkTextSubfield = factory.newSubfield('y', name);

        field.addSubfield(linkTextSubfield);

        this.record.addVariableField(field);
    }

    public void removeAttachment(String uri, String name) {
        int index = _getAttachmentIndex(uri, name);

        if (index == this.attachments.size()) {
            return;
        }

        this.attachments.remove(index);

        _removeEletronicResourceField(uri, name);

        nullifyDerivedFields();
    }

    private void _removeEletronicResourceField(String uri, String name) {
        for (VariableField variablefield :
                this.record.getVariableFields(MarcConstants.ELECTRONIC_LOCATION)) {
            DataField datafield = (DataField) variablefield;

            for (Subfield subfield : datafield.getSubfields('y')) {
                String itLinkText = subfield.getData();

                if (StringUtils.isBlank(itLinkText)) {
                    itLinkText = uri;
                }

                boolean found = false;

                for (Subfield subfield2 : datafield.getSubfields('u')) {
                    String itURI = subfield2.getData();

                    if (itURI.equals(uri) && itLinkText.equals(name)) {
                        record.removeVariableField(datafield);

                        found = true;

                        break;
                    }
                }

                if (found) {
                    break;
                }
            }
        }
    }

    private int _getAttachmentIndex(String uri, String name) {
        int index = 0;

        for (RecordAttachmentDTO attachment : getAttachments()) {
            if (attachment.getUri().equals(uri) && attachment.getName().equals(name)) {
                break;
            }

            index++;
        }

        return index;
    }

    protected List<BriefTabFieldDTO> getFields() {
        if (this.fields == null) {
            MarcDataReader marcDataReader = new MarcDataReader(record);

            this.fields = marcDataReader.getFieldList(briefTabFieldFormats);
        }

        return this.fields;
    }

    public RecordType getRecordType() {
        return getMaterialType().getRecordType();
    }

    public JSONObject getJson() {
        if (this.json == null) {
            _initJSON();
        }

        return this.json;
    }

    private void _initJSON() {
        json = new JSONObject();

        json.putOpt("000", record.getLeader().marshal());

        for (ControlField controlfield : record.getControlFields()) {
            json.putOpt(controlfield.getTag(), controlfield.getData());
        }

        for (DataField datafield : record.getDataFields()) {
            JSONObject datafieldJson = new JSONObject();

            datafieldJson.putOpt("ind1", datafield.getIndicator1());
            datafieldJson.putOpt("ind2", datafield.getIndicator2());

            for (Subfield subfield : datafield.getSubfields()) {
                datafieldJson.append(String.valueOf(subfield.getCode()), subfield.getData());
            }

            json.append(datafield.getTag(), datafieldJson);
        }
    }

    public String getHumanReadableMarc() {
        if (this.humanReadableMarc == null) {
            _initHumanReadableMarc();
        }

        return this.humanReadableMarc;
    }

    private void _initHumanReadableMarc() {
        StringBuilder sb = new StringBuilder();

        sb.append("000 ");
        sb.append(record.getLeader().marshal());
        sb.append('\n');

        for (ControlField field : record.getControlFields()) {
            sb.append(field.toString());
            sb.append('\n');
        }

        for (DataField field : record.getDataFields()) {
            sb.append(field.getTag());
            sb.append(' ');

            char ind1 = field.getIndicator1();
            char ind2 = field.getIndicator2();

            sb.append(ind1 == ' ' ? MarcConstants.NO_INDICATOR : ind1);
            sb.append(ind2 == ' ' ? MarcConstants.NO_INDICATOR : ind2);

            for (Subfield subfield : field.getSubfields()) {
                sb.append(MarcConstants.DEFAULT_SPLITTER);
                sb.append(subfield.getCode());
                sb.append(subfield.getData());
            }

            sb.append('\n');
        }

        this.humanReadableMarc = sb.toString();
    }

    public void setRecord(Record record) {
        nullifyDerivedFields();

        this.record = record;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();

        this.populateExtraData(json);

        json.putOpt("id", this.getId());
        json.putOpt("database", this.getRecordDatabase());

        json.putOpt("created", this.getCreated());
        json.putOpt("modified", this.getModified());

        json.putOpt("attachments", this.toJSONArray(this.getAttachments()));
        json.putOpt("json", this.getJson());
        json.putOpt("marc", this.getHumanReadableMarc());

        return json;
    }

    public boolean isNew() {
        if (isNew == null) {
            this.isNew = this.record.getLeader().getRecordStatus() == 'n';
        }

        return isNew;
    }

    public void setNew(boolean isNew) {
        nullifyDerivedFields();

        this.isNew = isNew;

        this.record.getLeader().setRecordStatus(getRecordStatusCode());
    }

    public char getRecordStatusCode() {
        return isNew() ? 'n' : 'c';
    }

    public void setDateOfLastTransaction() {
        MarcFactory factory = MarcFactory.newInstance();

        ControlField field = (ControlField) record.getVariableField(DATE_OF_LAST_TRANSACTION_TAG);

        if (field == null) {
            field = factory.newControlField(DATE_OF_LAST_TRANSACTION_TAG);

            record.addVariableField(field);
        }

        Format compactISO = new SimpleDateFormat(_COMPACT_ISO_FORMAT);

        field.setData(compactISO.format(new Date()));

        nullifyDerivedFields();
    }

    public void setFixedLengthDataElements() {
        Record record = getRecord();

        MarcFactory factory = MarcFactory.newInstance();

        ControlField field = (ControlField) record.getVariableField("008");

        if (field == null) {
            // Following the specs, this field should be constructed only
            // if it doesn't already exist.  Otherwise, keep what has
            // come with the freemarc string.
            field = factory.newControlField("008");

            // From 01 to 06

            Format cf008Format = new SimpleDateFormat(_CF008_FORMAT);

            // From 07 to 40
            String data = cf008Format.format(new Date()) + "s||||     bl|||||||||||||||||por|u";

            field.setData(data);

            record.addVariableField(field);
        }
        nullifyDerivedFields();
    }
}
