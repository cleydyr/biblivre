package biblivre.core;

import java.sql.PreparedStatement;

public class ParameterSetterFactory {

	public static ParameterSetter getParameterSetter(Class<?> parameterClass)
			throws ParameterSetterNotFoundException {
		if (parameterClass.isAssignableFrom(String.class)) {
			return (PreparedStatement preparedStatement, Object parameter, int position) ->
				preparedStatement.setString(position, (String) parameter);
		}
		else if (parameterClass.isAssignableFrom(Long.class)) {
			return (PreparedStatement preparedStatement, Object parameter, int position) ->
				preparedStatement.setLong(position, (Long) parameter);
		}
		else if (parameterClass.isAssignableFrom(Integer.class)) {
			return (PreparedStatement preparedStatement, Object parameter, int position) ->
				preparedStatement.setInt(position, (Integer) parameter);
		}
		else {
			throw new ParameterSetterNotFoundException(parameterClass);
		}
	}

}
