package biblivre;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.beans.BeansException;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jca.context.SpringContextResourceAdapter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import biblivre.administration.accesscards.AccessCardConfig;
import biblivre.circulation.accesscontrol.AccessControlConfig;
import biblivre.circulation.user.UserConfig;
import biblivre.core.controllers.SchemaServlet;

@EnableWebMvc
@Configuration
@Import({ AccessCardConfig.class, UserConfig.class, AccessControlConfig.class })
public class BiblivreApp implements ServletContextInitializer, ApplicationContextAware {
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		ServletRegistration.Dynamic serviceServlet =
			servletContext.addServlet("SchemaServlet", new SchemaServlet());

		serviceServlet.addMapping("/*");
		serviceServlet.setAsyncSupported(true);
		serviceServlet.setLoadOnStartup(2);
	}

	private static ApplicationContext context;

	public static <T extends Object> T getBean(Class<T> beanClass) {
		return context.getBean(beanClass);
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		BiblivreApp.context = context;
	}
}
