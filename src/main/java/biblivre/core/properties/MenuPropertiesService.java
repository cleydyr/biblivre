package biblivre.core.properties;

import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@ConfigurationProperties(prefix = "biblivre.menus")
@Getter
@Setter
public class MenuPropertiesService {
    private Collection<String> disabled;

    public boolean isEnabled(String item) {
        if (disabled == null) {
            return true;
        }

        return !disabled.contains(item);
    }
}
