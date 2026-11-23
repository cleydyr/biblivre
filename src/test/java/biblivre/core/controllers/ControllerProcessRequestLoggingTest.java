package biblivre.core.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import biblivre.administration.permissions.PermissionBO;
import biblivre.administration.setup.State;
import biblivre.core.AbstractHandler;
import biblivre.core.AbstractValidator;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.RequestParserHelper;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.translations.LanguageBO;
import biblivre.core.translations.TranslationBO;
import biblivre.loggingfixture.Validator;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.read.ListAppender;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;

class ControllerProcessRequestLoggingTest {

    private static final String HANDLER_MODULE = "loggingprobe";
    private static final String VALIDATOR_MODULE = "loggingfixture";

    private final ListAppender<ILoggingEvent> appender = new ListAppender<>();
    private final Logger controllerLogger = (Logger) LoggerFactory.getLogger(Controller.class);
    private boolean previousLocked;

    @BeforeEach
    void attachLogAppender() {
        SchemaThreadLocal.remove();
        previousLocked = State.LOCKED.get();
        State.LOCKED.set(false);
        appender.list.clear();
        appender.start();
        controllerLogger.addAppender(appender);
    }

    @AfterEach
    void detachLogAppender() {
        controllerLogger.detachAppender(appender);
        State.LOCKED.set(previousLocked);
        SchemaThreadLocal.remove();
    }

    @Test
    void handlerFailureLogsTheTargetExceptionWithModuleAndAction() throws Exception {
        RecordingController controller =
                controllerFor(HANDLER_MODULE, "explode", new ExplodingHandler(), Map.of());

        controller.processRequest();

        ILoggingEvent event = onlyError();
        assertEquals("Handler loggingprobe.explode failed", event.getFormattedMessage());
        IThrowableProxy throwableProxy = event.getThrowableProxy();
        assertEquals(IllegalStateException.class.getName(), throwableProxy.getClassName());
        assertEquals("holding id missing", throwableProxy.getMessage());
        assertNull(throwableProxy.getCause());
        assertEquals("error.runtime_error", controller.errorKey);
        assertInstanceOf(IllegalStateException.class, controller.error);
        assertEquals("holding id missing", controller.error.getMessage());
    }

    @Test
    void handlerValidationFailureIsLoggedAsWarningWithoutStackTrace() throws Exception {
        RecordingController controller =
                controllerFor(HANDLER_MODULE, "reject", new ExplodingHandler(), Map.of());

        controller.processRequest();

        assertEquals(1, appender.list.size());
        ILoggingEvent event = appender.list.getFirst();
        assertEquals(Level.WARN, event.getLevel());
        assertEquals(
                "Handler loggingprobe.reject rejected: cataloging.lending.error.limit_exceeded",
                event.getFormattedMessage());
        assertNull(event.getThrowableProxy());
        assertEquals("cataloging.lending.error.limit_exceeded", controller.errorKey);
        assertInstanceOf(ValidationException.class, controller.error);
    }

    @Test
    void validatorFailureLogsTheTargetExceptionWithModuleAndAction() throws Exception {
        RecordingController controller =
                controllerFor(
                        VALIDATOR_MODULE,
                        "explode",
                        new ExplodingHandler(),
                        Map.of("biblivre.loggingfixture.Validator", new Validator()));

        controller.processRequest();

        ILoggingEvent event = onlyError();
        assertEquals("Validator loggingfixture.explode failed", event.getFormattedMessage());
        IThrowableProxy throwableProxy = event.getThrowableProxy();
        assertEquals(IllegalStateException.class.getName(), throwableProxy.getClassName());
        assertEquals("validator blew up", throwableProxy.getMessage());
        assertNull(throwableProxy.getCause());
        assertEquals("error.runtime_error", controller.errorKey);
        assertEquals("validator blew up", controller.error.getMessage());
    }

    @Test
    void missingHandlerMethodLogsModuleAndActionWithTheLookupException() throws Exception {
        RecordingController controller =
                controllerFor(HANDLER_MODULE, "missing", new AbstractHandler() {}, Map.of());

        controller.processRequest();

        ILoggingEvent event = onlyError();
        assertEquals("Invalid handler for loggingprobe.missing", event.getFormattedMessage());
        assertEquals(
                NoSuchMethodException.class.getName(), event.getThrowableProxy().getClassName());
        assertEquals("error.invalid_handler", controller.errorKey);
        assertInstanceOf(NoSuchMethodException.class, controller.error);
    }

    private ILoggingEvent onlyError() {
        assertEquals(1, appender.list.size());
        ILoggingEvent event = appender.list.getFirst();
        assertEquals(Level.ERROR, event.getLevel());
        return event;
    }

    private static RecordingController controllerFor(
            String module,
            String action,
            AbstractHandler handler,
            Map<String, AbstractValidator> validators)
            throws Exception {
        RecordingController controller = new RecordingController();
        controller.setRequest(request(module, action));
        controller.setPermissionBO(mock(PermissionBO.class));
        controller.setHandlers(Map.of("biblivre." + module + ".Handler", handler));
        controller.setValidators(validators);
        return controller;
    }

    private static ExtendedRequest request(String module, String action) throws Exception {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpRequest.setParameter("module", module);
        httpRequest.setParameter("action", action);
        return new ExtendedRequest(
                httpRequest,
                mock(RequestParserHelper.class),
                mock(LanguageBO.class),
                mock(TranslationBO.class));
    }

    private static final class RecordingController extends Controller {
        private String errorKey;
        private Throwable error;

        @Override
        protected void doReturn() {}

        @Override
        protected void doAuthorizationError() {}

        @Override
        protected void doLockedStateError() {}

        @Override
        protected void doError(String error, Throwable exception) {
            this.errorKey = error;
            this.error = exception;
        }

        @Override
        protected void doWarning(String warning, Throwable exception) {
            this.errorKey = warning;
            this.error = exception;
        }
    }

    public static final class ExplodingHandler extends AbstractHandler {
        public void explode(ExtendedRequest request, ExtendedResponse response) {
            throw new IllegalStateException("holding id missing");
        }

        public void reject(ExtendedRequest request, ExtendedResponse response) {
            throw new ValidationException("cataloging.lending.error.limit_exceeded");
        }
    }
}
