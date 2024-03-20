package biblivre;

import biblivre.acquisition.order.OrderDAO;
import biblivre.acquisition.order.OrderDAOImpl;
import biblivre.acquisition.quotation.QuotationDAO;
import biblivre.acquisition.quotation.QuotationDAOImpl;
import biblivre.acquisition.request.RequestDAO;
import biblivre.acquisition.request.RequestDAOImpl;
import biblivre.acquisition.supplier.SupplierDAO;
import biblivre.acquisition.supplier.SupplierDAOImpl;
import biblivre.administration.accesscards.AccessCardBO;
import biblivre.administration.accesscards.AccessCardDAO;
import biblivre.administration.accesscards.AccessCardDAOImpl;
import biblivre.administration.backup.BackupBO;
import biblivre.administration.backup.BackupDAO;
import biblivre.administration.backup.BackupDAOImpl;
import biblivre.administration.indexing.*;
import biblivre.administration.permissions.PermissionBO;
import biblivre.administration.permissions.PermissionDAO;
import biblivre.administration.permissions.PermissionDAOImpl;
import biblivre.administration.reports.ReportsBO;
import biblivre.administration.reports.ReportsDAO;
import biblivre.administration.reports.ReportsDAOImpl;
import biblivre.administration.usertype.UserTypeBO;
import biblivre.administration.usertype.UserTypeDAO;
import biblivre.administration.usertype.UserTypeDAOImpl;
import biblivre.cataloging.*;
import biblivre.cataloging.bibliographic.BiblioRecordBO;
import biblivre.cataloging.holding.HoldingBO;
import biblivre.cataloging.holding.HoldingDAO;
import biblivre.cataloging.holding.HoldingDAOImpl;
import biblivre.cataloging.labels.LabelGenerator;
import biblivre.cataloging.search.SearchDAO;
import biblivre.cataloging.search.SearchDAOImpl;
import biblivre.circulation.accesscontrol.AccessControlBO;
import biblivre.circulation.accesscontrol.AccessControlDAO;
import biblivre.circulation.accesscontrol.AccessControlDAOImpl;
import biblivre.circulation.lending.*;
import biblivre.circulation.reservation.ReservationBO;
import biblivre.circulation.reservation.ReservationDAO;
import biblivre.circulation.reservation.ReservationDAOImpl;
import biblivre.circulation.user.*;
import biblivre.core.Updates;
import biblivre.core.configurations.ConfigurationBO;
import biblivre.core.configurations.ConfigurationsDAO;
import biblivre.core.configurations.ConfigurationsDAOImpl;
import biblivre.core.controllers.*;
import biblivre.core.schemas.SchemaBO;
import biblivre.core.schemas.SchemaDAO;
import biblivre.core.schemas.SchemasDAOImpl;
import biblivre.core.translations.*;
import biblivre.digitalmedia.DigitalMediaBO;
import biblivre.digitalmedia.DigitalMediaDAO;
import biblivre.digitalmedia.DigitalMediaDAOFactory;
import biblivre.login.LoginBO;
import biblivre.login.LoginDAO;
import biblivre.login.LoginDAOImpl;
import biblivre.mail.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@SpringBootApplication(
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class,
        scanBasePackageClasses = {
            BiblioRecordBO.class,
            DigitalMediaBO.class,
            IndexingGroupBO.class,
            TranslationBO.class,
            ConfigurationBO.class,
            SchemaBO.class,
            HoldingBO.class,
            LabelGenerator.class,
            UserBO.class,
            UserTypeBO.class,
            LoginBO.class,
            PermissionBO.class,
            TabFieldsBO.class,
            LendingBO.class,
            ReservationBO.class,
            AccessControlBO.class,
            AccessCardBO.class,
            BackupBO.class,
            EmailSender.class,
            Updates.class,
            ReportsBO.class,
            biblivre.update.v6_0_0$1_0_0$alpha.Update.class,
            biblivre.update.v6_0_0$1_0_1$alpha.Update.class,
            biblivre.update.v6_0_0$1_0_2$alpha.Update.class,
            biblivre.update.v6_0_0$1_1_0$alpha.Update.class,
            biblivre.update.v6_0_0$2_0_0$alpha.Update.class,
            biblivre.update.v6_0_0$3_0_0$alpha.Update.class,
            biblivre.update.v6_0_0$3_0_1$alpha.Update.class,
            biblivre.update.v6_0_0$4_0_0$alpha.Update.class,
        })
