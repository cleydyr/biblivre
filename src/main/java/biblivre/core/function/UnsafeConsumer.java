package biblivre.core.function;

@FunctionalInterface
public interface UnsafeConsumer<T> {
    public void accept(T arg) throws Exception;
}
