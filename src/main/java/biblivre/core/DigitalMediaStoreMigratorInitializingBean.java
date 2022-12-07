package biblivre.core;

import biblivre.digitalmedia.migrator.DigitalMediaStoreMigrator;
import biblivre.digitalmedia.migrator.exception.DigitalMediaStoreNotFoundException;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DigitalMediaStoreMigratorInitializingBean implements InitializingBean {
    private Map<String, DigitalMediaStoreMigrator> digitalMediaMigratorMap;

    @Override
    public void afterPropertiesSet() throws Exception {
        String migratorName = System.getenv("DIGITAL_MEDIA_MIGRATOR");

        if (StringUtils.isBlank(migratorName)) {
            return;
        }

        DigitalMediaStoreMigrator digitalMediaStoreMigrator =
                digitalMediaMigratorMap.get(migratorName);

        if (digitalMediaStoreMigrator == null) {
            throw new DigitalMediaStoreNotFoundException(
                    String.format("Can't find class corresponding to id: %s", migratorName));
        }

        digitalMediaStoreMigrator.init();

        digitalMediaStoreMigrator.migrate();
    }

    @Autowired
    public void setDigitalMediaStoreMigrator(
            Map<String, DigitalMediaStoreMigrator> digitalMediaMigratorMap) {
        this.digitalMediaMigratorMap = digitalMediaMigratorMap;
    }
}
