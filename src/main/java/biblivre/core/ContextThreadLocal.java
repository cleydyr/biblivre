package biblivre.core;

public class ContextThreadLocal {
	private static ThreadLocal<Context> THREADLOCAL;

	public static void init() {
		THREADLOCAL = new ThreadLocal<>();
	}

	public static Context getContext() {
		return THREADLOCAL.get();
	}

	public void cleanUp() {
		THREADLOCAL.remove();
	}
}
