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
package biblivre;

import biblivre.core.controllers.*;
import biblivre.core.utils.StringPool;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.MultipartConfigElement;
import java.lang.reflect.Constructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

@SpringBootApplication(nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
@Import(OpenApiGeneratorConfig.class)
public class BiblivreInitializer extends SpringBootServletInitializer implements WebMvcConfigurer {
    @Autowired private ApplicationContext applicationContext;

    @Autowired private AutowireCapableBeanFactory autowireCapableBeanFactory;

    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();

        templateResolver.setApplicationContext(applicationContext);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".template");
        templateResolver.setTemplateMode(TemplateMode.TEXT);
        templateResolver.setCacheable(true);

        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();

        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.setEnableSpringELCompiler(true);

        return templateEngine;
    }

    @Bean
    public FilterRegistrationBean<SchemaFilter> schemaFilterRegistration() throws Exception {
        return createFilterRegistration(
                SchemaFilter.class,
                SecurityProperties.DEFAULT_FILTER_ORDER - 1,
                DispatcherType.REQUEST,
                DispatcherType.ERROR);
    }

    @Bean
    public FilterRegistrationBean<ExtendedRequestResponseFilter>
            extendedRequestResponseFilterRegistration() throws Exception {
        return createFilterRegistration(
                ExtendedRequestResponseFilter.class,
                2,
                DispatcherType.REQUEST,
                DispatcherType.FORWARD,
                DispatcherType.ERROR);
    }

    @Bean
    public FilterRegistrationBean<HandlerContextFilter> handlerContextFilterRegistration()
            throws Exception {
        return createFilterRegistration(
                HandlerContextFilter.class, 3, DispatcherType.REQUEST, DispatcherType.FORWARD);
    }

    @Bean
    public FilterRegistrationBean<SchemaRedirectFilter> schemaRedirectFilterRegistration()
            throws Exception {
        return createFilterRegistration(SchemaRedirectFilter.class, 4, DispatcherType.REQUEST);
    }

    @Bean
    public FilterRegistrationBean<StatusFilter> statusFilterRegistration() throws Exception {
        return createFilterRegistration(StatusFilter.class, 5, DispatcherType.REQUEST);
    }

    public <T extends Filter> FilterRegistrationBean<T> createFilterRegistration(
            Class<T> filterClass, int order, DispatcherType first, DispatcherType... rest)
            throws Exception {

        FilterRegistrationBean<T> registration = new FilterRegistrationBean<>();

        Constructor<T> constructor = filterClass.getConstructor();

        T filter = constructor.newInstance();

        autowireCapableBeanFactory.autowireBean(filter);

        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setOrder(order);
        registration.setDispatcherTypes(first, rest);

        return registration;
    }

    @Bean
    public ServletRegistrationBean<SchemaServlet> schemaServletRegistration() {
        SchemaServlet servlet = new SchemaServlet();

        autowireCapableBeanFactory.autowireBean(servlet);

        ServletRegistrationBean<SchemaServlet> servletRegistration =
                new ServletRegistrationBean<>(servlet, "/");

        servletRegistration.setLoadOnStartup(1);

        MultipartConfigElement multipartConfig = new MultipartConfigElement(StringPool.BLANK);

        servletRegistration.setMultipartConfig(multipartConfig);

        return servletRegistration;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS")
                .allowedHeaders("*");
    }

    public static void main(String[] args) {
        SpringApplication.run(BiblivreInitializer.class, args);
    }
}
