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
package biblivre.view.taglibs;

import biblivre.core.translations.TranslationsMap;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Translations extends SimpleTagSupport {

    private String key;
    private String param1;
    private String param2;
    private String param3;
    private boolean escapeHTML;

    private static final Logger logger = LoggerFactory.getLogger(Translations.class);

    @Override
    public void doTag() throws JspException {
        final JspWriter out = getJspContext().getOut();

        try {
            ServletRequest request = ((PageContext) getJspContext()).getRequest();
            TranslationsMap map = (TranslationsMap) request.getAttribute("translationsMap");

            String translation = map.getText(this.key);

            if (this.param1 != null) {
                translation = translation.replace("{0}", this.param1);
            }

            if (this.param2 != null) {
                translation = translation.replace("{1}", this.param2);
            }

            if (this.param3 != null) {
                translation = translation.replace("{2}", this.param3);
            }

            if (this.escapeHTML) {
                translation = StringEscapeUtils.escapeHtml4(translation);
            }

            out.print(translation);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new JspException(e.getMessage());
        }
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    public void setParam3(String param3) {
        this.param3 = param3;
    }

    public void setEscapeHTML(boolean escapeHTML) {
        this.escapeHTML = escapeHTML;
    }
}
