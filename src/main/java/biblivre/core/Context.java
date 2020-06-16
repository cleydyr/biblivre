package biblivre.core;

public class Context {
	private final String schema;

	public Context(String schema) {
		this.schema = schema;
	}

	public String getSchema() {
		return schema;
	}
}
