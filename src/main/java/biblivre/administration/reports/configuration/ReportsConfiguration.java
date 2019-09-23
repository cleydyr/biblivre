package biblivre.administration.reports.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import biblivre.administration.reports.ReportsBOFactory;
import biblivre.administration.reports.ReportsBOFactoryImpl;
import biblivre.administration.reports.ReportsDAOFactory;
import biblivre.administration.reports.ReportsDAOFactoryImpl;

@Configuration
@ComponentScan(basePackages = {"biblivre.administration.reports"})
public class ReportsConfiguration {

	@Bean
	public ReportsBOFactory reportsBOFactory() {
		return new ReportsBOFactoryImpl(reportsDAOFactory());
	}

	@Bean
	public ReportsDAOFactory reportsDAOFactory() {
		return new ReportsDAOFactoryImpl();
	}
}
