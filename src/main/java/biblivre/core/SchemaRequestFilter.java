package biblivre.core;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import biblivre.core.schemas.Schemas;
import biblivre.core.utils.Constants;

@Component
@Order(1)
public class SchemaRequestFilter implements Filter {
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;

		_loadSchemaAndController(req);

		chain.doFilter(request, response);
	}

	private void _loadSchemaAndController(HttpServletRequest request) {
		String url = request.getPathInfo();

		String schema = null;

		if (StringUtils.isNotBlank(url)) {
			String[] urlArray = url.split("/");

			schema = urlArray[0];

			if (schema.equals("DigitalMediaController")) {
				schema = null;
			}

			if (urlArray.length <= 1 && !url.endsWith("/")) {
				request.setAttribute("redirectToSchema", true);
			}
		}

		if (Schemas.isNotLoaded(schema)) {
			boolean isMultipleSchemasEnabled = Schemas.isMultipleSchemasEnabled();

			if (isMultipleSchemasEnabled) {
				schema = Constants.GLOBAL_SCHEMA;
			} else {
				schema = Constants.SINGLE_SCHEMA;
			}
		}

		request.setAttribute("schema", schema);
	}
}
