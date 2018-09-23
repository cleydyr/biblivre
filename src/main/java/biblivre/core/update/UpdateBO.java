package biblivre.core.update;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import java.util.function.BiConsumer;

import biblivre.core.utils.Constants;

public abstract class UpdateBO {
	public boolean globalUpdate() {
		return _update(Constants.GLOBAL_SCHEMA, (dao, schema) -> {
			try {
				doGlobalUpdate(dao);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}

	public boolean schemaUpdate(String schema) {
		return _update(Constants.GLOBAL_SCHEMA, (t, u) -> {
			try {
				doSchemaUpdate(t, u);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}

	protected void doGlobalUpdate(UpdatesDAO dao) throws SQLException {
		
	}
	
	protected void doSchemaUpdate(UpdatesDAO dao, String schema) throws SQLException {
		
	}
	
	protected abstract String getVersion();

	private boolean _update(String schema, BiConsumer<UpdatesDAO, String> updater) {
		System.out.println("Updating to version " + getVersion());

		UpdatesDAO dao = UpdatesDAO.getInstance(schema);
		Connection con = null;

		try {
			if (!dao.checkTableExistance("versions")) {
				dao.fixVersionsTable();
			}

			Set<String> installedVersions = dao.getInstalledVersions();
			String version = getVersion();

			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();

				updater.accept(dao, schema);

				dao.commitUpdate(version, con);
			}

			return true;
		}
		catch (SQLException e) {
			dao.rollbackUpdate(con);

			e.printStackTrace();
		}

		return false;
	}
}
