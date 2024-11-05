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
package biblivre.core;

import biblivre.core.enums.ActionResult;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.utils.TextUtils;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;

@Setter
public final class Message {
    @Getter private ActionResult level;
    @Getter private String text;
    private String stackTrace;
    @Getter private List<Pair<String, String>> errorList;

    public Message(ActionResult level, String text) {
        super();
        this.setLevel(level);
        this.setText(text);
    }

    public Message(ActionResult level, String message, Throwable exception) {
        this(level, message);

        if (exception != null) {
            if (exception instanceof ValidationException) {
                this.setErrorList(((ValidationException) exception).getErrorList());
            } else {
                this.setStackTrace(ExceptionUtils.getStackTrace(exception));
            }
        }
    }

    public Message() {
        this(ActionResult.NORMAL, "");
    }

    public Message(Throwable exception) {
        this(ActionResult.WARNING, exception.getMessage(), exception);
    }

    public boolean isSuccess() {
        return ActionResult.NORMAL.equals(this.getLevel())
                || ActionResult.SUCCESS.equals(this.getLevel());
    }

    public String getStackTrace(boolean encode) {
        if (!encode || this.stackTrace == null) {
            return this.stackTrace;
        } else {
            System.out.println(this.stackTrace);
            return TextUtils.biblivreEncode(this.stackTrace);
        }
    }

    public void setText(ActionResult level, String text) {
        this.setLevel(level);
        this.setText(text);
    }
}
