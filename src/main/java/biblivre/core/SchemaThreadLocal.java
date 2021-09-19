package biblivre.core;

public class SchemaThreadLocal {
	private static final ThreadLocal<String> threadLocal = new ThreadLocal<String>();

	public static void setSchema(String schema) {
		threadLocal.set(schema);
	}

	public static String get() {
		return threadLocal.get();
	}

	public static void remove() {
		threadLocal.remove();
	}
}
