package biblivre.core.controllers;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;

/**
 * Servlet Filter implementation class RedirectToSchemaFilter
 */
@WebFilter("*")
public class RedirectToSchemaFilter implements Filter {

	public void destroy() {
	}

	public void doFilter(
			ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {

		ExtendedRequest xRequest = ((ExtendedRequest) request);

		if (xRequest.mustRedirectToSchema()) {
			String query = xRequest.getQueryString();

			if (StringUtils.isNotBlank(query)) {
				query = "?" + query;
			} else {
				query = "";
			}

			((ExtendedResponse) response).sendRedirect(xRequest.getRequestURI() + "/" + query);

			return;
		}

		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}
}
