package biblivre.core.function;

@FunctionalInterface
public interface UnsafeSupplier<T> {
    T get() throws Exception;
}
