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

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Writer;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

public class StatusFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        String url =
                httpServletRequest
                        .getRequestURI()
                        .substring(httpServletRequest.getContextPath().length() + 1);

        String controller = null;

        if (StringUtils.isNotBlank(url)) {
            String[] urlArray = url.split("/");

            if (urlArray.length > 1) {
                controller = urlArray[1];
            }
        }

        if (controller == null) {
            controller = httpServletRequest.getParameter("controller");
        }

        if ("status".equals(controller)) {

            Writer out = response.getWriter();
            JSONObject json = new JSONObject();

            json.put("success", true);
            json.put("status_message", "Disponível");

            out.write(json.toString());

            return;
        }

        chain.doFilter(request, response);
    }
}
