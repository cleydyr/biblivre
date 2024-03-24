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
import biblivre.core.utils.TextUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

@Component
public class Handler extends AbstractHandler {
    private LoginBO loginBO;
    private IndexingBO indexingBO;
    private PermissionBO permissionBO;
    private BackupBO backupBO;

    @Autowired MenuProvider menuProvider;

    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    public void login(ExtendedRequest request, ExtendedResponse response) {

        String username = request.getString("username");
        String password = request.getString("password");

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            setMessage(ActionResult.WARNING, "login.access_denied");
            setJspURL("/WEB-INF/jsp/index.jsp");
            return;
        }

        LoginDTO user = loginBO.login(username, password);

        if (user != null) {
            AuthorizationPoints atps = permissionBO.getUserAuthorizationPoints(user);

            _setAdmin(user, atps);

            _performChecks(request, password, atps);

            _populateSessionAttributes(request, user, atps);

            authenticateWithSpringSecurity(user, request, response);

            setMessage(ActionResult.NORMAL, "login.welcome");
        } else {
            setMessage(ActionResult.WARNING, "login.access_denied");
        }

        setJspURL("/WEB-INF/jsp/index.jsp");
    }

    private void authenticateWithSpringSecurity(
            LoginDTO user, HttpServletRequest request, HttpServletResponse response) {
        Collection<? extends GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_USER"));

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user.getLogin(), null, authorities);

        SecurityContextHolderStrategy securityContextHolderStrategy =
                SecurityContextHolder.getContextHolderStrategy();

        SecurityContext context = securityContextHolderStrategy.createEmptyContext();

        context.setAuthentication(authentication);

        securityContextHolderStrategy.setContext(context);

        securityContextRepository.saveContext(context, request, response);
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

        setMessage(ActionResult.NORMAL, "login.goodbye");
        setJspURL("/WEB-INF/jsp/index.jsp");
    }

    public void changePassword(ExtendedRequest request, ExtendedResponse response) {

        int loggedId = request.getLoggedUserId();

        String newPassword = request.getString("new_password");

        LoginDTO login = loginBO.get(loggedId);

        login.setModifiedBy(loggedId);

        byte[] passwordSalt = TextUtils.generatePasswordSalt();

        login.setSaltedPassword(TextUtils.encodeSaltedPassword(newPassword, passwordSalt));

        login.setPasswordSalt(passwordSalt);

        loginBO.update(login);

        boolean warningPassword =
                newPassword.equals("abracadabra") && login.getLogin().equals("admin");
        request.setScopedSessionAttribute("system_warning_password", warningPassword);

        setMessage(ActionResult.SUCCESS, "login.password.success");
        setJspURL("/WEB-INF/jsp/administration/password.jsp");
    }

    private void _performChecks(
            ExtendedRequest request, String password, AuthorizationPoints atps) {

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

        if ((user.getId() == 1) || SchemaThreadLocal.isGlobalSchema()) {
            atps.setAdmin(true);
        }
    }

    private void _checkDefaultPassword(ExtendedRequest request, String password) {
        boolean warningPassword = password.equalsIgnoreCase("abracadabra");
        request.setScopedSessionAttribute("system_warning_password", warningPassword);
    }

    private void _checkLastBackup(ExtendedRequest request, AuthorizationPoints atps) {

        if (atps.isAllowed(AuthorizationPointTypes.ADMINISTRATION_BACKUP)) {
            boolean warningBackup;

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

    private void _populateMenus(ExtendedRequest request, AuthorizationPoints atps) {

        request.setSessionAttribute(
                "modules", menuProvider.getAllowedModules(item -> atps.isAllowed("menu", item)));
    }

    @Autowired
    public void setLoginBO(LoginBO loginBO) {
        this.loginBO = loginBO;
    }

    @Autowired
    public void setIndexingBO(IndexingBO indexingBO) {
        this.indexingBO = indexingBO;
    }

    @Autowired
    public void setPermissionBO(PermissionBO permissionBO) {
        this.permissionBO = permissionBO;
    }

    @Autowired
    public void setBackupBO(BackupBO backupBO) {
        this.backupBO = backupBO;
    }
}
