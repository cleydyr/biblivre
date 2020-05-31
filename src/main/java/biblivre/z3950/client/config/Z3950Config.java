package biblivre.z3950.client.config;

import java.util.HashMap;
import java.util.Map;

import org.jzkit.search.provider.z3950.Z3950ServiceFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import biblivre.z3950.client.Z3950Client;
import biblivre.z3950.server.JZKitBackend;

@Import(Z3950AppContext.class)
@Configuration
public class Z3950Config {

	@Bean
	public ApplicationContext z3950XmlAppContext() {
		return new AnnotationConfigApplicationContext(Z3950AppContext.class);
	}

	@Bean
	public Z3950Client z3950Client(Z3950ServiceFactory factory, ApplicationContext context) {
		Z3950Client z3950Client = new Z3950Client();

		z3950Client.setFactory(factory);

		z3950Client.setZ3950Context(z3950XmlAppContext());

		return z3950Client;
	}

	@Bean
	public Z3950ServiceFactory z3950Factory() {
		Z3950ServiceFactory z3950ServiceFactory = new Z3950ServiceFactory();

		z3950ServiceFactory.setApplicationContext(z3950XmlAppContext());

		z3950ServiceFactory.setDefaultRecordSchema("usmarc");

		z3950ServiceFactory.setDefaultElementSetName("F");

		Map<String, String> recordArchetypes = new HashMap<>();

		recordArchetypes.put("default", "usmarc::F");

		recordArchetypes.put("FullDisplay", "usmarc::F");

		recordArchetypes.put("BriefDisplay", "usmarc::B");

		recordArchetypes.put("Holdings", "usmarc::F");

		z3950ServiceFactory.setRecordArchetypes(recordArchetypes);

		return z3950ServiceFactory;
	}

	@Bean
	public JZKitBackend backend() {
		return new JZKitBackend();
	}
}
