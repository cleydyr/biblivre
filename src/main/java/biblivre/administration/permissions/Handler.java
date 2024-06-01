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

import biblivre.circulation.user.PagedUserSearchWebHelper;
import biblivre.circulation.user.UserBO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractHandler;
import biblivre.core.DTOCollection;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.enums.ActionResult;
import biblivre.core.utils.TextUtils;
import biblivre.login.LoginBO;
import biblivre.login.LoginDTO;
import biblivre.search.SearchException;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Handler extends AbstractHandler {
    private PermissionBO permissionBO;
    private LoginBO loginBO;
    private UserBO userBO;

    biblivre.circulation.user.Handler userHandler;

    @Autowired private PagedUserSearchWebHelper pagedUserSearchWebHelper;

    public void search(ExtendedRequest request, ExtendedResponse response) {
        var pagedSearchDTO = pagedUserSearchWebHelper.getPagedUserSearchDTO(request);

        try {
            DTOCollection<UserDTO> userList = userBO.search(pagedSearchDTO);

            if (userList.isEmpty()) {
                this.setMessage(ActionResult.WARNING, "circulation.error.no_users_found");
                return;
            }

            DTOCollection<PermissionDTO> list = new DTOCollection<>();
            list.setPaging(userList.getPaging());

            for (UserDTO user : userList) {
                list.add(this.populatePermission(user));
            }

            put("search", list.toJSONObject());
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        } catch (SearchException e) {
            this.setMessage(ActionResult.ERROR, "error.internal_error");
        }
    }

    public void open(ExtendedRequest request, ExtendedResponse response) {
        int userId = request.getInteger("user_id");
        if (userId == 0) {
            this.setMessage(ActionResult.WARNING, "error.invalid_user");
            return;
        }

        UserDTO udto = userBO.get(userId);
        if (udto == null) {
            this.setMessage(ActionResult.WARNING, "error.invalid_user");
            return;
        }

        int loginId = udto.getLoginId();

        PermissionDTO dto = new PermissionDTO();
        dto.setUser(udto);

        if (loginId > 0) {
            dto.setLogin(loginBO.get(loginId));
            Collection<String> list = permissionBO.getByLoginId(loginId);
            if (list != null) {
                dto.setPermissions(list);
            }
        }
        try {
            put("permission", dto.toJSONObject());
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        }
    }

    public void save(ExtendedRequest request, ExtendedResponse response) {

        int userId = request.getInteger("user_id");
        if (userId == 0) {
            this.setMessage(ActionResult.WARNING, "error.invalid_user");
            return;
        }

        UserDTO udto = userBO.get(userId);

        if (udto == null) {
            this.setMessage(ActionResult.WARNING, "error.invalid_user");
            return;
        }

        String login = request.getString("new_login");
        String password = request.getString("new_password");
        boolean employee = request.getBoolean("employee", false);

        Integer loginId = udto.getLoginId();
        boolean newLogin = (loginId == null || loginId == 0);

        LoginDTO ldto = new LoginDTO();
        ldto.setLogin(login);
        ldto.setEmployee(employee);

        byte[] passwordSalt = TextUtils.generatePasswordSalt();

        ldto.setPasswordSalt(passwordSalt);

        if (StringUtils.isNotBlank(password)) {
            ldto.setSaltedPassword(TextUtils.encodeSaltedPassword(password, passwordSalt));
        }

        boolean result;

        if (newLogin) {
            ldto.setCreatedBy(request.getLoggedUserId());
            result = loginBO.save(ldto, udto);
        } else {
            ldto.setId(udto.getLoginId());
            ldto.setModifiedBy(request.getLoggedUserId());
            result = loginBO.update(ldto);
        }

        String[] permissions = request.getParameterValues("permissions[]");

        if (permissions != null) {
            result &= permissionBO.save(udto.getLoginId(), Arrays.asList(permissions));
        }

        if (result) {
            if (newLogin) {
                this.setMessage(
                        ActionResult.SUCCESS, "administration.permission.success.create_login");
            } else if (!password.isEmpty()) {
                this.setMessage(
                        ActionResult.SUCCESS, "administration.permission.success.password_saved");
            } else {
                this.setMessage(
                        ActionResult.SUCCESS,
                        "administration.permission.success.permissions_saved");
            }
        } else {
            this.setMessage(ActionResult.WARNING, "administration.permission.error.create_login");
        }

        PermissionDTO dto = this.populatePermission(udto);

        try {
            put("data", dto.toJSONObject());
            put("full_data", true);
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        }
    }

    public void delete(ExtendedRequest request, ExtendedResponse response) {
        int userId = request.getInteger("user_id");
        if (userId == 0) {
            this.setMessage(ActionResult.WARNING, "error.invalid_user");
            return;
        }

        UserDTO udto = userBO.get(userId);

        if (udto == null) {
            this.setMessage(ActionResult.WARNING, "error.invalid_user");
            return;
        }

        // WE DELETE THE LOGIN RECORD, AND THE PERMISSIONS WILL ALSO BE DELETED
        // BY THE LOGIN_BO

        if (loginBO.delete(udto)) {
            this.setMessage(ActionResult.SUCCESS, "administration.permission.success.delete");
        } else {
            this.setMessage(ActionResult.WARNING, "administration.permission.error.delete");
        }
    }

    private PermissionDTO populatePermission(UserDTO user) {
        PermissionDTO dto = new PermissionDTO();
        dto.setUser(user);

        if (user.getLoginId() == null || user.getLoginId() == 0) {
            return dto;
        }

        LoginDTO ldto = loginBO.get(user.getLoginId());

        if (ldto == null) {
            return dto;
        }

        dto.setLogin(ldto);

        Collection<String> permissions = permissionBO.getByLoginId(user.getLoginId());

        if (permissions == null) {
            return dto;
        }

        dto.setPermissions(permissions);

        return dto;
    }

    @Autowired
    public void setPermissionBO(PermissionBO permissionBO) {
        this.permissionBO = permissionBO;
    }

    @Autowired
    public void setLoginBO(LoginBO loginBO) {
        this.loginBO = loginBO;
    }

    @Autowired
    public void setUserBO(UserBO userBO) {
        this.userBO = userBO;
    }

    @Autowired
    public void setUserHandler(biblivre.circulation.user.Handler userHandler) {
        this.userHandler = userHandler;
    }
}
