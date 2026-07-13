package biblivre.administration.usertype;

import biblivre.reports.generated.api.UserTypeApiDelegate;
import biblivre.reports.generated.api.model.RestUserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserTypeApiDelegateImpl implements UserTypeApiDelegate {
    private UserTypeBO userTypeBO;

    @Override
    public ResponseEntity<RestUserType> getUserType(Integer id) {
        UserTypeDTO dto = userTypeBO.get(id);

        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(toRestUserType(dto), HttpStatus.OK);
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
