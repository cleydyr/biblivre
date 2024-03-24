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
package biblivre.cataloging.holding;

import biblivre.cataloging.BriefTabFieldDTO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.cataloging.enums.HoldingAvailability;
import biblivre.marc.MaterialType;
import java.io.Serial;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.json.JSONObject;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;

public class HoldingDTO extends RecordDTO implements Comparable<HoldingDTO> {
    private static final String CONTROL_NUMBER_FOR_RELATED_BIBLIOGRAPHIC_RECORD_TAG = "004";

    @Serial private static final long serialVersionUID = 1L;

    private Integer recordId;
    private String accessionNumber;
    private HoldingAvailability availability;

    private String locationD;
    private Boolean labelPrinted;

    private transient String shelfLocation;
    private transient BiblioRecordDTO biblioRecord;

    @Override
    public MaterialType getMaterialType() {
        return MaterialType.HOLDINGS;
    }

    public Integer getRecordId() {
        return this.recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;

        _setCF004(recordId);
    }

    public String getAccessionNumber() {
        return this.accessionNumber;
    }

    public void setAccessionNumber(String accessionNumber) {
        this.accessionNumber = accessionNumber;
    }

    public String getLocationD() {
        return this.locationD;
    }

    public void setLocationD(String locationD) {
        this.locationD = locationD;
    }

    public HoldingAvailability getAvailability() {
        return Objects.requireNonNullElse(this.availability, HoldingAvailability.AVAILABLE);
    }

    public void setAvailability(HoldingAvailability availability) {
        this.availability = availability;
    }

    public void setAvailability(String availability) {
        this.availability = HoldingAvailability.fromString(availability);
    }

    public BiblioRecordDTO getBiblioRecord() {
        return this.biblioRecord;
    }

    public void setBiblioRecord(BiblioRecordDTO biblioRecord) {
        this.biblioRecord = biblioRecord;
    }

    public Boolean getLabelPrinted() {
        return this.labelPrinted == null ? Boolean.FALSE : this.labelPrinted;
    }

    public void setLabelPrinted(Boolean labelPrinted) {
        this.labelPrinted = labelPrinted;
    }

    public String getShelfLocation() {
        return this.shelfLocation;
    }

    public void setShelfLocation(String shelfLocation) {
        this.shelfLocation = shelfLocation;
    }

    @Override
    public List<BriefTabFieldDTO> getFields() {
        return Collections.emptyList();
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject json = super.toJSONObject();

        json.putOpt("material_type", this.getMaterialType());
        json.putOpt("record_id", this.getRecordId());
        json.putOpt("accession_number", this.getAccessionNumber());
        json.putOpt("location_d", this.getLocationD());
        json.putOpt("shelf_location", this.getShelfLocation());
        json.putOpt("availability", this.getAvailability().toString());

        if (this.getBiblioRecord() != null) {
            json.put("biblio", this.getBiblioRecord().toJSONObject());
        }

        return json;
    }

    @Override
    public int compareTo(HoldingDTO o) {
        if (o == null) {
            return 0;
        }

        return Comparator.<String>naturalOrder()
                .compare(this.getAccessionNumber(), o.getAccessionNumber());
    }

    private void _setCF004(int biblioRecordId) {
        MarcFactory factory = MarcFactory.newInstance();

        Record record = getRecord();

        ControlField field =
                (ControlField)
                        record.getVariableField(
                                CONTROL_NUMBER_FOR_RELATED_BIBLIOGRAPHIC_RECORD_TAG);

        if (field == null) {
            field = factory.newControlField(CONTROL_NUMBER_FOR_RELATED_BIBLIOGRAPHIC_RECORD_TAG);

            record.addVariableField(field);
        }

        field.setData(String.valueOf(biblioRecordId));

        nullifyDerivedFields();
    }
}
