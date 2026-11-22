package biblivre.core;

import biblivre.core.function.UnsafeRunnable;
import biblivre.core.function.UnsafeSupplier;
import biblivre.core.utils.Constants;

public class SchemaThreadLocal {
    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void setSchema(String schema) {
        threadLocal.set(schema);
    }

    public static String get() {
        return threadLocal.get();
    }

    /**
     * Schema bound to this thread.
     *
     * <p>A missing schema is not replaced with the global or single schema. That would read or
     * write another library, and a null schema cannot be used as a {@code ConcurrentHashMap} key.
     *
     * @return the bound schema, never {@code null}
     * @throws IllegalStateException if no schema is bound to this thread
     */
    public static String requireSchema() {
        String schema = threadLocal.get();

        if (schema == null) {
            throw new IllegalStateException("No schema is bound to the current thread");
        }

        return schema;
    }

    public static String remove() {
        String value = threadLocal.get();

        threadLocal.remove();

        return value;
    }

    public static boolean isGlobalSchema() {
        return Constants.GLOBAL_SCHEMA.equals(get());
    }

    public static <T> T withGlobalSchema(UnsafeSupplier<T> supplier) {
        return withSchema(Constants.GLOBAL_SCHEMA, supplier);
    }

    public static <T> T withPublicSchema(UnsafeSupplier<T> supplier) {
        return withSchema("public", supplier);
    }

    public static <T> T withSchema(String schema, UnsafeSupplier<T> supplier) {
        String currentSchema = SchemaThreadLocal.remove();

        try {
            SchemaThreadLocal.setSchema(schema);

            return supplier.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            SchemaThreadLocal.remove();

            SchemaThreadLocal.setSchema(currentSchema);
        }
    }

    public static void withSchema(String schema, UnsafeRunnable runnable) {
        withSchema(
                schema,
                () -> {
                    runnable.run();

                    return null;
                });
    }

    public static void withGlobalSchema(UnsafeRunnable runnable) {
        withSchema(
                Constants.GLOBAL_SCHEMA,
                () -> {
                    runnable.run();

                    return null;
                });
    }
}
