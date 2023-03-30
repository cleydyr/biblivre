package biblivre.login;

import biblivre.circulation.user.UserDTO;

public interface LoginDAO {

    LoginDTO get(Integer loginId);

    LoginDTO login(String login, String password);

    boolean update(LoginDTO login);

    LoginDTO getByLogin(String loginName);

    boolean delete(UserDTO userDTO);

    boolean save(LoginDTO dto, UserDTO udto);

    LoginDTO login(String login, byte[] saltedPassword);
}
