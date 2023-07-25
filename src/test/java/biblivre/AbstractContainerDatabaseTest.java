package biblivre;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.fail;

import biblivre.administration.indexing.IndexingDAOImpl;
import biblivre.administration.indexing.IndexingGroupBO;
import biblivre.administration.indexing.IndexingGroupsDAOImpl;
import biblivre.administration.permissions.PermissionBO;
import biblivre.administration.permissions.PermissionDAOImpl;
import biblivre.administration.usertype.UserTypeBO;
import biblivre.administration.usertype.UserTypeDAOImpl;
import biblivre.cataloging.ImportBO;
import biblivre.cataloging.RecordDAOImpl;
import biblivre.cataloging.TabFieldsBO;
import biblivre.cataloging.TabFieldsDAOImpl;
import biblivre.cataloging.bibliographic.BiblioRecordBO;
import biblivre.cataloging.dataimport.impl.ISO2709ImportProcessor;
import biblivre.cataloging.dataimport.impl.MarcFileImportProcessor;
import biblivre.cataloging.dataimport.impl.MarcXMLImportProcessor;
import biblivre.cataloging.holding.HoldingBO;
import biblivre.cataloging.holding.HoldingDAOImpl;
import biblivre.cataloging.search.SearchDAOImpl;
import biblivre.circulation.lending.LendingDAOImpl;
import biblivre.circulation.reservation.ReservationDAOImpl;
import biblivre.circulation.user.UserBO;
import biblivre.circulation.user.UserDAOImpl;
import biblivre.core.AbstractDAO;
import biblivre.core.configurations.ConfigurationBO;
import biblivre.core.schemas.SchemaBO;
import biblivre.core.schemas.SchemaDAO;
import biblivre.core.schemas.SchemasDAOImpl;
import biblivre.core.translations.TranslationBO;
import biblivre.core.translations.TranslationsDAOImpl;
import biblivre.core.utils.Constants;
import biblivre.digitalmedia.DigitalMediaBO;
import biblivre.digitalmedia.postgres.PostgresLargeObjectDigitalMediaDAO;
import biblivre.login.LoginBO;
import biblivre.login.LoginDAOImpl;
import com.github.stefanbirkner.systemlambda.Statement;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.jetbrains.annotations.NotNull;
import org.junit.ClassRule;
import org.springframework.core.io.ClassPathResource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

public abstract class AbstractContainerDatabaseTest {
    private static DataSource dataSource;

    @ClassRule
    protected final PostgreSQLContainer<?> postgreSQLContainer =
            SharedPostgreSQLContainer.getInstance();

    public AbstractContainerDatabaseTest() {
        postgreSQLContainer.start();
    }

    protected <T extends AbstractDAO> T getInstance(Class<T> clazz) {
        return AbstractDAO.getInstance(__ -> getDataSource(), clazz, "single");
    }

    protected DataSource getDataSource() {

        if (dataSource == null) {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(postgreSQLContainer.getJdbcUrl());
            hikariConfig.setUsername(postgreSQLContainer.getUsername());
            hikariConfig.setPassword(postgreSQLContainer.getPassword());
            hikariConfig.setDriverClassName(postgreSQLContainer.getDriverClassName());

            dataSource = new HikariDataSource(hikariConfig);
        }

        return dataSource;
    }

    protected void execute(Statement statement) {
        try {
            withEnvironmentVariable(
                            Constants.DATABASE_PORT,
                            String.valueOf(
                                    postgreSQLContainer.getMappedPort(
                                            Constants.DEFAULT_POSTGRESQL_PORT)))
                    .and(Constants.DATABASE_NAME, postgreSQLContainer.getDatabaseName())
                    .and(Constants.DATABASE_PASSWORD, postgreSQLContainer.getPassword())
                    .and(Constants.DATABASE_USERNAME, postgreSQLContainer.getUsername())
                    .execute(statement);
        } catch (Exception e) {
            fail(e);
        }
    }

    @NotNull
    protected SchemaBO getWiredSchemaBO() {
        SchemaBO schemaBO = new SchemaBO();

        schemaBO.setDatabaseTemplate(new ClassPathResource("/META-INF/sql/biblivre-template.sql"));

        SchemaDAO schemaDAO = getInstance(SchemasDAOImpl.class);

        schemaBO.setSchemaDAO(schemaDAO);

        ConfigurationBO configurationBO = new ConfigurationBO();

        schemaBO.setConfigurationBO(configurationBO);

        return schemaBO;
    }

    @NotNull
    protected ConfigurationBO getWiredConfigurationBO() {
        return new ConfigurationBO();
    }

