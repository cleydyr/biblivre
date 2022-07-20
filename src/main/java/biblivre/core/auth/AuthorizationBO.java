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
package biblivre.core.auth;

import biblivre.administration.permissions.PermissionDAO;
import biblivre.core.AbstractBO;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.exceptions.AuthorizationException;
import biblivre.login.LoginDTO;
import java.util.Collection;

public class AuthorizationBO extends AbstractBO {
    private PermissionDAO permissionDAO;

    public void authorize(AuthorizationPoints atps, String module, String action) {
        if (atps == null) {
            atps = AuthorizationPoints.getNotLoggedInstance();
        }

        if (!atps.isAllowed(module, action)) {
            throw new AuthorizationException();
        }
    }

    public AuthorizationPoints getUserAuthorizationPoints(LoginDTO login) {
        Collection<String> permissions = null;

        if (SchemaThreadLocal.isGlobalSchema()) {
            permissions = this.permissionDAO.getByLoginId(login.getId());
        }

        return new AuthorizationPoints(
                SchemaThreadLocal.get(), true, login.isEmployee(), permissions);
    }

    public void setAuthorizationDAO(PermissionDAO permissionDAO) {
        this.permissionDAO = permissionDAO;
    }
}
