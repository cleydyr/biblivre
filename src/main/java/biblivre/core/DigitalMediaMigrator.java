package biblivre.core;

import biblivre.digitalmedia.migrator.DigitalMediaStoreMigrator;
import biblivre.digitalmedia.migrator.DigitalMediaStoreMigratorService;
import biblivre.digitalmedia.migrator.exception.DigitalMediaStoreNotFoundException;

public class DigitalMediaMigrator {

	public static void processMigration()
		throws DigitalMediaStoreNotFoundException {

		String migratorName = System.getenv("DIGITAL_MEDIA_MIGRATOR");

		if (migratorName == null) {
			return;
		}

		DigitalMediaStoreMigrator migrator =
			DigitalMediaStoreMigratorService.selectMigrator(migratorName);

		migrator.migrate();
	}
}
