package biblivre.core.function;

public interface UnsafeSupplier<T> {
    public T get() throws Exception;
}
