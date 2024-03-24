/*******************************************************************************
 * Este arquivo é parte do Biblivre5.
 *
 * Biblivre5 é um software livre; você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da Licença Pública Geral GNU como
 * publicada pela Fundação do Software Livre (FSF); na versão 3 da
 * Licença, ou (caso queira) qualquer versão posterior.
 *
 * Este programa é distribuído na esperança de que possa ser  útil,
 * mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 * MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
 * Licença Pública Geral GNU para maiores detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 * com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
 *
 * @author Alberto Wagner <alberto@biblivre.org.br>
 * @author Danniel Willian <danniel@biblivre.org.br>
 ******************************************************************************/
package biblivre.login;

import biblivre.administration.permissions.PermissionBO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractBO;
import biblivre.core.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoginBO extends AbstractBO {
    private LoginDAO loginDAO;
    private PermissionBO permissionBO;

    public final LoginDTO login(String login, String password) {
        LoginDTO loginDTO = loginDAO.getByLogin(login);

        if (loginDTO == null) {
            return null;
        }

        if (loginDTO.getPasswordSalt() == null) {
            String encodedPassword = TextUtils.encodePasswordSHA(password);

            loginDTO = this.loginDAO.login(login, encodedPassword);

            if (loginDTO != null) {
                updatePasswordAlgorithm(password, loginDTO);
            }
        } else {
            byte[] passwordSalt = loginDTO.getPasswordSalt();

            byte[] saltedPassword = TextUtils.encodeSaltedPassword(password, passwordSalt);

            if (loginDTO.getSaltedPassword() == null) {
                String utf8Password = new String(saltedPassword);

                loginDTO = this.loginDAO.login(login, utf8Password);

                if (loginDTO != null) {
                    loginDTO.setSaltedPassword(saltedPassword);

                    loginDAO.update(loginDTO);
                }
            } else {
                loginDTO = this.loginDAO.login(login, saltedPassword);
            }
        }

        return loginDTO;
    }

    private void updatePasswordAlgorithm(String password, LoginDTO loginDTO) {
        byte[] passwordSalt = TextUtils.generatePasswordSalt();

        loginDTO.setPasswordSalt(passwordSalt);

        byte[] saltedPassword = TextUtils.encodeSaltedPassword(password, passwordSalt);

        loginDTO.setSaltedPassword(saltedPassword);

        loginDAO.update(loginDTO);
    }

    public boolean update(LoginDTO login) {
        return this.loginDAO.update(login);
    }

    public boolean delete(UserDTO user) {
        permissionBO.delete(user);
        return this.loginDAO.delete(user);
    }

    public LoginDTO get(Integer loginId) {
        return this.loginDAO.get(loginId);
    }

    public boolean loginExists(String login) {
        return this.get(login) != null;
    }

    public LoginDTO get(String login) {
        return this.loginDAO.getByLogin(login);
    }

    public boolean save(LoginDTO dto, UserDTO udto) {
        return this.loginDAO.save(dto, udto);
    }

    @Autowired
    public void setLoginDAO(LoginDAO loginDAO) {
        this.loginDAO = loginDAO;
    }

    @Autowired
    public void setPermissionBO(PermissionBO permissionBO) {
        this.permissionBO = permissionBO;
    }
}
