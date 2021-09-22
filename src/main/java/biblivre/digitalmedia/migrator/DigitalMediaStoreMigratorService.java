package biblivre.digitalmedia.migrator;

import biblivre.digitalmedia.migrator.exception.DigitalMediaStoreInstantiationException;
import biblivre.digitalmedia.migrator.exception.DigitalMediaStoreNotFoundException;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DigitalMediaStoreMigratorService {
    private static final Logger _logger =
            LoggerFactory.getLogger(DigitalMediaStoreMigratorService.class);

    public static DigitalMediaStoreMigrator selectMigrator(String migratorId)
            throws DigitalMediaStoreNotFoundException, DigitalMediaStoreInstantiationException {

        if (StringUtils.isBlank(migratorId)) {
            throw new IllegalArgumentException("id can't be blank");
        }

        try {
            Iterable<DigitalMediaStoreMigrator> migrators = getMigrators();

            for (DigitalMediaStoreMigrator migrator : migrators) {
                if (migrator.getId().equals(migratorId)) {
                    return migrator;
                }
            }
        } catch (ServiceConfigurationError sce) {
        	_logger.error("error while trying to find migrator with id " + migratorId);

            throw new DigitalMediaStoreInstantiationException(sce);
        }

        throw new DigitalMediaStoreNotFoundException(
                String.format("Can't find class corresponding to id: %s", migratorId));
    }

    public static Iterable<DigitalMediaStoreMigrator> getMigrators() {
        return ServiceLoader.load(DigitalMediaStoreMigrator.class);
    }
}
