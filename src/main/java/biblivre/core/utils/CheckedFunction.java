package biblivre.core.utils;

@FunctionalInterface
public interface CheckedFunction<T, U> {
	public U apply(T t) throws Exception;
}
