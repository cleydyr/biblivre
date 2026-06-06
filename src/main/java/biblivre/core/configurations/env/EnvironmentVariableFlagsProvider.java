package biblivre.core.configurations.env;

import biblivre.core.configurations.FlagsProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "FLAGS_PROVIDER_IMPL", havingValue = "env", matchIfMissing = true)
public class EnvironmentVariableFlagsProvider implements FlagsProvider {
    private static final String FLAG_ENV_PREFIX = "FLAG_";

    @Override
    public boolean isFlagEnabled(String key) {
        String value = getEnvValue(key);

        if (StringUtils.isBlank(value)) {
            return false;
        }

        return "true".equalsIgnoreCase(value) || "1".equals(value) || "yes".equalsIgnoreCase(value);
    }

    @Override
    public Object getFlagValue(String key) {
        String value = getEnvValue(key);

        return StringUtils.isBlank(value) ? null : value;
    }

    private static String getEnvValue(String key) {
        return System.getenv(toEnvVarName(key));
    }

    static String toEnvVarName(String key) {
        return FLAG_ENV_PREFIX + key.replace('-', '_').toUpperCase();
    }
}
