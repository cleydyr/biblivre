package biblivre.core;

@SuppressWarnings("serial")
public class ParameterSetterNotFoundException extends Exception {

	public ParameterSetterNotFoundException(Class<?> parameterClass) {
		super("Cannot find a PreparedStatement set* method for type " +
				parameterClass.getCanonicalName());
	}

}
