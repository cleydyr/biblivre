package biblivre.administration.permissions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

public class PermissionDAOFactoryImpl implements PermissionDAOFactory {

	private Map<String, PermissionDAO> _instances = new ConcurrentHashMap<>();
	private DataSource _dataSource;

	public PermissionDAOFactoryImpl(DataSource dataSource) {
		_dataSource = dataSource;
	}

	@Override
	public PermissionDAO getInstance(String schema) {
		return _instances.computeIfAbsent(schema, key -> new PermissionDAOImpl(_dataSource, schema));
	}
}
