package biblivre.core;

import java.nio.charset.StandardCharsets;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

public class FreemarkerTemplateHelper {
	public static final Configuration freemarkerConfiguration;

	static {
		freemarkerConfiguration = new Configuration(Configuration.VERSION_2_3_28);

		// Set the preferred charset template files are stored in. UTF-8 is
		// a good choice in most applications:
		freemarkerConfiguration.setDefaultEncoding(StandardCharsets.UTF_8.name());

		// Sets how errors will appear.
		// During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
		freemarkerConfiguration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

		// Don't log exceptions inside FreeMarker that it will thrown at you anyway:
		freemarkerConfiguration.setLogTemplateExceptions(false);

		// Wrap unchecked exceptions thrown during template processing in
		freemarkerConfiguration.setWrapUncheckedExceptions(true);
	}
}
