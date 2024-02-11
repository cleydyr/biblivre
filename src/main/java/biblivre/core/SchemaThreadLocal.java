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
