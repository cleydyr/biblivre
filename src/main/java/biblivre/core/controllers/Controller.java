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

import biblivre.administration.setup.State;
import biblivre.core.AbstractHandler;
import biblivre.core.AbstractValidator;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.auth.AuthorizationBO;
import biblivre.core.auth.AuthorizationPoints;
import biblivre.core.configurations.Configurations;
import biblivre.core.exceptions.AuthorizationException;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.utils.Constants;
import biblivre.core.utils.TextUtils;
import biblivre.spring.SpringUtils;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.servlet.ServletException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.web.context.WebApplicationContext;

public abstract class Controller {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    protected ExtendedRequest xRequest;
    protected ExtendedResponse xResponse;
    protected AbstractHandler handler;
    protected boolean headerOnly;
    protected Class<? extends AbstractHandler> handlerClass;

    public Controller(ExtendedRequest xRequest, ExtendedResponse xResponse) {
        this.xRequest = xRequest;
        this.xResponse = xResponse;
        this.headerOnly = false;
    }

    protected void processRequest() throws ServletException, IOException {
        String module = null;
        String action = null;

        this.xRequest.setCharacterEncoding(Constants.DEFAULT_CHARSET.name());
        this.xResponse.setCharacterEncoding(Constants.DEFAULT_CHARSET.name());

        WebApplicationContext applicationContext = SpringUtils.getWebApplicationContext(xRequest);

        try {
            module =
                    this.xRequest.getString(
                            "module", (String) this.xRequest.getAttribute("module"));
            action =
                    this.xRequest.getString(
                            "action", (String) this.xRequest.getAttribute("action"));

            // In case of invalid pack and method, send user to index page
            if (StringUtils.isBlank(module) || StringUtils.isBlank(action)) {
                this.doError("error.void");
                return;
            }

            if (State.LOCKED.get() && !action.equals("progress")) {
                this.doLockedStateError();
                return;
            }

            boolean isSetup =
                    (module.equals("administration.setup")
                            || (module.equals("menu") && action.equals("setup")));

            if (isSetup
                    && (Configurations.getBoolean(Constants.CONFIG_NEW_LIBRARY)
                            || action.equals("progress"))) {
                // authorize
            } else {
                AuthorizationPoints authPoints =
                        (AuthorizationPoints)
                                this.xRequest.getScopedSessionAttribute("logged_user_atps");
                if (authPoints == null) {
                    authPoints = AuthorizationPoints.getNotLoggedInstance();
                }

                AuthorizationBO authorizationBO = applicationContext.getBean(AuthorizationBO.class);

                authorizationBO.authorize(authPoints, module, action);
            }
        } catch (AuthorizationException e) {
            // Exception thrown in abo.authorize
            this.doAuthorizationError();
            return;
        }

        try {
            String handlerClassName = "biblivre." + module + ".Handler";

            this.handlerClass = (Class<? extends AbstractHandler>) Class.forName(handlerClassName);

            try {
                Object bean = applicationContext.getBean(this.handlerClass);
                this.handler = (AbstractHandler) bean;
            } catch (NoSuchBeanDefinitionException nsbde) {
                this.handler =
                        (AbstractHandler) this.handlerClass.getDeclaredConstructor().newInstance();
            }

            String validatorClassName = "biblivre." + module + ".Validator";

            Class<?> validatorClass = Class.forName(validatorClassName);

            AbstractValidator validator = null;

            try {
                Object bean = applicationContext.getBean(validatorClass);

                validator = (AbstractValidator) bean;
            } catch (NoSuchBeanDefinitionException nsbde) {
                validator =
                        (AbstractValidator) validatorClass.getDeclaredConstructor().newInstance();
            }

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
            this.log.error(e.getMessage(), e);
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
            this.log.error(e.getMessage(), e);
            return;

        } catch (Exception e) {
            // ClassNotFoundException, NoSuchMethodException, InstantiationException,
            // IllegalAccessException, etc.
            this.doError("error.invalid_handler", e);
            this.log.error(e.getMessage(), e);
            return;
        }

        this.doReturn();
    }

    public boolean isHeaderOnly() {
        return this.headerOnly;
    }

    public void setHeaderOnly(boolean headerOnly) {
        this.headerOnly = headerOnly;
    }

    protected abstract void doReturn() throws ServletException, IOException;

    protected abstract void doAuthorizationError() throws ServletException, IOException;

    protected abstract void doLockedStateError() throws ServletException, IOException;

    protected abstract void doError(String error, Throwable e) throws ServletException, IOException;

    protected abstract void doWarning(String warning, Throwable e)
            throws ServletException, IOException;

    protected void doError(String error) throws ServletException, IOException {
        this.doError(error, null);
    }

    private Method _getMethodFromHandler(String action) throws NoSuchMethodException {
        Method method = null;

        Class<? extends AbstractHandler> lookupClass = this.handlerClass;

        while (method == null && !lookupClass.equals(AbstractHandler.class)) {
            try {
                method =
                        lookupClass.getDeclaredMethod(
                                TextUtils.camelCase(action),
                                ExtendedRequest.class,
                                ExtendedResponse.class);
            } catch (NoSuchMethodException e) {
                lookupClass = (Class<? extends AbstractHandler>) lookupClass.getSuperclass();
            }
        }

        if (method == null) {
            throw new NoSuchMethodException(action);
        }

        return method;
    }
}
