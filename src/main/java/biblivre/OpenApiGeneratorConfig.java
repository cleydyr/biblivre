package biblivre;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@Configuration
@ComponentScan(
        basePackages = {
            "org.openapitools",
            "biblivre.reports.generated.api",
            "org.openapitools.configuration"
        },
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
public class OpenApiGeneratorConfig {}
