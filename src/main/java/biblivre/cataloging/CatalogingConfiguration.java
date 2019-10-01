package biblivre.cataloging;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import biblivre.core.HandlerBeanNameGenerator;

@Configuration
@ComponentScan(
		basePackages = "biblivre.cataloging",
		nameGenerator = HandlerBeanNameGenerator.class
)
public class CatalogingConfiguration {

}
