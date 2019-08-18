package biblivre.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PreparedStatementUtil {
	private static final int FIRST_POSITION = 1;

	public static void setAllParameters(PreparedStatement preparedStatement, Object... parameters)
			throws SQLException {
		int position = FIRST_POSITION;

		for (Object parameter : parameters) {
			ParameterSetter parameterSetter = ParameterSetterFactory.getParameterSetter(
					parameter.getClass());

			parameterSetter.setFor(preparedStatement, parameter, position);

			position++;
		}
	}
}
