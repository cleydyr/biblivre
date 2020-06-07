package biblivre.core.utils;

@FunctionalInterface
public interface CheckedConsumer<T> {
	public void accept(T t) throws Exception;
}
