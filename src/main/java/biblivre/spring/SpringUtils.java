package biblivre.spring;

import biblivre.core.ExtendedRequest;
import javax.servlet.ServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SpringUtils {

    public static WebApplicationContext getWebApplicationContext(ExtendedRequest extendedRequest) {
        ServletContext servletContext = extendedRequest.getServletContext();

        WebApplicationContext applicationContext =
                WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        return applicationContext;
    }
}
