package biblivre.core.function;

public interface UnsafeSupplier<T> {
    T get() throws Exception;
}
