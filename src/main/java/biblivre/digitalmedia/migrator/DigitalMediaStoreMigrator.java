package biblivre.digitalmedia.migrator;

public interface DigitalMediaStoreMigrator {

    String from();

    String to();

    default String getId() {
        return getClass().getName();
    }

    void migrate();

    void init();
}
