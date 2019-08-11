package biblivre.update;

import java.sql.Connection;
import java.sql.SQLException;

import biblivre.core.UpdatesDAO;

public interface UpdateService {
	public String getVersion();

	public default void end(Connection connection, UpdatesDAO dao) throws SQLException {
		dao.commitUpdate(getVersion(), connection);
	}

	public default void doUpdate(Connection connection) {
		// Do nothing
	}
}
