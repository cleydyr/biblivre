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
import jakarta.servlet.*;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;

public class SchemaFilter implements Filter {
    private SchemaBO schemaBO;
    private ConfigurationBO configurationBO;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String schema = SchemaUtil.extractSchema(request);

        if (schemaBO.isNotLoaded(schema)) {
            schema =
                    configurationBO.isMultipleSchemasEnabled()
                            ? Constants.GLOBAL_SCHEMA
                            : Constants.SINGLE_SCHEMA;
        }

        SchemaThreadLocal.setSchema(schema);

        request.setAttribute("schemas", schemaBO.getSchemas());

        request.setAttribute("configurations", configurationBO);

        try {
            chain.doFilter(request, response);
        } finally {
            SchemaThreadLocal.remove();
        }
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
