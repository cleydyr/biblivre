package biblivre.administration.reports.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import biblivre.administration.reports.ReportsBOFactory;
import biblivre.administration.reports.ReportsBoFactoryImpl;

@Configuration
@ComponentScan(basePackages = {"biblivre.administration.reports"})
public class ReportsConfiguration {

	@Bean
	public ReportsBOFactory reportsBOFactory() {
		return new ReportsBoFactoryImpl();
	}
}
