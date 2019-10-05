package biblivre.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import biblivre.core.schemas.Schemas;
import biblivre.core.utils.Constants;

public class SchemaInterceptor extends HandlerInterceptorAdapter {
	@Override
	public boolean preHandle(
			HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		_loadSchemaAndController(request);

		return true;
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
