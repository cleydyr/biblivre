package biblivre.web.filter;

import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.commons.lang3.StringUtils;

public class SchemaRedirectFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        ExtendedRequest xRequest = (ExtendedRequest) request;

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
}
