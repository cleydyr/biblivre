package biblivre.core.configurations.flagsmith;

import biblivre.core.configurations.FlagsProvider;
import com.flagsmith.FlagsmithClient;
import com.flagsmith.exceptions.FlagsmithClientError;
import com.flagsmith.models.Flags;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "FLAGS_PROVIDER_IMPL", havingValue = "flagsmith")
public class FlagsmithFlagProvider implements FlagsProvider {
    private final FlagsmithClient flagsmith;

    public FlagsmithFlagProvider() throws FlagsmithClientError {
        this.flagsmith =
                FlagsmithClient.newBuilder()
                        .setApiKey(System.getenv("FLAGSMITH_SERVER_SIDE_ENVIRONMENT_KEY"))
                        .withApiUrl(System.getenv("FLAGSMITH_API_URL"))
                        .build();
    }

    @Override
    public boolean isFlagEnabled(String key) {
        try {
            Flags flags = flagsmith.getEnvironmentFlags();

            return flags.isFeatureEnabled(key);
        } catch (FlagsmithClientError e) {
            return false;
        }
    }

    @Override
    public Object getFlagValue(String key) {
        try {
            Flags flags = flagsmith.getEnvironmentFlags();

            return flags.getFeatureValue(key);
        } catch (FlagsmithClientError e) {
            return null;
        }
    }
}
