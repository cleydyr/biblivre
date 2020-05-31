package biblivre.z3950.server;

import java.util.Set;
import java.util.TreeSet;

import org.jzkit.ServiceDirectory.CollectionDescriptionDBO;
import org.jzkit.ServiceDirectory.CollectionInstanceDBO;
import org.jzkit.configuration.provider.xml.XMLImpl;
import org.jzkit.z3950.server.Z3950Listener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import biblivre.core.schemas.SchemaDTO;
import biblivre.core.schemas.SchemasDAO;
import biblivre.core.utils.Constants;
import biblivre.z3950.client.config.Z3950Config;

public class Z3950ServerBO {
	private static ApplicationContext _context;
	private static Z3950LocalServer server;
	private static Set<String> loadedSchemas;
	private static String singleSchema = "single";

	public static void setSingleSchema(String schema) {
		singleSchema = schema;
	}

	public static String getSingleSchema() {
		return singleSchema;
	}

	public Boolean getServerStatus() {
		return Boolean.valueOf(server != null ? server.isActive() : false);
	}

	public boolean startServer() {
		if (server == null) {
			server = new Z3950LocalServer();

			Z3950Listener listener = _buildZ390Listener();

			server.setListener(listener);
		}

		if (!server.isActive()) {
			server.startServer();
		}

		return server.isActive();
	}

	public boolean stopServer() {
		if (server != null) {
			server.stopServer();
			server = null;
		}
		return false;
	}

	public boolean reloadServer() {
		stopServer();
		_loadCollections();
		return startServer();
	}

	public Z3950ServerBO() {
		_getContext();
		_loadCollections();
	}

	private ApplicationContext _getContext() {
		if (_context == null) {
			try {
				_context = new AnnotationConfigApplicationContext(Z3950Config.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return _context;
	}

	private void _loadCollections() {
		XMLImpl config = (XMLImpl) _getContext().getBean("JZKitConfig");

		if (loadedSchemas == null) {
			loadedSchemas = new TreeSet<>();
		}

		SchemasDAO schemasDao = SchemasDAO.getInstance(Constants.GLOBAL_SCHEMA);

		Set<SchemaDTO> schemas = schemasDao.list();

		for (SchemaDTO schema : schemas) {
			String name = schema.getSchema();
			if ((!loadedSchemas.contains(name)) && (!schema.isDisabled())) {
				loadedSchemas.add(name);

				CollectionDescriptionDBO desc = new CollectionDescriptionDBO();
				desc.setCode(name);
				desc.setCollectionName(name);

				config.registerCollectionDescription(desc);

				CollectionInstanceDBO instance = new CollectionInstanceDBO();
				instance.setCode(name);
				instance.setLocalId(name);
				instance.setProfile("bath");

				config.registerCollectionInstance(instance);
				try {
					config.addCollection(name, "default", instance);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Z3950Listener _buildZ390Listener() {
		Z3950Listener listener = new Z3950Listener();

		listener.setBackendBeanName("backend");
		listener.setDefault("default");
		listener.setPort(2200);
		listener.setApplicationContext(_getContext());
		return listener;
	}
}
