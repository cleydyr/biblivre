package biblivre.digitalmedia.migrator;

import biblivre.digitalmedia.migrator.exception.DigitalMediaStoreNotFoundException;
import java.util.ServiceLoader;
import org.apache.commons.lang3.StringUtils;

public class DigitalMediaStoreMigratorService {
    public static DigitalMediaStoreMigrator selectMigrator(String migratorId)
            throws DigitalMediaStoreNotFoundException {

        if (StringUtils.isBlank(migratorId)) {
            throw new IllegalArgumentException("id can't be blank");
        }

        Iterable<DigitalMediaStoreMigrator> migrators = getMigrators();

        for (DigitalMediaStoreMigrator migrator : migrators) {
            if (migrator.getId().equals(migratorId)) {
                return migrator;
            }
        }

        throw new DigitalMediaStoreNotFoundException(
                String.format("Can't find class corresponding to id: %s", migratorId));
    }

    public static Iterable<DigitalMediaStoreMigrator> getMigrators() {
        return ServiceLoader.load(DigitalMediaStoreMigrator.class);
    }
}
