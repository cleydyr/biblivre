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

import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.RequestParserHelper;
import biblivre.core.translations.LanguageBO;
import biblivre.core.translations.TranslationBO;
import biblivre.core.utils.Constants;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;

public class ExtendedRequestResponseFilter implements Filter {
    private RequestParserHelper requestParserHelper;

    private LanguageBO languageBO;

    private TranslationBO translationBO;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        request.setCharacterEncoding(Constants.DEFAULT_CHARSET.name());
        response.setCharacterEncoding(Constants.DEFAULT_CHARSET.name());

        ExtendedRequest xRequest;
        ExtendedResponse xResponse;

        if (request instanceof ExtendedRequest) {
            // Avoid rewrapping if forwarding
            xRequest = (ExtendedRequest) request;
        } else {
            xRequest =
                    new ExtendedRequest(
                            (HttpServletRequest) request,
                            requestParserHelper,
                            languageBO,
                            translationBO);
        }

        if (response instanceof ExtendedResponse) {
            // Avoid rewrapping if forwarding
            xResponse = (ExtendedResponse) response;
        } else {
            xResponse = new ExtendedResponse((HttpServletResponse) response);
        }

        chain.doFilter(xRequest, xResponse);
    }

    @Autowired
    public void setRequestParserHelper(RequestParserHelper requestParserHelper) {
        this.requestParserHelper = requestParserHelper;
    }

    @Autowired
    public void setLanguageBO(LanguageBO languageBO) {
        this.languageBO = languageBO;
    }

    @Autowired
    public void setTranslationBO(TranslationBO translationBO) {
        this.translationBO = translationBO;
    }
}
