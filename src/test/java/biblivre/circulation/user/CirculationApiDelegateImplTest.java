package biblivre.circulation.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import biblivre.reports.generated.api.model.RestUserSearchableField;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class CirculationApiDelegateImplTest {

    @Test
    void getUserSearchableFields_mapsFieldsFromUserFieldBO() {
        UserFieldDTO nameField = new UserFieldDTO();
        nameField.setKey("name");
        nameField.setType(UserFieldType.STRING);
        nameField.setRequired(true);
        nameField.setMaxLength(100);
        nameField.setSortOrder(1);

        UserFieldDTO emailField = new UserFieldDTO();
        emailField.setKey("email");
        emailField.setType(UserFieldType.TEXT);
        emailField.setRequired(false);
        emailField.setMaxLength(255);
        emailField.setSortOrder(2);

        UserFieldBO userFieldBO = mock(UserFieldBO.class);
        when(userFieldBO.getSearchableFields()).thenReturn(List.of(nameField, emailField));

        CirculationApiDelegateImpl delegate = new CirculationApiDelegateImpl();
        delegate.setUserFieldBO(userFieldBO);

        ResponseEntity<List<RestUserSearchableField>> response = delegate.getUserSearchableFields();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        RestUserSearchableField firstField = response.getBody().getFirst();
        assertEquals("name", firstField.getKey());
        assertEquals(RestUserSearchableField.TypeEnum.STRING, firstField.getType());
        assertEquals(true, firstField.getRequired());
        assertEquals(100, firstField.getMaxLength());
        assertEquals(1, firstField.getSortOrder());

        RestUserSearchableField secondField = response.getBody().get(1);
        assertEquals("email", secondField.getKey());
        assertEquals(RestUserSearchableField.TypeEnum.TEXT, secondField.getType());
    }
}
