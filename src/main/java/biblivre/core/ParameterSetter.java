package biblivre.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface ParameterSetter {
	void setFor(PreparedStatement preparedStatement, Object parameter, int position)
			throws SQLException;
}
