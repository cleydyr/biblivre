package biblivre.core.persistence.jdbc;

@SuppressWarnings("serial")
public class ParameterSetterNotFoundException extends RuntimeException {

	public ParameterSetterNotFoundException(Class<?> parameterClass) {
		super("Cannot find a PreparedStatement set* method for type " +
				parameterClass.getCanonicalName());
	}

}
