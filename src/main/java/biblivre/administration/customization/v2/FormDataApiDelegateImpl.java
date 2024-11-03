package biblivre.administration.customization.v2;

import biblivre.cataloging.FormTabDatafieldDTO;
import biblivre.cataloging.FormTabSubfieldDTO;
import biblivre.cataloging.TabFieldsBO;
import biblivre.cataloging.enums.AutocompleteType;
import biblivre.generated.api.FormDataApiDelegate;
import biblivre.generated.api.model.*;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class FormDataApiDelegateImpl implements FormDataApiDelegate {
    @Autowired private TabFieldsBO tabFieldsBO;

    @Override
    public ResponseEntity<List<RestFormData>> getFormData(RestRecordType recordType) {
        return ResponseEntity.ok(
                tabFieldsBO.getFormFields(recordType.toString()).stream()
                        .map(FormDataApiDelegateImpl::toRestFormData)
                        .toList());
    }

    public static RestFormData toRestFormData(FormTabDatafieldDTO formTabDatafieldDTO) {
        RestFormData restFormData = new RestFormData();

        restFormData.setDatafield(formTabDatafieldDTO.getDatafield());

        restFormData.setCollapsed(formTabDatafieldDTO.isCollapsed());

        restFormData.setRepeatable(formTabDatafieldDTO.isRepeatable());

        String[] indicator1Values = formTabDatafieldDTO.getIndicator1Values();

        restFormData.setIndicator1(indicator1Values == null ? null : List.of(indicator1Values));

        String[] indicator2Values = formTabDatafieldDTO.getIndicator2Values();

        restFormData.setIndicator2(indicator2Values == null ? null : List.of(indicator2Values));

        String[] materialTypeValues = formTabDatafieldDTO.getMaterialTypeValues();

        restFormData.setMaterialType(
                materialTypeValues == null ? null : List.of(materialTypeValues));

        restFormData.setSortOrder(formTabDatafieldDTO.getSortOrder());

        restFormData.setSubfields(
                formTabDatafieldDTO.getSubfields().stream()
                        .map(FormDataApiDelegateImpl::toRestSubfield)
                        .toList());

        return restFormData;
    }

    public static RestSubfield toRestSubfield(FormTabSubfieldDTO formTabSubfieldDTO) {
        RestSubfield restSubfield = new RestSubfield();

        restSubfield.setDatafield(formTabSubfieldDTO.getDatafield());

        restSubfield.setSubfield(formTabSubfieldDTO.getSubfield());

        restSubfield.collapsed(formTabSubfieldDTO.isCollapsed());

        restSubfield.setRepeatable(formTabSubfieldDTO.isRepeatable());

        AutocompleteType autocompleteType = formTabSubfieldDTO.getAutocompleteType();

        if (autocompleteType != null) {
            RestAutocompleteType restAutocompleteType =
                    RestAutocompleteType.fromValue(autocompleteType.toString());

            restSubfield.setAutocompleteType(restAutocompleteType);
        }

        restSubfield.setSortOrder(formTabSubfieldDTO.getSortOrder());

        return restSubfield;
    }
}
