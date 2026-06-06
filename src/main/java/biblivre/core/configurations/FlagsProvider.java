package biblivre.core.configurations;

public interface FlagsProvider {
    public boolean isFlagEnabled(String key);

    public Object getFlagValue(String key);
}
