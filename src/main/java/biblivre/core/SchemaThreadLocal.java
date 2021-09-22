package biblivre.core;

import biblivre.core.utils.Constants;
import java.util.function.Supplier;

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

    public static <T> T withGlobalSchema(Supplier<T> supplier) {
        return withSchema(Constants.GLOBAL_SCHEMA, supplier);
    }

    public static <T> T withPublicSchema(Supplier<T> supplier) {
        return withSchema("public", supplier);
    }

    public static <T> T withSchema(String schema, Supplier<T> supplier) {
        String currentSchema = SchemaThreadLocal.remove();

        SchemaThreadLocal.setSchema(schema);

        T value = supplier.get();

        SchemaThreadLocal.remove();

        SchemaThreadLocal.setSchema(currentSchema);

        return value;
    }
}
