package biblivre.administration.usertype;

import biblivre.reports.generated.api.UserTypesApiDelegate;
import biblivre.reports.generated.api.model.RestUserType;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserTypesApiDelegateImpl implements UserTypesApiDelegate {
    private UserTypeBO userTypeBO;

    @Override
    public ResponseEntity<List<RestUserType>> getUserTypes() {
        return new ResponseEntity<>(
                userTypeBO.list().stream().map(this::toRestUserType).toList(), HttpStatus.OK);
    }

    private RestUserType toRestUserType(UserTypeDTO dto) {
        RestUserType restUserType = new RestUserType();
        restUserType.setId(dto.getId());
        restUserType.setName(dto.getName());
        restUserType.setDescription(dto.getDescription());
        restUserType.setLendingLimit(dto.getLendingLimit());
        restUserType.setReservationLimit(dto.getReservationLimit());
        restUserType.setLendingTimeLimit(dto.getLendingTimeLimit());
        restUserType.setReservationTimeLimit(dto.getReservationTimeLimit());
        restUserType.setFineValue(dto.getFineValue());
        return restUserType;
    }

    @Autowired
    public void setUserTypeBO(UserTypeBO userTypeBO) {
        this.userTypeBO = userTypeBO;
    }
}
