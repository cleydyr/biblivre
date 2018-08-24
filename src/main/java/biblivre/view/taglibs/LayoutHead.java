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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.utils.Constants;

public class LayoutHead extends TagSupport {
	private static final long serialVersionUID = 1L;

	private String schema;
	
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
	public int doStartTag() throws JspException {
		this.init();	
		
		String schema = this.getSchema();
		
		pageContext.getRequest().setAttribute("schema", schema);

		try {
			this.pageContext.include("/jsp/taglib/layout/head/start.jsp");
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			this.pageContext.include("/jsp/taglib/layout/head/end.jsp");
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Tag.EVAL_PAGE;
	}
}
