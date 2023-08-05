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

import biblivre.cataloging.CatalogingHandler;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.bibliographic.BiblioRecordBO;
import biblivre.cataloging.enums.HoldingAvailability;
import biblivre.cataloging.enums.RecordDatabase;
import biblivre.core.DTOCollection;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.enums.ActionResult;
import biblivre.marc.MarcDataReader;
import biblivre.marc.MarcUtils;
import biblivre.marc.MaterialType;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Handler extends CatalogingHandler {
    private HoldingBO holdingBO;
    private BiblioRecordBO biblioRecordBO;

    @Autowired
    public Handler(HoldingBO recordBO, HoldingBO holdingBO, BiblioRecordBO biblioRecordBO) {
        super(recordBO, MaterialType.HOLDINGS);
        this.holdingBO = holdingBO;
        this.biblioRecordBO = biblioRecordBO;
    }

    @Override
    protected RecordDTO createRecordDTO(ExtendedRequest request) {
        return new HoldingDTO();
    }

    @Override
    public void open(ExtendedRequest request, ExtendedResponse response) {
        Integer id = request.getInteger("id", null);

        if (id == null) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.record_not_found");
            return;
        }

        RecordDTO dto = holdingBO.get(id);

        if (dto == null) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.record_not_found");
            return;
        }

        MarcDataReader marcDataReader = new MarcDataReader(dto.getRecord());

        String holdingLocation = marcDataReader.getShelfLocation();

        if (StringUtils.isBlank(holdingLocation)) {
            RecordDTO parent = biblioRecordBO.get(((HoldingDTO) dto).getRecordId());

            marcDataReader = new MarcDataReader(MarcUtils.iso2709ToRecord(parent.getIso2709()));
            holdingLocation = marcDataReader.getShelfLocation();
        }

        ((HoldingDTO) dto).setShelfLocation(holdingLocation);

        try {
            put("data", dto.toJSONObject());
        } catch (Exception e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        }
    }

    public void list(ExtendedRequest request, ExtendedResponse response) {
        int recordId = request.getInteger("record_id");

        DTOCollection<HoldingDTO> list = holdingBO.list(recordId);

        try {
            put("list", list.toJSONObject());
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        }
    }

    @Override
    public void delete(ExtendedRequest request, ExtendedResponse response) {
        Integer id = request.getInteger("id", null);

        if (id == null) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.record_not_found");
            return;
        }

        RecordDTO dto = holdingBO.get(id);

        if (dto == null) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.record_not_found");
            return;
        }

        boolean success = holdingBO.delete(dto);

        if (success) {
            this.setMessage(ActionResult.SUCCESS, "cataloging.record.success.delete");
        } else {
            this.setMessage(ActionResult.WARNING, "cataloging.record.error.delete");
        }
    }

    public void createAutomaticHolding(ExtendedRequest request, ExtendedResponse response) {
        Integer recordId = request.getInteger("record_id", null);

        if (recordId == null) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.record_not_found");
            return;
        }

        RecordDTO rdto = biblioRecordBO.get(recordId);

        if (rdto == null) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.record_not_found");
            return;
        }

        AutomaticHoldingDTO autoDto = this.createAutomaticHoldingDto(request);
        autoDto.setBiblioRecordDto(rdto);

        if (autoDto.getHoldingCount() <= 0) {
            this.setMessage(ActionResult.WARNING, "cataloging.record.error.save");
            return;
        }

        if (holdingBO.createAutomaticHolding(autoDto)) {
            this.list(request, response);
        } else {
            this.setMessage(ActionResult.WARNING, "cataloging.record.error.save");
        }
    }

    protected void doHydrateRecord(RecordDTO dto, ExtendedRequest request) {
        super.doHydrateRecord(dto, request);

        HoldingAvailability availability =
                request.getEnum(HoldingAvailability.class, "availability");

        HoldingDTO hdto = (HoldingDTO) dto;

        hdto.setAvailability(availability);

        hdto.setRecordId(request.getInteger("record_id"));
    }

    private AutomaticHoldingDTO createAutomaticHoldingDto(ExtendedRequest request) {
        AutomaticHoldingDTO dto = new AutomaticHoldingDTO();

        dto.setHoldingCount(request.getInteger("holding_count", 1));
        dto.setIssueNumber(request.getInteger("holding_volume_number", 0));
        dto.setNumberOfIssues(request.getInteger("holding_volume_count", 1));
        dto.setLibraryName(request.getString("holding_library"));
        dto.setAcquisitionType(request.getString("holding_acquisition_type"));
        dto.setAcquisitionDate(request.getString("holding_acquisition_date"));
        dto.setDatabase(request.getEnum(RecordDatabase.class, "database"));
        dto.setCreatedBy(request.getLoggedUserId());

        return dto;
    }

    @Autowired
    public void setHoldingBO(HoldingBO holdingBO) {
        super.setRecordBO(holdingBO);
        this.holdingBO = holdingBO;
    }

    @Autowired
    public void setBiblioRecordBO(BiblioRecordBO biblioRecordBO) {
        this.biblioRecordBO = biblioRecordBO;
    }

    @Override
    protected void populateFields(RecordDTO dto, JSONObject data) {}
}
