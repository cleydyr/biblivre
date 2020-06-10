package biblivre.core;

@FunctionalInterface
public interface CheckedBiConsumer<T, U> {
	public void accept(T t, U u) throws Exception;
}
