package biblivre.digitalmedia.migrator;

public interface DigitalMediaStoreMigrator {

	public String from();

	public String to();

	public default String getId() {
		return getClass().getName();
	}

	void migrate();
}
