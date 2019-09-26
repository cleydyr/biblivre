package biblivre.administration.permissions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PermissionDAOFactoryImpl implements PermissionDAOFactory {

	private Map<String, PermissionDAO> _instances = new ConcurrentHashMap<>();

	@Override
	public PermissionDAO getInstance(String schema) {
		return _instances.computeIfAbsent(schema, PermissionDAOImpl::new);
	}
}
