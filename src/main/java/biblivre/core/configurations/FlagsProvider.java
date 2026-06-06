package biblivre.core.configurations;

public interface FlagsProvider {
    boolean isFlagEnabled(String key);

    Object getFlagValue(String key);
}
