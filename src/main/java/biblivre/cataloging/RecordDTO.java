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
import biblivre.core.AbstractDTO;
import biblivre.core.utils.Constants;
import biblivre.marc.MarcUtils;
import biblivre.marc.MaterialType;
import java.io.ByteArrayOutputStream;
import java.util.List;
import org.json.JSONObject;
import org.marc4j.MarcStreamWriter;
import org.marc4j.MarcWriter;
import org.marc4j.marc.Record;

public class RecordDTO extends AbstractDTO {
    private static final long serialVersionUID = 1L;

    private Record record;

    private int id;
    private byte[] iso2709;
    private MaterialType materialType;
    private RecordDatabase recordDatabase;

    private transient List<RecordAttachmentDTO> attachments;
    private transient List<BriefTabFieldDTO> fields;
    private transient JSONObject json;
    private transient String humanReadableMarc;

    public MaterialType getMaterialType() {
        if (this.materialType == null) {
            return MaterialType.fromRecord(this.record);
        }

        return this.materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = MaterialType.fromString(materialType);
    }

    public void setMaterialType(MaterialType materialType) {
        this.materialType = materialType;
    }

    public RecordDatabase getRecordDatabase() {
        return this.recordDatabase == null ? RecordDatabase.MAIN : this.recordDatabase;
    }

    public void setRecordDatabase(String recordDatabase) {
        this.recordDatabase = RecordDatabase.fromString(recordDatabase);
    }

    public void setRecordDatabase(RecordDatabase recordDatabase) {
        this.recordDatabase = recordDatabase;
    }

    public int getId() {
        if (this.id == -1) {
            this.id = Integer.parseInt(this.record.getControlNumber());
        }

        return this.id;
    }

    public void setId(int id) {
        this.id = id;

        this.record.getControlNumberField().setData(String.valueOf(id));
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
        _nullifyDerivedFields();

        this.record = MarcUtils.iso2709ToRecord(iso2709);

        this.iso2709 = iso2709;
    }

    private void _nullifyDerivedFields() {
        this.id = -1;
        this.iso2709 = null;
        this.materialType = null;
        this.json = null;
        this.attachments = null;
        this.fields = null;
        this.humanReadableMarc = null;
    }

    public List<RecordAttachmentDTO> getAttachments() {
        return this.attachments;
    }

    public void setAttachments(List<RecordAttachmentDTO> attachments) {
        this.attachments = attachments;
    }

    public List<BriefTabFieldDTO> getFields() {
        return this.fields;
    }

    public void setFields(List<BriefTabFieldDTO> fields) {
        this.fields = fields;
    }

    public JSONObject getJson() {
        return this.json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }

    public String getHumanReadableMarc() {
        return this.humanReadableMarc;
    }

    public void setHumanReadableMarc(String marc) {
        this.humanReadableMarc = marc;
    }

    public Record getRecord() {
        return this.record;
    }

    public void setRecord(Record record) {
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
        json.putOpt("fields", this.toJSONArray(this.getFields()));
        json.putOpt("json", this.getJson());
        json.putOpt("marc", this.getHumanReadableMarc());

        return json;
    }
}
