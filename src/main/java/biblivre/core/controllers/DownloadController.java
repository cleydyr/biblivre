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

import biblivre.core.Dialog;
import biblivre.core.HttpCallback;
import biblivre.core.Message;
import biblivre.core.enums.ActionResult;
import biblivre.core.file.BiblivreFile;
import biblivre.core.utils.FileIOUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class DownloadController extends Controller {
    private static final Logger logger = LoggerFactory.getLogger(DownloadController.class);

    @Override
    protected void doReturn() throws IOException {
        BiblivreFile file = this.handler.getFile();
        Message message = this.handler.getMessage();
        int returnCode = this.handler.getReturnCode();

        if (StringUtils.isNotBlank(message.getText()) || returnCode != 0) {
            if (returnCode == 0) {
                this.xResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, message.getText());
            } else {
                this.xResponse.setStatus(returnCode);
            }

            return;
        }

        FileIOUtils.sendHttpFile(file, this.xRequest, this.xResponse, this.headerOnly);

        HttpCallback callback = this.handler.getCallback();

        if (callback != null) {
            callback.success();
        }
    }

    @Override
    protected void doAuthorizationError() throws ServletException, IOException {
        Message message = new Message(ActionResult.WARNING, "error.no_permission");

        this.dispatch("/WEB-INF/jsp/error.jsp", message);
    }

    @Override
    protected void doLockedStateError() throws ServletException, IOException {
        Message message = new Message(ActionResult.WARNING, "error.biblivre_is_locked_please_wait");

        this.dispatch("/WEB-INF/jsp/error.jsp", message);
    }

    @Override
    protected void doError(String error, Throwable e) throws ServletException, IOException {
        if (e != null && logger.isDebugEnabled()) {
            logger.error(error, e);
        } else {
            logger.error(error);
        }

        Message message = new Message(ActionResult.ERROR, error);

        this.dispatch("/WEB-INF/jsp/error.jsp", message);
    }

    @Override
    protected void doWarning(String warning, Throwable e) throws ServletException, IOException {
        if (e != null && logger.isDebugEnabled()) {
            logger.warn(warning, e);
        } else {
            logger.warn(warning);
        }

        Message message = new Message(ActionResult.WARNING, warning, e);
        this.dispatch("/WEB-INF/jsp/error.jsp", message);
    }

    private void dispatch(String jsp, Message message) throws ServletException, IOException {
        if (StringUtils.isNotBlank(message.getText())) {
            Dialog.show(this.xRequest, message.getText(), message.getLevel());
        }

        this.xResponse.setContentType("text/html;charset=UTF-8");
        this.xRequest.getRequestDispatcher(jsp).forward(this.xRequest, this.xResponse);
    }
}
