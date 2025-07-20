package biblivre.circulation.user;

import java.util.List;

public interface UserFieldsDAO {

    List<UserFieldDTO> listFields();

    UserFieldDTO persistField(UserFieldDTO userFieldDTO);
}
