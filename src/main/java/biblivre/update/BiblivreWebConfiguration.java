package biblivre.update;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import biblivre.core.AuthorizationPointsInterceptor;
import biblivre.core.SchemaInterceptor;

@Configuration
@EnableWebMvc
public class BiblivreWebConfiguration implements WebMvcConfigurer {
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new AuthorizationPointsInterceptor());
		registry.addInterceptor(new SchemaInterceptor());
	}
}
