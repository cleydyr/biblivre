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
package biblivre.core;

import biblivre.administration.permissions.PermissionBO;
import biblivre.core.auth.AuthorizationPoints;

public abstract class AbstractBO {
    private PermissionBO permissionBO;

    public void authorize(String module, String action, AuthorizationPoints authorizationPoints) {
        if (authorizationPoints == null) {
            authorizationPoints = AuthorizationPoints.getNotLoggedInstance();
        }

        permissionBO.authorize(authorizationPoints, module, action);
    }

    public void setPermissionBO(PermissionBO permissionBO) {
        this.permissionBO = permissionBO;
    }
}
