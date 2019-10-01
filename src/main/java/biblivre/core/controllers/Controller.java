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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import biblivre.administration.permissions.PermissionConfiguration;
import biblivre.administration.setup.State;
import biblivre.core.AbstractHandler;
import biblivre.core.AbstractValidator;
import biblivre.core.AppConfig;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.auth.AuthorizationBO;
import biblivre.core.auth.AuthorizationPoints;
import biblivre.core.configurations.Configurations;
import biblivre.core.exceptions.AuthorizationException;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.utils.Constants;
import biblivre.core.utils.TextUtils;


public abstract class Controller {

	private static Logger _logger = LoggerFactory.getLogger(Controller.class);

	protected ExtendedRequest xRequest;
	protected ExtendedResponse xResponse;
	protected AbstractHandler handler;
	protected boolean headerOnly;
	protected Class<?> handlerClass;

	public Controller(ExtendedRequest xRequest, ExtendedResponse xResponse) {
		this.xRequest = xRequest;
		this.xResponse = xResponse;
		this.headerOnly = false;
	}

	public boolean isHeaderOnly() {
		return this.headerOnly;
	}

	public void setHeaderOnly(boolean headerOnly) {
		this.headerOnly = headerOnly;
	}

	protected void processRequest() throws ServletException, IOException {
		
		this.xRequest.setCharacterEncoding(Constants.DEFAULT_CHARSET.name());
		this.xResponse.setCharacterEncoding(Constants.DEFAULT_CHARSET.name());
		
		String schema = this.xRequest.getSchema();
		String module = this.xRequest.getString("module", (String) this.xRequest.getAttribute("module"));
		String action = this.xRequest.getString("action", (String) this.xRequest.getAttribute("action"));

		try {
			// In case of invalid package and method, send user to index page
			if (_checkValidRequestParameters(module, action)) {
				this.doError("error.void");
				return;
			}

			if (_checkLockedAndNotRequestingProgressInformation(action)) {
				this.doLockedStateError();
				return;	
			}

			if (!_isFirstSetup(schema, module, action)) {
				_checkAuthorization(schema, module, action);
			}			
		} catch (AuthorizationException e) {
			this.doAuthorizationError();

			return;	
		}
		
		try {
			this.handlerClass = Class.forName("biblivre." + module + ".Handler");

			this.handler = _tryGettingHandlerFromContextBean(this.handlerClass);

			if (this.handler == null) {
				this.handler = (AbstractHandler) this.handlerClass.newInstance();
			}

			if (!_isValidActionForModule(module, action)) {
				this.doReturn();

				return;
			}
		} catch (AuthorizationException e) {
			this.doAuthorizationError();
		} catch (ClassNotFoundException | NoSuchMethodException e) {
			//No Validator found, do nothing.
			//No Method found in the Validator, so do nothing.
		} catch (InvocationTargetException e) {
			// Exception thrown in method.invoke
			Throwable handlerException = e.getTargetException();

			if (handlerException instanceof AuthorizationException) {
				this.doAuthorizationError();
			} else {
				this.doError("error.runtime_error", handlerException);
			}

			_logger.error(e.getMessage(), e);

			return;
		} catch (Exception e) {
			// ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, etc.

			_logger.warn("Unknown handler", e);

			this.doError("error.invalid_handler", e);

			return;
		}
		
		try {
			_executeAppropriatedMethodForAction(action);
		} catch (InvocationTargetException e) {
			_handleActionExecutionException(e);

			return;
		} catch (Exception e) {
			_handleGenericException(e);

			return;
		}

		this.doReturn();
	}

	private void _executeAppropriatedMethodForAction(String action)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Method method = null;

		try {
			method =
					this.handlerClass.getDeclaredMethod(
							TextUtils.camelCase(action), ExtendedRequest.class,
							ExtendedResponse.class);
		} catch (NoSuchMethodException e) {
			Class<?> superclass = this.handlerClass.getSuperclass();

			method =
					superclass.getDeclaredMethod(
							TextUtils.camelCase(action), ExtendedRequest.class,
							ExtendedResponse.class);
		}

		method.invoke(this.handler, this.xRequest, this.xResponse);
	}

	private boolean _isValidActionForModule(String module, String action) throws Exception {
		AbstractValidator validator = _getValidator(module, action);

		return validator.checkValidation(this.handler);
	}

	private AbstractValidator _getValidator(String module, String action) throws Exception {
		Class<?> validatorClass = Class.forName("biblivre." + module + ".Validator");

		String validationMethodName = "validate_" + action;

		Method validationMethod =
				validatorClass.getDeclaredMethod(
						TextUtils.camelCase(validationMethodName), AbstractHandler.class,
						ExtendedRequest.class, ExtendedResponse.class);

		AbstractValidator validator = (AbstractValidator) validatorClass.newInstance();

		validationMethod.invoke(validator, this.handler, this.xRequest, this.xResponse);

		return validator;
	}

	private void _checkAuthorization(String schema, String module, String action) {
		AuthorizationPoints authPoints =
				(AuthorizationPoints) this.xRequest.getSessionAttribute(schema,
						"logged_user_atps");

		if (authPoints == null) {
			authPoints = AuthorizationPoints.getNotLoggedInstance(schema);
		}

		AuthorizationBO abo = AuthorizationBO.getInstance(schema);

		abo.authorize(authPoints, module, action);
	}

	private boolean _isFirstSetup(String schema, String module, String action) {
		return (module.equals("administration.setup") || (module.equals("menu") &&
				action.equals("setup"))) &&
				(Configurations.getBoolean(schema, Constants.CONFIG_NEW_LIBRARY) ||
						action.equals("progress"));
	}

	private boolean _checkLockedAndNotRequestingProgressInformation(String action) {
		return State.LOCKED.get() && !action.equals("progress");
	}

	private boolean _checkValidRequestParameters(String module, String action) {
		return StringUtils.isBlank(module) || StringUtils.isBlank(action);
	}

	private AbstractHandler _tryGettingHandlerFromContextBean(Class<?> clazz) {
		try {
			AnnotationConfigApplicationContext context =
				new AnnotationConfigApplicationContext(AppConfig.class);

			return (AbstractHandler) context.getBean(clazz);
		} catch(Exception e) {
			_logger.warn("Handler bean not found: ", clazz.getName());
		}

		return null;
	}

	private void _handleGenericException(Exception e) throws ServletException, IOException {
		// ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, etc.
		this.doError("error.invalid_handler", e);

		_logger.error(e.getMessage(), e);
	}

	private void _handleActionExecutionException(InvocationTargetException e)
			throws ServletException, IOException {
		// Exception thrown in method.invoke
		Throwable handlerException = e.getTargetException();

		if (handlerException instanceof AuthorizationException) {
			this.doAuthorizationError();
		} else 	if (handlerException instanceof ValidationException) {
			this.doWarning(handlerException.getMessage(), handlerException);
		} else {
			this.doError("error.runtime_error", handlerException);
		}

		_logger.error(e.getMessage(), e);
	}

	protected abstract void doReturn() throws ServletException, IOException;
	protected abstract void doAuthorizationError() throws ServletException, IOException;
	protected abstract void doLockedStateError() throws ServletException, IOException;
	protected abstract void doError(String error, Throwable e) throws ServletException, IOException;
	protected abstract void doWarning(String warning, Throwable e) throws ServletException, IOException;

	protected void doError(String error) throws ServletException, IOException {
		this.doError(error, null);
	}
}