    @NotNull
    protected ImportBO getWiredImportBO() {
        ImportBO importBO = new ImportBO();

        importBO.setImportProcessors(
                List.of(
                        new ISO2709ImportProcessor(),
                        new MarcXMLImportProcessor(),
                        new MarcFileImportProcessor()));

        importBO.setBiblioRecordBO(getWiredBiblioRecordBO());

        importBO.setPermissionBO(getWiredPermissionBO());
        return importBO;
    }

    @NotNull
    protected BiblioRecordBO getWiredBiblioRecordBO() {
        BiblioRecordBO biblioRecordBO = new BiblioRecordBO();

        biblioRecordBO.setRecordDAO(getInstance(RecordDAOImpl.class));

        biblioRecordBO.setConfigurationBO(getWiredConfigurationBO());

        biblioRecordBO.setDigitalMediaBO(getWiredDigitalMediaBO());

        biblioRecordBO.setindexingDAO(getInstance(IndexingDAOImpl.class));

        biblioRecordBO.setPermissionBO(getWiredPermissionBO());

        biblioRecordBO.setLendingDAO(getInstance(LendingDAOImpl.class));

        biblioRecordBO.setReservationDAO(getInstance(ReservationDAOImpl.class));

        biblioRecordBO.setHoldingBO(getWiredHoldingBO());

        biblioRecordBO.setIndexingGroupBO(getWiredIndexingGroupBO());

        biblioRecordBO.setTabFieldsBO(getWiredTabFieldsBO());

        biblioRecordBO.setSearchDAO(getInstance(SearchDAOImpl.class));

        return biblioRecordBO;
    }

    @NotNull
    protected TabFieldsBO getWiredTabFieldsBO() {
        TabFieldsBO tabFieldsBO = new TabFieldsBO();

        tabFieldsBO.setTabFieldsDAO(getInstance(TabFieldsDAOImpl.class));

        return tabFieldsBO;
    }

    @NotNull
    protected IndexingGroupBO getWiredIndexingGroupBO() {
        IndexingGroupBO indexingGroupBO = new IndexingGroupBO();

        indexingGroupBO.setIndexingGroupDAO(getInstance(IndexingGroupsDAOImpl.class));

        indexingGroupBO.setTranslationsBO(getWiredTranslationsBO());

        return indexingGroupBO;
    }

    @NotNull
    protected TranslationBO getWiredTranslationsBO() {
        TranslationBO translationsBO = new TranslationBO();

        translationsBO.setTranslationsDAO(getInstance(TranslationsDAOImpl.class));

        return translationsBO;
    }

    @NotNull
    protected HoldingBO getWiredHoldingBO() {
        HoldingBO holdingBO = new HoldingBO();

        holdingBO.setRecordDAO(getInstance(RecordDAOImpl.class));

        holdingBO.setHoldingDAO(getInstance(HoldingDAOImpl.class));

        holdingBO.setConfigurationBO(getWiredConfigurationBO());

        holdingBO.setPermissionBO(getWiredPermissionBO());

        holdingBO.setLoginBO(getWiredLoginBO());

        holdingBO.setUserBO(getWiredUserBO());

        holdingBO.setDigitalMediaBO(getWiredDigitalMediaBO());

        return holdingBO;
    }

    @NotNull
    protected UserBO getWiredUserBO() {
        UserBO userBO = new UserBO();

        userBO.setUserDAO(getInstance(UserDAOImpl.class));

        userBO.setUserTypeBO(getWiredUserTypeBO());

        return userBO;
    }

    @NotNull
    protected UserTypeBO getWiredUserTypeBO() {
        UserTypeBO userTypeBO = new UserTypeBO();

        userTypeBO.setUserTypeDAO(getInstance(UserTypeDAOImpl.class));

        userTypeBO.setPermissionBO(getWiredPermissionBO());

        userTypeBO.setUserDAO(getInstance(UserDAOImpl.class));

        return userTypeBO;
    }

    @NotNull
    protected LoginBO getWiredLoginBO() {
        LoginBO loginBO = new LoginBO();

        loginBO.setLoginDAO(getInstance(LoginDAOImpl.class));

        loginBO.setPermissionBO(getWiredPermissionBO());

        return loginBO;
    }

    @NotNull
    protected DigitalMediaBO getWiredDigitalMediaBO() {
        DigitalMediaBO digitalMediaBO = new DigitalMediaBO();

        digitalMediaBO.setDigitalMediaDAO(getInstance(PostgresLargeObjectDigitalMediaDAO.class));

        PermissionBO permissionBO = getWiredPermissionBO();

        digitalMediaBO.setPermissionBO(permissionBO);

        return digitalMediaBO;
    }

    @NotNull
    protected PermissionBO getWiredPermissionBO() {
        PermissionBO permissionBO = new PermissionBO();

        permissionBO.setPermissionDAO(getInstance(PermissionDAOImpl.class));

        return permissionBO;
    }
}
