package biblivre.core.persistence.sql;

@SuppressWarnings("serial")
public class ParameterSetterNotFoundException extends RuntimeException {

	public ParameterSetterNotFoundException(Class<?> parameterClass) {
		super("Cannot find a PreparedStatement set* method for type " +
				parameterClass.getCanonicalName());
	}

}
