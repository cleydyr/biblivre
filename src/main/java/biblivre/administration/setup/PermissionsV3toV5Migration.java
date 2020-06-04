package biblivre.administration.setup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.compress.utils.Sets;

import biblivre.administration.permissions.PermissionDAO;

/**
 * @author Cleydyr Albuquerque
 */
public class PermissionsV3toV5Migration {
	private static final String MAPPING_FILE_NAME = "v3_to_v5_permission_mapping.properties";
	private static final File MAPPING_FILE = new File(
			PermissionsV3toV5Migration.class.getClassLoader()
				.getResource(MAPPING_FILE_NAME).getFile());

	private PermissionDAO permissionDAO;
	private DataMigrationDAO dataMigrationDAO;

	private static Map<String, Collection<String>> V3_TO_V5_PERMISSION_MAPPING =
			_loadV3ToV5PermissionMapping();

	public PermissionsV3toV5Migration(
		PermissionDAO permissionDAO, DataMigrationDAO dataMigrationDAO) {
		this.permissionDAO = permissionDAO;
		this.dataMigrationDAO = dataMigrationDAO;
	}

	public void migrate() throws SQLException {
		Map<Integer, Collection<String>> v3Permissions =
			dataMigrationDAO.listPermissions();

		importFromV3(v3Permissions);
	}

	public void importFromV3(Map<Integer, Collection<String>> v3Permissions) {
		v3Permissions.forEach((loginId, oldPermissions) -> {
			Set<String> newPermissions =
				_getV5PermissionSetFrom(oldPermissions);

			_saveIfNotPresent(loginId, newPermissions);
		});
	}

	private void _saveIfNotPresent(int loginId, Collection<String> newPermissions) {
		Set<String> existingPermissions = permissionDAO.getByLoginId(loginId);

		newPermissions.removeAll(existingPermissions);

		permissionDAO.save(loginId, newPermissions);
	}

	private static Set<String> _getV5PermissionSetFrom(
		Collection<String> oldPermissions) {

		return oldPermissions.stream()
			.flatMap(oldPermission ->
				V3_TO_V5_PERMISSION_MAPPING.get(oldPermission).stream()
			)
			.collect(Collectors.toSet());
	}

	private static Map<String, Collection<String>>
		_loadV3ToV5PermissionMapping() {

		Properties props = new Properties();

		try {
			Map<String, Collection<String>> result = new HashMap<>();

			props.load(new FileInputStream(MAPPING_FILE));

			props.forEach((key, value) -> {
				Set<String> values = Collections.unmodifiableSet(
					Sets.newHashSet(((String) value).split(",")));

				result.put((String) key, values);
			});

			return result;
		} catch (IOException e) {
			e.printStackTrace();

			return Collections.emptyMap();
		}
	}
}
