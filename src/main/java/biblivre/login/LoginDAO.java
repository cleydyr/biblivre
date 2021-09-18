package biblivre.login;

import java.util.List;

import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractDTO;

public interface LoginDAO {

	LoginDTO get(Integer loginId);

	LoginDTO login(String login, String password);

	boolean update(LoginDTO login);

	LoginDTO getByLogin(String loginName);

	boolean delete(UserDTO userDTO);

	boolean save(LoginDTO dto, UserDTO udto);

	boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList);

}