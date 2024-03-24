package biblivre.core.function;

@FunctionalInterface
public interface UnsafeConsumer<T> {
    void accept(T arg) throws Exception;
}
