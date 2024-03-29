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
package biblivre.administration.permissions;

import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractDTO;
import biblivre.login.LoginDTO;
import java.io.Serial;
import java.util.Collection;
import org.json.JSONObject;

public class PermissionDTO extends AbstractDTO {

    @Serial private static final long serialVersionUID = 1L;

    private Collection<String> permissions;
    private LoginDTO login;
    private UserDTO user;

    public LoginDTO getLogin() {
        return this.login;
    }

    public void setLogin(LoginDTO login) {
        this.login = login;
    }

    public Collection<String> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(Collection<String> permissions) {
        this.permissions = permissions;
    }

    public UserDTO getUser() {
        return this.user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject json = super.toJSONObject();

        json.putOpt("id", (this.getUser() != null) ? this.getUser().getId() : 0);

        return json;
    }
}
