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

import biblivre.core.utils.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.tagext.Tag;
import jakarta.servlet.jsp.tagext.TagSupport;
import java.io.Serial;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LayoutHead extends TagSupport {
    @Serial private static final long serialVersionUID = 1L;

    private String schema;

    private static final Logger logger = LoggerFactory.getLogger(LayoutHead.class);

    public boolean isSchemaSelection() {
        return this.getSchema().equals(Constants.GLOBAL_SCHEMA);
    }

    private String getSchema() {
        return StringUtils.defaultString(this.schema);
    }

    private void init() {
        HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
        this.schema = (String) request.getAttribute("schema");
    }

    @Override
    public int doStartTag() {
        this.init();

        String schema = this.getSchema();

        pageContext.getRequest().setAttribute("schema", schema);

        try {
            this.pageContext.include("/WEB-INF/jsp/taglib/layout/head/start.jsp");
        } catch (Exception e) {
            logger.error("can't do start tag", e);
        }

        return EVAL_BODY_INCLUDE;
    }

    @Override
    public int doEndTag() {
        try {
            this.pageContext.include("/WEB-INF/jsp/taglib/layout/head/end.jsp");
        } catch (Exception e) {
            logger.error("can't do end tag", e);
        }

        return Tag.EVAL_PAGE;
    }
}
