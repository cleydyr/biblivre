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

import biblivre.administration.permissions.PermissionBO;
import biblivre.administration.setup.State;
import biblivre.core.AbstractHandler;
import biblivre.core.AbstractValidator;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.auth.AuthorizationPoints;
import biblivre.core.configurations.ConfigurationBO;
import biblivre.core.exceptions.AuthorizationException;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.utils.Constants;
import biblivre.core.utils.TextUtils;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class Controller {

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);
    protected ExtendedRequest xRequest;
    protected ExtendedResponse xResponse;
    protected AbstractHandler handler;
    @Getter @Setter protected boolean headerOnly;
    private PermissionBO permissionBO;

    private Map<String, AbstractHandler> handlersMap;

    private Map<String, AbstractValidator> validatorsMap;
    private ConfigurationBO configurationBO;

    public Controller() {
        this.headerOnly = false;
    }

    protected void processRequest() throws ServletException, IOException {
        String module;
        String action;

        try {
            module =
                    this.xRequest.getString(
                            "module", (String) this.xRequest.getAttribute("module"));
            action =
                    this.xRequest.getString(
                            "action", (String) this.xRequest.getAttribute("action"));

            // In case of invalid pack and method, send user to index page
            if (StringUtils.isBlank(module) || StringUtils.isBlank(action)) {
                this.doError();
                return;
            }

            if (State.LOCKED.get() && !action.equals("progress")) {
                this.doLockedStateError();
                return;
            }

            boolean isSetup =
                    (module.equals("administration.setup")
                            || (module.equals("menu") && action.equals("setup")));

            if (!isSetup
                    || (!configurationBO.getBoolean(Constants.CONFIG_NEW_LIBRARY)
                            && !action.equals("progress"))) {
                AuthorizationPoints authPoints =
                        (AuthorizationPoints)
                                this.xRequest.getScopedSessionAttribute("logged_user_atps");
                if (authPoints == null) {
                    authPoints = AuthorizationPoints.getNotLoggedInstance();
                }

                permissionBO.authorize(authPoints, module, action);
            }
        } catch (AuthorizationException e) {
            // Exception thrown in abo.authorize
            this.doAuthorizationError();
            return;
        }

        try {
            String handlerClassName = "biblivre." + module + ".Handler";

            this.handler = handlersMap.get(handlerClassName);

            String validatorClassName = "biblivre." + module + ".Validator";

            Class<?> validatorClass = Class.forName(validatorClassName);

            AbstractValidator validator;

            validator = validatorsMap.get(validatorClassName);

            String validationMethodName = "validate_" + action;
            Method validationMethod =
                    validatorClass.getDeclaredMethod(
                            TextUtils.camelCase(validationMethodName),
                            AbstractHandler.class,
                            ExtendedRequest.class,
                            ExtendedResponse.class);

            validationMethod.invoke(validator, this.handler, this.xRequest, this.xResponse);
            if (!validator.checkValidation(this.handler)) {
                this.doReturn();
                return;
            }
        } catch (AuthorizationException e) {
            this.doAuthorizationError();
        } catch (ClassNotFoundException cnfe) {
            // No Validator found, do nothing.
        } catch (NoSuchMethodException nsme) {
            // No Method found in the Validator, so do nothing.
        } catch (InvocationTargetException e) {
            // Exception thrown in method.invoke
            Throwable handlerException = e.getTargetException();
            if (handlerException instanceof AuthorizationException) {
                this.doAuthorizationError();
            } else {
                this.doError("error.runtime_error", handlerException);
            }
            logger.error(e.getMessage(), e);
            return;
        } catch (Exception e) {
            // ClassNotFoundException, NoSuchMethodException, InstantiationException,
            // IllegalAccessException, etc.
            this.doError("error.invalid_handler", e);
            return;
        }

        try {
            Method method = _getMethodFromHandler(action);

            method.invoke(this.handler, this.xRequest, this.xResponse);
        } catch (InvocationTargetException e) {
            // Exception thrown in method.invoke
            Throwable handlerException = e.getTargetException();

            if (handlerException instanceof AuthorizationException) {
                this.doAuthorizationError();
            } else if (handlerException instanceof ValidationException) {
                this.doWarning(handlerException.getMessage(), handlerException);
            } else {
                this.doError("error.runtime_error", handlerException);
            }
            logger.error(e.getMessage(), e);
            return;

        } catch (Exception e) {
            // ClassNotFoundException, NoSuchMethodException, InstantiationException,
            // IllegalAccessException, etc.
            this.doError("error.invalid_handler", e);
            logger.error(e.getMessage(), e);
            return;
        }

        this.doReturn();
    }

    protected abstract void doReturn() throws ServletException, IOException;

    protected abstract void doAuthorizationError() throws ServletException, IOException;

    protected abstract void doLockedStateError() throws ServletException, IOException;

    protected abstract void doError(String error, Throwable e) throws ServletException, IOException;

    protected abstract void doWarning(String warning, Throwable e)
            throws ServletException, IOException;

    protected void doError() throws ServletException, IOException {
        this.doError("error.void", null);
    }

    private Method _getMethodFromHandler(String action) throws NoSuchMethodException {
        Class<?> lookupClass = this.handler.getClass();

        return getMethod(action, lookupClass);
    }

    public static Method getMethod(String action, Class<?> lookupClass)
            throws NoSuchMethodException {
        Method method = null;

        while (method == null && !lookupClass.equals(AbstractHandler.class)) {
            try {
                method =
                        lookupClass.getDeclaredMethod(
                                TextUtils.camelCase(action),
                                ExtendedRequest.class,
                                ExtendedResponse.class);
            } catch (NoSuchMethodException e) {
                lookupClass = lookupClass.getSuperclass();
            }
        }

        if (method == null) {
            throw new NoSuchMethodException(action);
        }

        return method;
    }

    @Autowired
    public final void setPermissionBO(PermissionBO permissionBO) {
        this.permissionBO = permissionBO;
    }

    @Autowired
    public final void setHandlers(Map<String, AbstractHandler> handlersMap) {
        this.handlersMap = handlersMap;
    }

    @Autowired
    public final void setValidators(Map<String, AbstractValidator> validatorsMap) {
        this.validatorsMap = validatorsMap;
    }

    @Autowired
    public void setConfigurationBO(ConfigurationBO configurationBO) {
        this.configurationBO = configurationBO;
    }

    public void setRequest(ExtendedRequest xRequest) {
        this.xRequest = xRequest;
    }

    public void setResponse(ExtendedResponse xResponse) {
        this.xResponse = xResponse;
    }
}
