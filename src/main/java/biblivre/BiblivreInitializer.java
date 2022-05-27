/*******************************************************************************
 * Este arquivo é parte do Biblivre5.
 *
 * Biblivre5 é um software livre; você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da Licença Pública Geral GNU como
 * publicada pela Fundação do Software Livre (FSF); na versão 3 da
 * Licença, ou (caso queira) qualquer versão posterior.
 *
 * Este programa é distribuído na esperança de que possa ser  útil,
 * mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 * MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
 * Licença Pública Geral GNU para maiores detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 * com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
 *
 * @author Alberto Wagner <alberto@biblivre.org.br>
 * @author Danniel Willian <danniel@biblivre.org.br>
 ******************************************************************************/
package biblivre;

import biblivre.acquisition.order.OrderDAO;
import biblivre.acquisition.order.OrderDAOImpl;
import biblivre.acquisition.quotation.QuotationDAO;
import biblivre.acquisition.quotation.QuotationDAOImpl;
import biblivre.acquisition.request.RequestDAO;
import biblivre.acquisition.request.RequestDAOImpl;
import biblivre.acquisition.supplier.SupplierDAO;
import biblivre.acquisition.supplier.SupplierDAOImpl;
import biblivre.administration.accesscards.AccessCardDAO;
import biblivre.administration.accesscards.AccessCardDAOImpl;
import biblivre.administration.backup.BackupDAO;
import biblivre.administration.backup.BackupDAOImpl;
import biblivre.administration.indexing.IndexingDAO;
import biblivre.administration.indexing.IndexingDAOImpl;
import biblivre.administration.indexing.IndexingGroupsDAO;
import biblivre.administration.indexing.IndexingGroupsDAOImpl;
import biblivre.administration.permissions.PermissionDAO;
import biblivre.administration.permissions.PermissionDAOImpl;
import biblivre.administration.reports.ReportsDAO;
import biblivre.administration.reports.ReportsDAOImpl;
import biblivre.administration.setup.DataMigrationDAO;
import biblivre.administration.setup.DataMigrationDAOImpl;
import biblivre.administration.setup.SetupDAO;
import biblivre.administration.setup.SetupDAOImpl;
import biblivre.administration.usertype.UserTypeDAO;
import biblivre.administration.usertype.UserTypeDAOImpl;
import biblivre.cataloging.RecordDAO;
import biblivre.cataloging.RecordDAOImpl;
import biblivre.cataloging.holding.HoldingDAO;
import biblivre.cataloging.holding.HoldingDAOImpl;
import biblivre.cataloging.search.SearchDAO;
import biblivre.cataloging.search.SearchDAOImpl;
import biblivre.circulation.accesscontrol.AccessControlDAO;
import biblivre.circulation.accesscontrol.AccessControlDAOImpl;
import biblivre.circulation.lending.LendingDAO;
import biblivre.circulation.lending.LendingDAOImpl;
import biblivre.circulation.lending.LendingFineDAO;
import biblivre.circulation.lending.LendingFineDAOImpl;
import biblivre.circulation.reservation.ReservationDAO;
import biblivre.circulation.reservation.ReservationDAOImpl;
import biblivre.circulation.user.UserDAO;
import biblivre.circulation.user.UserDAOImpl;
import biblivre.core.DigitalMediaMigrator;
import biblivre.core.Updates;
import biblivre.core.auth.AuthorizationDAO;
import biblivre.core.auth.AuthorizationDAOImpl;
import biblivre.digitalmedia.DigitalMediaDAO;
import biblivre.digitalmedia.DigitalMediaDAOFactory;
import biblivre.login.LoginDAO;
import biblivre.login.LoginDAOImpl;
import biblivre.z3950.server.Z3950ServerBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BiblivreInitializer extends SpringBootServletInitializer {
    private static final Logger _logger = LoggerFactory.getLogger(BiblivreInitializer.class);

    private static boolean initialized = false;

    public static Z3950ServerBO Z3950server = null;

    public static synchronized void initialize() {
        if (!BiblivreInitializer.initialized) {
            try {
                Updates.fixPostgreSQL81();
                Updates.globalUpdate();

                DigitalMediaMigrator.processMigration();

                BiblivreInitializer.Z3950server = new Z3950ServerBO();
                BiblivreInitializer.Z3950server.startServer();

                BiblivreInitializer.initialized = true;
            } catch (Exception e) {
                _logger.error(e.getMessage(), e);
            }
        }
    }

    public static synchronized void destroy() {
        if (BiblivreInitializer.Z3950server != null) {
            BiblivreInitializer.Z3950server.stopServer();
        }
    }

    public static synchronized void reloadZ3950Server() {
        if (BiblivreInitializer.Z3950server != null) {
            BiblivreInitializer.Z3950server.reloadServer();
        }
    }

    @Bean
    public AccessCardDAO accessCardDAO() {
        return AccessCardDAOImpl.getInstance();
    }

    @Bean
    public AccessControlDAO accessControlDAO() {
        return AccessControlDAOImpl.getInstance();
    }

    @Bean
    public AuthorizationDAO authorizationDAO() {
        return AuthorizationDAOImpl.getInstance();
    }

    @Bean
    public BackupDAO backupDAO() {
        return BackupDAOImpl.getInstance();
    }

    @Bean
    public DataMigrationDAO dataMigrationDAO() {
        return DataMigrationDAOImpl.getInstance();
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
    public SetupDAO setupDAO() {
        return SetupDAOImpl.getInstance();
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
}
