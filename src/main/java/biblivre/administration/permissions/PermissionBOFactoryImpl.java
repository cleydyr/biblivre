package biblivre.administration.permissions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PermissionBOFactoryImpl implements PermissionBOFactory {
	private Map<String, PermissionBO> _instances = new ConcurrentHashMap<>();

	private PermissionDAOFactory _daoFactory;

	public PermissionBOFactoryImpl(PermissionDAOFactory daoFactory) {
		_daoFactory = daoFactory;
	}

	@Override
	public PermissionBO getInstance(String schema) {
		return _instances.computeIfAbsent(
				schema,
				key -> {
					PermissionDAO dao = _daoFactory.getInstance(key);

					return new PermissionBOImpl(dao);
				});
	}

}
