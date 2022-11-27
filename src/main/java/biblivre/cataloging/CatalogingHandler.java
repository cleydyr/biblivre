package biblivre.cataloging;

import biblivre.cataloging.enums.RecordDatabase;
import biblivre.core.AbstractDTO;
import biblivre.core.AbstractHandler;
import biblivre.core.DTOCollection;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.enums.ActionResult;
import biblivre.core.exceptions.ValidationException;
import biblivre.marc.MarcDataReader;
import biblivre.marc.MaterialType;
import biblivre.marc.RecordStatus;
import java.util.List;
import org.json.JSONObject;
import org.marc4j.MarcReader;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class CatalogingHandler extends AbstractHandler {
    protected RecordBO recordBO;
    protected MaterialType defaultMaterialType;

    protected TabFieldsBO tabFieldsBO;

    public CatalogingHandler(RecordBO recordBO, MaterialType defaultMaterialType) {
        this.recordBO = recordBO;

        this.defaultMaterialType = defaultMaterialType;
    }

    public void open(ExtendedRequest request, ExtendedResponse response) {
        Integer id = request.getInteger("id", null);

        if (id == null) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.record_not_found");

            return;
        }

        RecordDTO dto = recordBO.open(id, request.getAuthorizationPoints());

        if (dto == null) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.record_not_found");

            return;
        }

        JSONObject data = dto.toJSONObject();

        populateFields(dto, data);

        put("data", data);
    }

    protected void populateFields(RecordDTO dto, JSONObject data) {
        MarcDataReader marcDataReader = new MarcDataReader(dto.getRecord());

        List<BriefTabFieldDTO> fieldList =
                marcDataReader.getFieldList(tabFieldsBO.getBriefFormats(dto.getRecordType()));

        DTOCollection<AbstractDTO> collection = new DTOCollection<>();

        collection.addAll(fieldList);

        dto.addExtraData("fields", collection);

        dto.populateExtraData(data);
    }

    public void save(ExtendedRequest request, ExtendedResponse response) {
        Integer id = request.getInteger("id");

        boolean isNew = id == 0;

        RecordDTO recordDTO = isNew ? this.createRecordDTO(null) : recordBO.get(id);

        if (recordDTO == null) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.record_not_found");

            return;
        }

        hydrateRecord(recordDTO, request);

        int loggedUserId = request.getLoggedUserId();

        boolean success =
                recordBO.saveOrUpdate(recordDTO, loggedUserId, request.getAuthorizationPoints());

        if (!success) {
            this.setMessage(ActionResult.WARNING, "cataloging.record.error.save");

            return;
        }

        _setSuccessMessage(isNew);

        put("data", recordDTO.toJSONObject());
    }

    private void hydrateRecord(RecordDTO recordDTO, ExtendedRequest request) {
        MaterialType materialType =
                request.getEnum(MaterialType.class, "material_type", this.defaultMaterialType);

        recordDTO.setMaterialType(materialType);

        RecordDatabase recordDatabase = request.getEnum(RecordDatabase.class, "database");

        recordDTO.setRecordDatabase(recordDatabase);

        RecordConvertion recordConvertion = request.getEnum(RecordConvertion.class, "from");

        String recordData = request.getString("data");

        boolean isNew = request.getInteger("id") == 0;

        RecordStatus recordStatus = RecordStatus.fromNewStatus(isNew);

        MarcReader marcReader = recordConvertion.getReader(recordData, materialType, recordStatus);

        recordDTO.setRecord(marcReader.next());

        doHydrateRecord(recordDTO, request);
    }

    protected void doHydrateRecord(RecordDTO recordDTO, ExtendedRequest request) {
        int recordId = request.getInteger("id");

        recordDTO.setId(recordId);
    }

    protected abstract RecordDTO createRecordDTO(ExtendedRequest request);

    private void _setSuccessMessage(boolean isNew) {
        if (isNew) {
            this.setMessage(ActionResult.SUCCESS, "cataloging.record.success.save");
        } else {
            this.setMessage(ActionResult.SUCCESS, "cataloging.record.success.update");
        }
    }

    public void delete(ExtendedRequest request, ExtendedResponse response) {
        Integer id = request.getInteger("id", null);

        if (id == null) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.record_not_found");

            return;
        }

        RecordDTO dto = recordBO.get(id);

        RecordDatabase recordDatabase = request.getEnum(RecordDatabase.class, "database");

        if (dto == null || dto.getRecordDatabase() != recordDatabase) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.record_not_found");

            return;
        }

        if (recordDatabase == null) {
            ValidationException ex = new ValidationException("cataloging.error.invalid_database");

            this.setMessage(ex);

            return;
        }

        int loggedUserId = request.getLoggedUserId();

        boolean success = recordBO.delete(dto, loggedUserId, request.getAuthorizationPoints());

        if (success) {
            this.setMessage(ActionResult.SUCCESS, "cataloging.record.success.delete");
        } else {
            this.setMessage(ActionResult.WARNING, "cataloging.record.error.delete");
        }
    }

    public void setRecordBO(RecordBO recordBO) {
        this.recordBO = recordBO;
    }

    public void setDefaultMaterialType(MaterialType defaultMaterialType) {
        this.defaultMaterialType = defaultMaterialType;
    }

    @Autowired
    public void setTabFieldsBO(TabFieldsBO tabFieldsBO) {
        this.tabFieldsBO = tabFieldsBO;
    }
}