public class TestBiblivreApplication {
    @Bean
    public AccessControlDAO accessControlDAO() {
        return AccessControlDAOImpl.getInstance();
    }

    @Bean
    public BackupDAO backupDAO() {
        return BackupDAOImpl.getInstance();
    }

    @Bean
    public DigitalMediaDAO digitalMediaDAO() {
        return DigitalMediaDAOFactory.getDigitalMediaDAOImpl();
    }

    @Bean
    public HoldingDAO holdingDAO() {
        return HoldingDAOImpl.getInstance();
    }

    @Bean
    public IndexingDAO indexingDAO() {
        return IndexingDAOImpl.getInstance();
    }

    @Bean
    public IndexingGroupsDAO indexingGroupsDAO() {
        return IndexingGroupsDAOImpl.getInstance();
    }

    @Bean
    public LendingDAO lendingDAO() {
        return LendingDAOImpl.getInstance();
    }

    @Bean
    public LendingFineDAO lendingFineDAO() {
        return LendingFineDAOImpl.getInstance();
    }

    @Bean
    public LoginDAO loginDAO() {
        return LoginDAOImpl.getInstance();
    }

    @Bean
    public OrderDAO orderDAO() {
        return OrderDAOImpl.getInstance();
    }

    @Bean
    public PermissionDAO permissionDAO() {
        return PermissionDAOImpl.getInstance();
    }

    @Bean
    public QuotationDAO quotationDAO() {
        return QuotationDAOImpl.getInstance();
    }

    @Bean
    public RecordDAO recordDAO() {
        return RecordDAOImpl.getInstance();
    }

    @Bean
    public ReportsDAO reportsDAO() {
        return ReportsDAOImpl.getInstance();
    }

    @Bean
    public RequestDAO requestDAO() {
        return RequestDAOImpl.getInstance();
    }

    @Bean
    public ReservationDAO reservationDAO() {
        return ReservationDAOImpl.getInstance();
    }

    @Bean
    public SearchDAO searchDAO() {
        return SearchDAOImpl.getInstance();
    }

    @Bean
    public SupplierDAO supplierDAO() {
        return SupplierDAOImpl.getInstance();
    }

    @Bean
    public UserDAO userDAO() {
        return UserDAOImpl.getInstance();
    }

    @Bean
    public UserTypeDAO userTypeDAO() {
        return UserTypeDAOImpl.getInstance();
    }

    @Bean
    public AccessCardDAO accessCardDAO() {
        return AccessCardDAOImpl.getInstance();
    }

    @Bean
    public UserFieldsDAO userFieldsDAO() {
        return UserFieldsDAOImpl.getInstance();
    }

    @Bean
    public TabFieldsDAO tabFieldsDAO() {
        return TabFieldsDAOImpl.getInstance();
    }

    @Bean
    public LanguageDAO languagesDAO() {
        return LanguageDAOImpl.getInstance();
    }

    @Bean
    public SchemaDAO schemasDAO() {
        return SchemasDAOImpl.getInstance();
    }

    @Bean
    public ConfigurationsDAO configurationsDAO() {
        return ConfigurationsDAOImpl.getInstance();
    }

    @Bean
    public TranslationsDAO translationsDAO() {
        return TranslationsDAOImpl.getInstance();
    }

    @Autowired private ApplicationContext applicationContext;

    @Autowired private AutowireCapableBeanFactory autowireCapableBeanFactory;
}
