package biblivre.circulation.user;

import biblivre.reports.generated.api.CirculationApiDelegate;
import biblivre.reports.generated.api.model.RestUserSearchableField;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CirculationApiDelegateImpl implements CirculationApiDelegate {
    private UserFieldBO userFieldBO;

    @Override
    public ResponseEntity<List<RestUserSearchableField>> getUserSearchableFields() {
        return new ResponseEntity<>(
                userFieldBO.getSearchableFields().stream()
                        .map(this::toRestUserSearchableField)
                        .toList(),
                HttpStatus.OK);
    }

    private RestUserSearchableField toRestUserSearchableField(UserFieldDTO field) {
        RestUserSearchableField restField = new RestUserSearchableField();
        restField.setKey(field.getKey());
        restField.setType(RestUserSearchableField.TypeEnum.fromValue(field.getType().getString()));
        restField.setRequired(field.isRequired());
        restField.setMaxLength(field.getMaxLength());
        restField.setSortOrder(field.getSortOrder());
        return restField;
    }

    @Autowired
    public void setUserFieldBO(UserFieldBO userFieldBO) {
        this.userFieldBO = userFieldBO;
    }
}
