package biblivre.core.update;

import java.sql.Connection;
import java.util.Set;
import java.util.function.Consumer;

import biblivre.core.utils.Constants;

public abstract class UpdateBO {
	public boolean globalUpdate() {
		return _update(Constants.GLOBAL_SCHEMA, (schema) -> doGlobalUpdate());
	}

	public boolean schemaUpdate(String schema) {
		return _update(Constants.GLOBAL_SCHEMA, this::doSchemaUpdate);
	}

	protected void doGlobalUpdate() {
		
	}
	
	protected void doSchemaUpdate(String schema) {
		
	}
	
	protected abstract String getVersion();

	private boolean _update(String schema, Consumer<String> updater) {
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

				updater.accept(schema);

				dao.commitUpdate(version, con);
			}

			return true;
		}
		catch (Exception e) {
			dao.rollbackUpdate(con);

			e.printStackTrace();
		}

		return false;
	}
}
