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

import biblivre.BiblivreInitializer;
import biblivre.administration.backup.BackupBO;
import biblivre.administration.setup.State;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.auth.AuthorizationPoints;
import biblivre.core.configurations.Configurations;
import biblivre.core.schemas.SchemasDAOImpl;
import biblivre.core.utils.Constants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MultipartConfig
public final class SchemaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doHead(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        this.processDynamicRequest(request, response, true);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            BiblivreInitializer.initialize();
            ExtendedRequest xRequest = ((ExtendedRequest) request);

            String controller = xRequest.getController();

            if (StringUtils.isNotBlank(controller) && controller.equals("status")) {
                Writer out = response.getWriter();
                JSONObject json = new JSONObject();

                SchemaThreadLocal.withSchema(
                        "public",
                        () -> {
                            try {
                                // TODO: Completar com mais mensagens.
                                // Checking Database
                                SchemaThreadLocal.setSchema("public");

                                if (!SchemasDAOImpl.getInstance().testDatabaseConnection()) {
                                    json.put("success", false);
                                    json.put("status_message", "Falha no acesso ao Banco de Dados");
                                } else {
                                    json.put("success", true);
                                    json.put("status_message", "Disponível");
                                }
                            } catch (JSONException e) {
                            }

                            return null;
                        });

                out.write(json.toString());

                return;
            }

            this.processDynamicRequest(request, response);
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
                        || Configurations.getBoolean(Constants.CONFIG_NEW_LIBRARY))) {
            xRequest.setAttribute("module", "menu");
            xRequest.setAttribute("action", "setup");
            controller = "jsp";
        }

        if (controller.equals("jsp")) {
            JspController jspController = new JspController(xRequest, xResponse);
            jspController.setHeaderOnly(headerOnly);
            jspController.processRequest();

        } else if (controller.equals("json")) {
            JsonController jsonController = new JsonController(xRequest, xResponse);
            jsonController.setHeaderOnly(headerOnly);
            jsonController.processRequest();

        } else if (controller.equals("download")) {
            DownloadController downloadController = new DownloadController(xRequest, xResponse);
            downloadController.setHeaderOnly(headerOnly);
            downloadController.processRequest();

        } else if (controller.equals("media") || controller.equals("DigitalMediaController")) {
            xRequest.setAttribute("module", "digitalmedia");
            xRequest.setAttribute("action", "download");

            DownloadController downloadController = new DownloadController(xRequest, xResponse);
            downloadController.setHeaderOnly(headerOnly);
            downloadController.processRequest();

        } else if (controller.equals("log")) {
            xResponse.setContentType("text/html;charset=UTF-8");
            xRequest.dispatch("/jsp/log.jsp", xResponse);
        } else {
            xResponse.setContentType("text/html;charset=UTF-8");

            String page = "/jsp/index.jsp";

            if (State.LOCKED.get()) {
                page = "/jsp/progress.jsp";
            }

            xRequest.dispatch(page, xResponse);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(BackupBO.class);
}
