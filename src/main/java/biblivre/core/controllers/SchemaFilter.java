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
import biblivre.core.configurations.FlagsProvider;
import biblivre.core.schemas.SchemaBO;
import biblivre.core.utils.Constants;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class SchemaFilter implements Filter {
    static final String RESOLVED_SCHEMA_ATTRIBUTE = "biblivre.resolvedSchema";

    private SchemaBO schemaBO;
    private ConfigurationBO configurationBO;
    private FlagsProvider flagsProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String schema = (String) httpRequest.getAttribute(RESOLVED_SCHEMA_ATTRIBUTE);
        if (schema == null) {
            schema = resolveSchema(httpRequest, response);
            if (schema == null) {
                if (isErrorDispatch(httpRequest)) {
                    // The original library is not loaded. Do not substitute global/single.
                    continueWithoutSchema(request, response, chain);
                }
                return;
            }
            httpRequest.setAttribute(RESOLVED_SCHEMA_ATTRIBUTE, schema);
        }

        bindSchema(httpRequest, request, response, chain, schema);
    }

    private String resolveSchema(HttpServletRequest request, ServletResponse response)
            throws IOException {
        HttpServletRequest schemaRequest = requestWithOriginalUri(request);
        String boundSchema = SchemaUtil.extractBoundSchema(schemaRequest);
        String pathSchema = SchemaUtil.extractPathSchema(schemaRequest);
        boolean rejected =
                StringUtils.isNotBlank(boundSchema)
                        && (schemaBO.isNotLoaded(boundSchema)
                                || SchemaUtil.isForbiddenPathForBoundSchema(
                                        boundSchema, pathSchema));

        if (rejected) {
            if (!isErrorDispatch(request)) {
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN);
                return null;
            }
            if (schemaBO.isNotLoaded(boundSchema)) {
                return null;
            }
            return boundSchema;
        }

        String schema =
                StringUtils.isNotBlank(boundSchema)
                        ? boundSchema
                        : SchemaUtil.extractSchema(schemaRequest);

        if (StringUtils.isBlank(schema) || schemaBO.isNotLoaded(schema)) {
            schema =
                    configurationBO.isMultipleSchemasEnabled()
                            ? Constants.GLOBAL_SCHEMA
                            : Constants.SINGLE_SCHEMA;
        }

        return schema;
    }

    private void bindSchema(
            HttpServletRequest httpRequest,
            ServletRequest request,
            ServletResponse response,
            FilterChain chain,
            String schema)
            throws IOException, ServletException {
        String boundSchema = SchemaUtil.extractBoundSchema(httpRequest);

        request.setAttribute(
                "schemas", SchemaUtil.visibleSchemas(boundSchema, schemaBO.getSchemas()));

        request.setAttribute("configurations", configurationBO);

        request.setAttribute("flagsProvider", flagsProvider);

        request.setAttribute(
                "spaHref", SchemaUtil.buildSpaSearchHref(httpRequest.getContextPath(), schema));

        String previousSchema = SchemaThreadLocal.get();
        SchemaThreadLocal.setSchema(schema);
        try {
            chain.doFilter(request, response);
        } finally {
            restoreSchema(previousSchema);
        }
    }

    private static void continueWithoutSchema(
            ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String previousSchema = SchemaThreadLocal.get();
        SchemaThreadLocal.remove();
        try {
            chain.doFilter(request, response);
        } finally {
            restoreSchema(previousSchema);
        }
    }

    private static void restoreSchema(String previousSchema) {
        if (previousSchema == null) {
            SchemaThreadLocal.remove();
        } else {
            SchemaThreadLocal.setSchema(previousSchema);
        }
    }

    private static boolean isErrorDispatch(HttpServletRequest request) {
        return request.getDispatcherType() == DispatcherType.ERROR
                || request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE) != null
                || request.getAttribute(RequestDispatcher.ERROR_EXCEPTION) != null;
    }

    private static HttpServletRequest requestWithOriginalUri(HttpServletRequest request) {
        Object errorRequestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        if (!(errorRequestUri instanceof String originalRequestUri)
                || StringUtils.isBlank(originalRequestUri)
                || originalRequestUri.equals(request.getRequestURI())) {
            return request;
        }

        return new HttpServletRequestWrapper(request) {
            @Override
            public String getRequestURI() {
                return originalRequestUri;
            }
        };
    }

    @Autowired
    public void setSchemaBO(SchemaBO schemaBO) {
        this.schemaBO = schemaBO;
    }

    @Autowired
    public void setConfigurationBO(ConfigurationBO configurationBO) {
        this.configurationBO = configurationBO;
    }

    @Autowired
    public void setFlagsProvider(FlagsProvider flagsProvider) {
        this.flagsProvider = flagsProvider;
    }
}
