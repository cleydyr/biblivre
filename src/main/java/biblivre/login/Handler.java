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

import biblivre.administration.backup.BackupBO;
import biblivre.administration.backup.BackupDTO;
import biblivre.administration.indexing.IndexingBO;
import biblivre.administration.permissions.PermissionBO;
import biblivre.core.AbstractHandler;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.auth.AuthorizationPointTypes;
import biblivre.core.auth.AuthorizationPoints;
import biblivre.core.enums.ActionResult;
import biblivre.core.utils.CalendarUtils;
import biblivre.core.utils.Constants;
import biblivre.core.utils.TextUtils;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class Handler extends AbstractHandler {
    private LoginBO loginBO;
    private IndexingBO indexingBO;
    private PermissionBO permissionBO;
    private BackupBO backupBO;

    public void login(ExtendedRequest request, ExtendedResponse response) {

        String username = request.getString("username");
        String password = request.getString("password");

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            this.message.setText(ActionResult.WARNING, "login.access_denied");
            this.jspURL = "/jsp/index.jsp";
            return;
        }

        LoginDTO user = loginBO.login(username, password);

        if (user != null) {
            AuthorizationPoints atps = permissionBO.getUserAuthorizationPoints(user);

            _setAdmin(user, atps);

            _performChecks(request, password, user, atps);

            _populateSessionAttributes(request, user, atps);

            this.message.setText(ActionResult.NORMAL, "login.welcome");
            this.jspURL = "/jsp/index.jsp";
            return;
        } else {
            this.message.setText(ActionResult.WARNING, "login.access_denied");
            this.jspURL = "/jsp/index.jsp";
            return;
        }
    }

    public void logout(ExtendedRequest request, ExtendedResponse response) {

        String language = request.getLanguage();
        HttpSession session = request.getSession();

        Enumeration<String> attributes = session.getAttributeNames();

        while (attributes.hasMoreElements()) {
            String attribute = attributes.nextElement();

            if (attribute != null && attribute.startsWith(SchemaThreadLocal.get() + ".")) {
                session.removeAttribute(attribute);
            }
        }

        request.setScopedSessionAttribute("language", language);

        this.message.setText(ActionResult.NORMAL, "login.goodbye");
        this.jspURL = "/jsp/index.jsp";
        return;
    }

    public void changePassword(ExtendedRequest request, ExtendedResponse response) {

        int loggedId = request.getLoggedUserId();

        String newPassword = request.getString("new_password");
        LoginDTO login = loginBO.get(loggedId);
        login.setModifiedBy(loggedId);
        login.setEncPassword(TextUtils.encodePassword(newPassword));
        loginBO.update(login);

        boolean warningPassword =
                newPassword.equals("abracadabra") && login.getLogin().equals("admin");
        request.setScopedSessionAttribute("system_warning_password", warningPassword);

        this.message.setText(ActionResult.SUCCESS, "login.password.success");
        this.jspURL = "/jsp/administration/password.jsp";
        return;
    }

    private void _performChecks(
            ExtendedRequest request, String password, LoginDTO user, AuthorizationPoints atps) {

        if (atps.isAdmin()) {
            _checkDefaultPassword(request, password);
        }

        if (!SchemaThreadLocal.isGlobalSchema()) {
            _checkLastBackup(request, atps);

            _checkOutdatedIndexes(request, atps);
        }
    }

    private void _populateSessionAttributes(
            ExtendedRequest request, LoginDTO user, AuthorizationPoints atps) {

        request.setScopedSessionAttribute("logged_user", user);
        request.setScopedSessionAttribute("logged_user_atps", atps);

        try {
            _populateMenus(request, atps);
        } catch (Exception e) { // Should never happen
            this.setMessage(e);
        }
    }

    private void _setAdmin(LoginDTO user, AuthorizationPoints atps) {

        if ((user.getId() == 1) || SchemaThreadLocal.get().equals(Constants.GLOBAL_SCHEMA)) {
            atps.setAdmin(true);
        }
    }

    private void _checkDefaultPassword(ExtendedRequest request, String password) {
        boolean warningPassword = password.toLowerCase().equals("abracadabra");
        request.setScopedSessionAttribute("system_warning_password", warningPassword);
    }

    private void _checkLastBackup(ExtendedRequest request, AuthorizationPoints atps) {

        if (atps.isAllowed(AuthorizationPointTypes.ADMINISTRATION_BACKUP)) {
            boolean warningBackup = false;

            BackupDTO lastBackup = backupBO.getLastBackup();

            if (lastBackup == null) {
                // bbo.simpleBackup();
                warningBackup = true;
            } else {
                int diff =
                        CalendarUtils.calculateDateDifference(lastBackup.getCreated(), new Date());

                warningBackup = (diff >= 3);
            }

            request.setScopedSessionAttribute("system_warning_backup", warningBackup);
        }
    }

    private void _checkOutdatedIndexes(ExtendedRequest request, AuthorizationPoints atps) {
        if (atps.isAllowed(AuthorizationPointTypes.ADMINISTRATION_INDEXING)) {
            boolean warningReindex = indexingBO.isIndexOutdated();
            request.setScopedSessionAttribute("system_warning_reindex", warningReindex);
        }
    }

    private static void _populateMenus(ExtendedRequest request, AuthorizationPoints atps)
            throws Exception {

        JSONObject json = new JSONObject(_readMenusFile());

        Map<Integer, JSONObject> prioritizedMenus = _getPrioritizedMenus(json);

        Map<String, List<String>> allowedModules = new LinkedHashMap<>();

        prioritizedMenus
                .values()
                .forEach(
                        module -> {
                            JSONArray items = module.getJSONArray("items");

                            String name = module.getString("name");

                            items.forEach(
                                    obj -> {
                                        String item = obj.toString();

                                        if (atps.isAllowed("menu", item)) {
                                            allowedModules
                                                    .computeIfAbsent(name, __ -> new ArrayList<>())
                                                    .add(item);
                                        }
                                    });
                        });

        request.setSessionAttribute("modules", allowedModules);
    }

    private static Map<Integer, JSONObject> _getPrioritizedMenus(JSONObject json) {

        JSONArray modules = json.getJSONArray("modules");

        Map<Integer, JSONObject> prioritizedMenus = new TreeMap<>();

        modules.forEach(
                obj -> {
                    JSONObject module = (JSONObject) obj;

                    int priority = module.getInt("priority");

                    prioritizedMenus.put(priority, module);
                });
        return prioritizedMenus;
    }

    private static String _readMenusFile() throws IOException, URISyntaxException {
        return new String(
                Files.readAllBytes(
                        Paths.get(
                                Thread.currentThread()
                                        .getContextClassLoader()
                                        .getResource("/META-INF/menus/menus.json")
                                        .toURI())));
    }

    public void setLoginBO(LoginBO loginBO) {
        this.loginBO = loginBO;
    }

    public void setIndexingBO(IndexingBO indexingBO) {
        this.indexingBO = indexingBO;
    }

    public void setPermissionBO(PermissionBO permissionBO) {
        this.permissionBO = permissionBO;
    }

    public void setBackupBO(BackupBO backupBO) {
        this.backupBO = backupBO;
    }
}
