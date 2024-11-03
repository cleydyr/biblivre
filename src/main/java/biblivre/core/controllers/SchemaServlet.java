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
package biblivre.core.controllers;

import biblivre.administration.backup.BackupBO;
import biblivre.administration.setup.State;
import biblivre.cataloging.TabFieldsBO;
import biblivre.circulation.user.UserFieldBO;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.IFCacheableJavascript;
import biblivre.core.RequestParserHelper;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.auth.AuthorizationPoints;
import biblivre.core.configurations.ConfigurationBO;
import biblivre.core.file.DiskFile;
import biblivre.core.translations.LanguageBO;
import biblivre.core.translations.TranslationBO;
import biblivre.core.utils.Constants;
import biblivre.core.utils.FileIOUtils;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.Serial;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class SchemaServlet extends HttpServlet {

    @Serial private static final long serialVersionUID = 1L;

    private JspController jspController;

    private DownloadController downloadController;

    private JsonController jsonController;

    private UserFieldBO userFieldBO;

    private TabFieldsBO tabFieldsBO;

    private LanguageBO languageBO;

    private TranslationBO translationBO;

    private ConfigurationBO configurationBO;

    private RequestParserHelper requestParserHelper;

    @Value("${biblivre.logo.href:https://biblivre.org.br}")
    private String logoHref;

    @Value("${biblivre.logo.image.src:/static/images/logo_biblivre.png}")
    private String logoImageSrc;

    @Value("${biblivre.logo.image.alt:Biblivre}")
    private String logoImageAlt;

    @Value("${biblivre.footer.copyright.pre:Copyright ©}")
    private String footerPre;

    @Value("${biblivre.footer.copyright.href:https://biblivre.org.br}")
    private String footerHref;

    @Value("${biblivre.footer.copyright.text:Biblivre}")
    private String footerText;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Detour to the MVC controllers, i.e., Spring's DispatcherServlet if the request path
        // starts with /api
        if (request.getServletPath().startsWith("/api")) {
            RequestDispatcher rd = this.getServletContext().getNamedDispatcher("dispatcherServlet");

            rd.forward(request, response);

            return;
        }

        wireUpControllers(request, response);

        super.service(request, response);
    }

    private void wireUpControllers(HttpServletRequest request, HttpServletResponse response) {
        jspController.setRequest((ExtendedRequest) request);
        jspController.setResponse((ExtendedResponse) response);

        downloadController.setRequest((ExtendedRequest) request);
        downloadController.setResponse((ExtendedResponse) response);

        jsonController.setRequest((ExtendedRequest) request);
        jsonController.setResponse((ExtendedResponse) response);
    }

    @Override
    protected void doHead(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        boolean isStatic = path.contains("static/") || path.contains("extra/");

        if (isStatic) {
            this.processStaticRequest(request, response, true);
        } else {
            this.processDynamicRequest(request, response, true);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            String path = request.getServletPath();

            boolean isStatic = path.contains("static/") || path.contains("extra/");

            if (isStatic) {
                this.processStaticRequest(request, response);
            } else {
                this.processDynamicRequest(request, response);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        this.processDynamicRequest(request, response);
    }

    protected void processDynamicRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        this.processDynamicRequest(request, response, false);
    }

    protected void processDynamicRequest(
            HttpServletRequest request, HttpServletResponse response, boolean headerOnly)
            throws ServletException, IOException {
        ExtendedRequest xRequest = (ExtendedRequest) request;
        ExtendedResponse xResponse = (ExtendedResponse) response;

        String controller = xRequest.getController();
        String module = xRequest.getString("module");
        String action = xRequest.getString("action");

        AuthorizationPoints notLoggedAtps = AuthorizationPoints.getNotLoggedInstance();
        xRequest.setAttribute("notLoggedAtps", notLoggedAtps);

        // If there is an action but there isn't any controller or module, it's
        // a menu action
        if (StringUtils.isBlank(controller)
                && StringUtils.isBlank(module)
                && StringUtils.isNotBlank(action)) {
            xRequest.setAttribute("module", "menu");
            controller = "jsp";
        } else if (StringUtils.isBlank(controller)
                && (xRequest.getBoolean("force_setup")
                        || configurationBO.getBoolean(Constants.CONFIG_NEW_LIBRARY))) {
            xRequest.setAttribute("module", "menu");
            xRequest.setAttribute("action", "setup");
            controller = "jsp";
        }

        request.setAttribute("logoHref", logoHref);
        request.setAttribute("logoImageSrc", logoImageSrc);
        request.setAttribute("logoImageAlt", logoImageAlt);
        request.setAttribute("footerHref", footerHref);
        request.setAttribute("footerText", footerText);
        request.setAttribute("footerPre", footerPre);

        switch (controller) {
            case "jsp" -> {
                jspController.setHeaderOnly(headerOnly);
                jspController.processRequest();
            }
            case "json" -> {
                jsonController.setHeaderOnly(headerOnly);
                jsonController.processRequest();
            }
            case "download" -> {
                downloadController.setHeaderOnly(headerOnly);
                downloadController.processRequest();
            }
            case "media", "DigitalMediaController" -> {
                xRequest.setAttribute("module", "digitalmedia");
                xRequest.setAttribute("action", "download");
                downloadController.setHeaderOnly(headerOnly);
                downloadController.processRequest();
            }
            case "log" -> {
                xResponse.setContentType("text/html;charset=UTF-8");
                xRequest.dispatch("/WEB-INF/jsp/log.jsp", xResponse);
            }
            default -> {
                xResponse.setContentType("text/html;charset=UTF-8");
                String page = "/WEB-INF/jsp/index.jsp";
                if (State.LOCKED.get()) {
                    page = "/WEB-INF/jsp/progress.jsp";
                }
                xRequest.dispatch(page, xResponse);
            }
        }
    }

    protected void processStaticRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        this.processStaticRequest(request, response, false);
    }

    protected void processStaticRequest(
            HttpServletRequest request, HttpServletResponse response, boolean headerOnly)
            throws ServletException, IOException {
        final String path = request.getServletPath();
        final String realPath =
                path.substring(path.lastIndexOf(path.contains("static/") ? "/static" : "/extra"));

        if (realPath.endsWith(".i18n.js")
                || realPath.endsWith(".form.js")
                || realPath.endsWith(".user_fields.js")) {
            String filename = StringUtils.substringAfterLast(path, "/");
            String[] params = StringUtils.split(filename, ".");

            String schema = params[0];

            IFCacheableJavascript javascript;

            if (realPath.endsWith(".i18n.js")) {
                javascript =
                        SchemaThreadLocal.withSchema(schema, () -> translationBO.get(params[1]));
            } else if (realPath.endsWith(".user_fields.js")) {
                javascript = SchemaThreadLocal.withSchema(schema, userFieldBO::getFields);
            } else {
                javascript =
                        SchemaThreadLocal.withSchema(
                                schema, () -> tabFieldsBO.getFormFields(params[2]));
            }

            File cacheFile = javascript.getCacheFile();

            if (cacheFile != null) {
                DiskFile diskFile = new DiskFile(cacheFile, "application/javascript;charset=UTF-8");

                FileIOUtils.sendHttpFile(diskFile, request, response, headerOnly);
            } else {
                response.getOutputStream().print(javascript.toJavascriptString());
            }
            return;
        }

        // Other static files
        RequestDispatcher rd = this.getServletContext().getNamedDispatcher("dispatcherServlet");

        ExtendedRequest wrapped =
                new ExtendedRequest(request, requestParserHelper, languageBO, translationBO) {

                    @Override
                    public String getServletPath() {
                        return realPath;
                    }
                };

        rd.forward(wrapped, response);
    }

    private static final Logger logger = LoggerFactory.getLogger(BackupBO.class);

    @Autowired
    public void setJspController(JspController jspController) {
        this.jspController = jspController;
    }

    @Autowired
    public void setDownloadController(DownloadController downloadController) {
        this.downloadController = downloadController;
    }

    @Autowired
    public void setJsonController(JsonController jsonController) {
        this.jsonController = jsonController;
    }

    @Autowired
    public void setUserFields(UserFieldBO userFieldBO) {
        this.userFieldBO = userFieldBO;
    }

    @Autowired
    public void setTabFieldsBO(TabFieldsBO tabFieldsBO) {
        this.tabFieldsBO = tabFieldsBO;
    }

    @Autowired
    public void setLanguageBO(LanguageBO languageBO) {
        this.languageBO = languageBO;
    }

    @Autowired
    public void setTranslationBO(TranslationBO translationBO) {
        this.translationBO = translationBO;
    }

    @Autowired
    public void setConfigurationBO(ConfigurationBO configurationBO) {
        this.configurationBO = configurationBO;
    }
}
