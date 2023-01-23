package biblivre.core.function;

public interface UnsafeFunction<T, U> {
    U apply(T argument) throws Exception;
}
