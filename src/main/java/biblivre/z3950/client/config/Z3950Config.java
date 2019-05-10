package biblivre.z3950.client.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;

import br.org.biblivre.z3950server.JZKitBackend;
import javafx.application.Application;
import org.jzkit.a2j.codec.util.OIDRegister;
import org.jzkit.search.provider.z3950.Z3950ServiceFactory;
import org.jzkit.util.PropsHolder;
import org.jzkit.z3950.QueryModel.PropsBasedInternalToType1ConversionRules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;

import biblivre.z3950.client.Z3950Client;
import sun.security.provider.certpath.CollectionCertStore;

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

		HashMap<String, String> recordArchetypes = new HashMap<String, String>();

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
