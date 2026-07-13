package biblivre.administration.usertype;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import biblivre.reports.generated.api.model.RestUserType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class UserTypeApiDelegateImplTest {

    @Test
    void getUserType_mapsFieldsFromUserTypeBO() {
        UserTypeDTO dto = new UserTypeDTO();
        dto.setId(1);
        dto.setName("Student");
        dto.setDescription("Default student type");
        dto.setLendingLimit(3);
        dto.setReservationLimit(2);
        dto.setLendingTimeLimit(7);
        dto.setReservationTimeLimit(3);
        dto.setFineValue(1.5f);

        UserTypeBO userTypeBO = mock(UserTypeBO.class);
        when(userTypeBO.get(1)).thenReturn(dto);

        UserTypeApiDelegateImpl delegate = new UserTypeApiDelegateImpl();
        delegate.setUserTypeBO(userTypeBO);

        ResponseEntity<RestUserType> response = delegate.getUserType(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId());
        assertEquals("Student", response.getBody().getName());
        assertEquals("Default student type", response.getBody().getDescription());
        assertEquals(3, response.getBody().getLendingLimit());
        assertEquals(2, response.getBody().getReservationLimit());
        assertEquals(7, response.getBody().getLendingTimeLimit());
        assertEquals(3, response.getBody().getReservationTimeLimit());
        assertEquals(1.5f, response.getBody().getFineValue());
    }

    @Test
    void getUserType_returnsNotFoundWhenUserTypeDoesNotExist() {
        UserTypeBO userTypeBO = mock(UserTypeBO.class);
        when(userTypeBO.get(99)).thenReturn(null);

        UserTypeApiDelegateImpl delegate = new UserTypeApiDelegateImpl();
        delegate.setUserTypeBO(userTypeBO);

        ResponseEntity<RestUserType> response = delegate.getUserType(99);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
