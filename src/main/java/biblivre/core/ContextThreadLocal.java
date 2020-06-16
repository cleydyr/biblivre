package biblivre.core;

public class ContextThreadLocal {
	private static ThreadLocal<Context> THREADLOCAL;

	public static void init(Context ctx) {
		THREADLOCAL = ThreadLocal.withInitial(() -> ctx);
	}

	public static Context getContext() {
		return THREADLOCAL.get();
	}

	public static void cleanUp() {
		THREADLOCAL.remove();
	}
}
