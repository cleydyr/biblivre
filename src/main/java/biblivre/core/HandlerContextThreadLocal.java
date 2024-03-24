package biblivre.core;

public class HandlerContextThreadLocal {
    private static final ThreadLocal<HandlerContext> threadLocal =
            ThreadLocal.withInitial(biblivre.core.HandlerContext::new);

    public static void setHandlerContext(HandlerContext handlerContext) {
        threadLocal.set(handlerContext);
    }

    public static HandlerContext get() {
        return threadLocal.get();
    }

    public static void remove() {
        HandlerContext value = threadLocal.get();

        threadLocal.remove();
    }
}
