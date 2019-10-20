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

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import biblivre.administration.setup.State;
import biblivre.cataloging.Fields;
import biblivre.circulation.user.UserFields;
import biblivre.core.AbstractHandler;
import biblivre.core.AppConfig;
import biblivre.core.BiblivreInitializer;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.FreemarkerTemplateHelper;
import biblivre.core.IFCacheableJavascript;
import biblivre.core.auth.AuthorizationPoints;
import biblivre.core.configurations.Configurations;
import biblivre.core.file.DiskFile;
import biblivre.core.schemas.SchemasDAO;
import biblivre.core.translations.Translations;
import biblivre.core.utils.Constants;
import biblivre.core.utils.FileIOUtils;

public final class SchemaServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;


	private static final AnnotationConfigApplicationContext _context =
			new AnnotationConfigApplicationContext(AppConfig.class);

	private static Map<String, AbstractHandler> _handlers =
			_context.getBeansOfType(AbstractHandler.class);

	@Override
	protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getServletPath();
		boolean isStatic = path.contains("static/") || path.contains("extra/");

		if (isStatic) {
			this.processStaticRequest(request, response, true);
		} else {
			this.processDynamicRequest(request, response, true);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		BiblivreInitializer.initialize();

		ExtendedRequest xRequest = ((ExtendedRequest) request);

		if (xRequest.mustRedirectToSchema()) {
			_sendRedirectToSchema(response, xRequest);

			return;
		}

		String controller = xRequest.getController();

		if (StringUtils.isNotBlank(controller) && controller.equals("status")) {
			_sendStatusMessage(response);

			return;
		}

		String path = request.getServletPath();

		boolean isStatic = path.contains("static/") || path.contains("extra/");

		if (isStatic) {
			this.processStaticRequest(request, response);
		} else {
			this.processDynamicRequest(request, response);
		}
	}

	private void _sendStatusMessage(HttpServletResponse response) throws IOException {
		Writer out = response.getWriter();

		JSONObject json = new JSONObject();

		try {
			// TODO: Completar com mais mensagens.
			// Checking Database
			if (!SchemasDAO.getInstance("public").testDatabaseConnection()) {
				json.put("success", false);
				json.put("status_message", "Falha no acesso ao Banco de Dados");
			} else {
				json.put("success", true);
				json.put("status_message", "Disponível");
			}
		} catch (JSONException e) {
		}

		out.write(json.toString());
	}

	private void _sendRedirectToSchema(HttpServletResponse response, ExtendedRequest xRequest) throws IOException {
		String query = xRequest.getQueryString();

		if (StringUtils.isNotBlank(query)) {
			query = "?" + query;
		} else {
			query = "";
		}

		((ExtendedResponse) response).sendRedirect(xRequest.getRequestURI() + "/" + query);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.processDynamicRequest(request, response);
	}

	protected void processDynamicRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.processDynamicRequest(request, response, false);
	}

	protected void processDynamicRequest(HttpServletRequest request, HttpServletResponse response, boolean headerOnly) throws ServletException, IOException {
		ExtendedRequest xRequest = (ExtendedRequest) request;
		ExtendedResponse xResponse = (ExtendedResponse) response;

		String controller = xRequest.getController();
		String module = xRequest.getString("module");
		String action = xRequest.getString("action");

		AuthorizationPoints notLoggedAtps = AuthorizationPoints.getNotLoggedInstance(xRequest.getSchema());
		xRequest.setAttribute("notLoggedAtps", notLoggedAtps);
		
		// If there is an action but there isn't any controller or module, it's
		// a menu action
		if (_isMenuAction(controller, module, action)) {
			xRequest.setAttribute("module", "menu");
			controller = "jsp";
		} else if (_isSetupAction(xRequest, controller)) {
			xRequest.setAttribute("module", "menu");
			xRequest.setAttribute("action", "setup");
			controller = "jsp";
		}

		if (controller.equals("jsp")) {
			JspController jspController = new JspController(xRequest, xResponse);

			jspController.setHeaderOnly(headerOnly);
			jspController.processRequest(_handlers);
		} else if (controller.equals("json")) {
			JsonController jsonController = new JsonController(xRequest, xResponse);

			jsonController.setHeaderOnly(headerOnly);
			jsonController.processRequest(_handlers);
		} else if (controller.equals("download")) {
			DownloadController downloadController = new DownloadController(xRequest, xResponse);

			downloadController.setHeaderOnly(headerOnly);
			downloadController.processRequest(_handlers);
		} else if (controller.equals("media") || controller.equals("DigitalMediaController")) {
			xRequest.setAttribute("module", "digitalmedia");
			xRequest.setAttribute("action", "download");

			DownloadController downloadController = new DownloadController(xRequest, xResponse);

			downloadController.setHeaderOnly(headerOnly);
			downloadController.processRequest(_handlers);
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

	private boolean _isSetupAction(ExtendedRequest xRequest, String controller) {
		return StringUtils.isBlank(controller) &&
				(xRequest.getBoolean("force_setup") ||
						Configurations.getBoolean(
								xRequest.getSchema(), Constants.CONFIG_NEW_LIBRARY));
	}

	private boolean _isMenuAction(String controller, String module, String action) {
		return StringUtils.isBlank(controller) && StringUtils.isBlank(module) &&
				StringUtils.isNotBlank(action);
	}

	protected void processStaticRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.processStaticRequest(request, response, false);
	}

	protected void processStaticRequest(
			HttpServletRequest request, HttpServletResponse response, boolean headerOnly)
			throws ServletException, IOException {

		final String path = request.getServletPath();

		final String relevantPath = _getRelevantPath(path);

		if (_isPseudoStaticJavascriptPath(relevantPath)) {
			IFCacheableJavascript javascript = _getCacheableJavascript(path, relevantPath);

			_sendJavascriptCodeAsResponse(request, response, headerOnly, javascript);

			return;
		}

		_dispatchToOtherStaticFiles(request, response, relevantPath);
	}

	private void _dispatchToOtherStaticFiles(HttpServletRequest request, HttpServletResponse response,
			final String realPath) throws ServletException, IOException {
		RequestDispatcher rd = this.getServletContext().getNamedDispatcher("default");

		ExtendedRequest wrapped = new ExtendedRequest(request) {

			@Override
			public String getServletPath() {
				return realPath;
			}
		};

		rd.forward(wrapped, response);
	}

	private IFCacheableJavascript _getCacheableJavascript(
			final String path, final String realPath) {
		String filename = StringUtils.substringAfterLast(path, "/");

		String[] params = StringUtils.split(filename, ".");

		String schema = params[0];
		
		if (realPath.endsWith(".i18n.js")) {
			return Translations.get(schema, params[1]);
		} else if (realPath.endsWith(".user_fields.js")) {
			return UserFields.getFields(schema);
		} else {
			return Fields.getFormFields(schema, params[2]);
		}
	}

	private boolean _isPseudoStaticJavascriptPath(final String realPath) {
		return realPath.endsWith(".i18n.js") || realPath.endsWith(".form.js") ||
				realPath.endsWith(".user_fields.js");
	}

	private void _sendJavascriptCodeAsResponse(
			HttpServletRequest request, HttpServletResponse response, boolean headerOnly,
			IFCacheableJavascript javascript) throws IOException {

		File cacheFile = javascript.getCacheFile();

		if (cacheFile != null) {
			DiskFile diskFile = new DiskFile(cacheFile, "application/javascript;charset=UTF-8");

			FileIOUtils.sendHttpFile(diskFile, request, response, headerOnly);
		} else {
			try (ServletOutputStream outputStream = response.getOutputStream()) {
				outputStream.print(javascript.toJavascriptString());
			};
		}
	}

	private String _getRelevantPath(final String path) {
		if (path.contains("static/")) {
			return path.substring(path.lastIndexOf("/static"));
		} else {
			return path.substring(path.lastIndexOf("/extra"));
		}
	}
	
	@Override
	public void init() throws ServletException {
		FreemarkerTemplateHelper.freemarkerConfiguration
			.setServletContextForTemplateLoading(getServletContext(), "/freemarker");
	}
}
