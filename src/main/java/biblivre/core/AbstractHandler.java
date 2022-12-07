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

import biblivre.core.HandlerContextThreadLocal.HandlerContext;
import biblivre.core.configurations.ConfigurationBO;
import biblivre.core.enums.ActionResult;
import biblivre.core.file.BiblivreFile;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public class AbstractHandler {
    protected ConfigurationBO configurationBO;

    public AbstractHandler() {}

    protected HandlerContext getHandlerContext() {
        return HandlerContextThreadLocal.get();
    }

    public void setJson(JSONObject json) {
        getHandlerContext().setJson(json);
    }

    public JSONObject getJson() {
        return getHandlerContext().getJson();
    }

    public void setJspURL(String jspURL) {
        getHandlerContext().setJspURL(jspURL);
    }

    public String getJspURL() {
        return getHandlerContext().getJspURL();
    }

    public void setMessage(Message message) {
        getHandlerContext().setMessage(message);
    }

    public void setMessage(ActionResult level) {
        setMessage(level, "");
    }

    public void setMessage(ActionResult level, String message) {
        getHandlerContext().setMessage(new Message(level, message));
    }

    public void setMessage(Throwable exception) {
        setMessage(new Message(exception));
    }

    public Message getMessage() {
        return getHandlerContext().getMessage();
    }

    public boolean hasErrors() {
        Message message = this.getMessage();

        boolean hasErrorMessage = StringUtils.isNotBlank(message.getText());

        if (!hasErrorMessage) {
            return false;
        }

        ActionResult level = message.getLevel();
        switch (level) {
            case WARNING:
            case ERROR:
                return true;
            default:
                return false;
        }
    }

    public BiblivreFile getFile() {
        return getHandlerContext().getFile();
    }

    public void setFile(BiblivreFile file) {
        getHandlerContext().setFile(file);
    }

    public int getReturnCode() {
        return getHandlerContext().getReturnCode();
    }

    public void setReturnCode(int returnCode) {
        getHandlerContext().setReturnCode(returnCode);
    }

    public HttpCallback getCallback() {
        return getHandlerContext().getCallback();
    }

    public void setCallback(HttpCallback callback) {
        getHandlerContext().setCallback(callback);
    }

    public void put(String key, Object object) {
        JSONObject json = getJson();

        json.put(key, object);
    }

    public void append(String key, Object object) {
        JSONObject json = getJson();

        json.append(key, object);
    }

    public void putOpt(String key, Object object) {
        JSONObject json = getJson();

        json.putOpt(key, object);
    }

    public void accumulate(String key, Object object) {
        JSONObject json = getJson();

        json.accumulate(key, object);
    }

    @Autowired
    public void setConfigurationBO(ConfigurationBO configurationBO) {
        this.configurationBO = configurationBO;
    }
}
