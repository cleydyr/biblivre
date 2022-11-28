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

import biblivre.core.SchemaThreadLocal;
import biblivre.core.configurations.ConfigurationBO;
import biblivre.core.schemas.SchemaBO;
import biblivre.core.utils.Constants;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class SchemaFilter implements Filter {
    private SchemaBO schemaBO;
    private ConfigurationBO configurationBO;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String schema = extractSchema(request);

        SchemaThreadLocal.setSchema(schema);

        request.setAttribute("schemas", schemaBO.getSchemas());

        request.setAttribute("configurations", configurationBO);

        try {
            chain.doFilter(request, response);
        } finally {
            SchemaThreadLocal.remove();
        }
    }

    private String extractSchema(ServletRequest request) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        String requestURI = httpServletRequest.getRequestURI();

        String contextPath = httpServletRequest.getContextPath();

        String url = requestURI.substring(contextPath.length() + 1);

        String schema = null;

        if (StringUtils.isNotBlank(url)) {
            String[] urlArray = url.split("/");

            if (!"DigitalMediaController".equals(urlArray[0])) {
                schema = urlArray[0];
            }
        }

        if (schemaBO.isNotLoaded(schema)) {
            boolean isMultipleSchemasEnabled = configurationBO.isMultipleSchemasEnabled();

            if (isMultipleSchemasEnabled) {
                schema = Constants.GLOBAL_SCHEMA;
            } else {
                schema = Constants.SINGLE_SCHEMA;
            }
        }

        return schema;
    }

    @Autowired
    public void setSchemaBO(SchemaBO schemaBO) {
        this.schemaBO = schemaBO;
    }

    @Autowired
    public void setConfigurationBO(ConfigurationBO configurationBO) {
        this.configurationBO = configurationBO;
    }
}
