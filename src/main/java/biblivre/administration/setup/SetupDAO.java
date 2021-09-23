package biblivre.administration.setup;

public interface SetupDAO {

    void fixSequence(DataMigrationPhase migrationPhase);

    void deleteAll(DataMigrationPhase phase);
}
