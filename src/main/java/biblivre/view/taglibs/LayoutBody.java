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

import biblivre.core.SchemaThreadLocal;
import biblivre.core.translations.LanguageBO;
import biblivre.core.translations.TranslationsMap;
import biblivre.core.utils.Constants;
import biblivre.login.LoginDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.Tag;
import jakarta.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.io.Serial;
import lombok.Getter;
import lombok.Setter;

public class LayoutBody extends TagSupport {
    @Serial private static final long serialVersionUID = 1L;

    private TranslationsMap translationsMap;
    @Setter @Getter private boolean multiPart;
    @Setter @Getter private boolean banner;
    @Setter @Getter private boolean disableMenu;
    private LanguageBO languageBO;

    public boolean isSchemaSelection() {
        return Constants.GLOBAL_SCHEMA.equals(this.getSchema());
    }

    private boolean isLogged() {
        return (this.pageContext.getSession().getAttribute(this.getSchema() + ".logged_user")
                != null);
    }

    private boolean isEmployee() {
        LoginDTO dto =
                (LoginDTO)
                        this.pageContext
                                .getSession()
                                .getAttribute(this.getSchema() + ".logged_user");

        if (dto != null) {
            return dto.isEmployee();
        }

        return false;
    }

    private String getSchema() {
        return SchemaThreadLocal.get();
    }

    private void init() {
        HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();

        this.translationsMap = (TranslationsMap) request.getAttribute("translationsMap");

        this.languageBO = (LanguageBO) request.getAttribute("languageBO");
    }

    @Override
    public int doStartTag() throws JspException {
        this.init();

        doJSPForward();

        return EVAL_BODY_INCLUDE;
    }

    private void doJSPForward() throws JspException {
        ServletRequest request = pageContext.getRequest();

        request.setAttribute("schema", getSchema());
        request.setAttribute("translationsMap", translationsMap);
        request.setAttribute("isMultiPart", this.isMultiPart());
        request.setAttribute("isLogged", this.isLogged());
        request.setAttribute("isDisableMenu", this.isDisableMenu());
        request.setAttribute("isBanner", this.isBanner());
        request.setAttribute("isSchemaSelection", this.isSchemaSelection());
        request.setAttribute("isEmployee", this.isEmployee());
        request.setAttribute("languages", languageBO.getLanguages());

        String path = "/WEB-INF/jsp/taglib/layout/body/start.jsp";

        try {
            this.pageContext.include(path);
        } catch (ServletException | IOException e) {
            throw new JspException(e);
        }
    }

    @Override
    public int doEndTag() throws JspException {
        String path = "/WEB-INF/jsp/taglib/layout/body/end.jsp";
        try {
            this.pageContext.include(path);
        } catch (ServletException | IOException e) {
            throw new JspException(e);
        }
        return Tag.EVAL_PAGE;
    }
}
