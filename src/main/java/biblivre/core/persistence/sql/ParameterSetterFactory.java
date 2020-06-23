package biblivre.core.persistence.sql;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;

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
		else if (parameterClass.isAssignableFrom(Boolean.class)) {
			return (PreparedStatement preparedStatement, Object parameter, int position) ->
				preparedStatement.setBoolean(position, (Boolean) parameter);
		}
		else if (parameterClass.isAssignableFrom(Date.class)) {
			return (PreparedStatement preparedStatement, Object parameter, int position) ->
				preparedStatement.setDate(position, new java.sql.Date(((Date) parameter).getTime()));
		}
		else if (parameterClass.isAssignableFrom(Float.class)) {
			return (PreparedStatement preparedStatement, Object parameter, int position) ->
				preparedStatement.setFloat(position, (Float) parameter);
		}
		else if (parameterClass.isAssignableFrom(NullableSQLObject.class)) {
			return (PreparedStatement preparedStatement, Object parameter, int position) ->
				preparedStatement.setNull(position, ((NullableSQLObject) parameter).getType());
		}
		else if (parameterClass.isAssignableFrom(Timestamp.class)) {
			return (PreparedStatement preparedStatement, Object parameter, int position) ->
				preparedStatement.setTimestamp(position, (Timestamp) parameter);
		}
		else {
			throw new ParameterSetterNotFoundException(parameterClass);
		}
	}

}
