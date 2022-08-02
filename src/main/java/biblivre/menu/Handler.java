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
package biblivre.menu;

import biblivre.acquisition.request.RequestBO;
import biblivre.acquisition.request.RequestDTO;
import biblivre.acquisition.supplier.SupplierBO;
import biblivre.acquisition.supplier.SupplierDTO;
import biblivre.administration.backup.BackupBO;
import biblivre.administration.indexing.IndexingGroups;
import biblivre.administration.usertype.UserTypeBO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.bibliographic.BiblioRecordBO;
import biblivre.cataloging.enums.RecordType;
import biblivre.circulation.user.UserBO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractHandler;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.utils.DatabaseUtils;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("biblivre.menu.Handler")
public class Handler extends AbstractHandler {
    private UserBO userBO;
    private SupplierBO supplierBO;
    private RequestBO requestBO;
    private BiblioRecordBO biblioRecordBO;
    private BackupBO backupBO;
    private UserTypeBO userTypeBO;

    public void ping(ExtendedRequest request, ExtendedResponse response) {}

    public void i18n(ExtendedRequest request, ExtendedResponse response) throws IOException {
        if (request.getBoolean("from_translations")) {
            setJspURL("/jsp/administration/translations.jsp");
        } else {
            setJspURL("/jsp/index.jsp");
        }
    }

    public void listBibliographic(ExtendedRequest request, ExtendedResponse response) {

        String letter = request.getString("letter");
        Integer order =
                request.getInteger(
                        "order", IndexingGroups.getDefaultSortableGroupId(RecordType.BIBLIO));

        if (StringUtils.isBlank(letter)) {
            letter = "a";
        }

        List<RecordDTO> records = biblioRecordBO.listByLetter(letter.charAt(0), order);

        request.setAttribute("records", records);

        setJspURL("/jsp/list/bibliographic.jsp");
        return;
    }

    public void listAuthorities(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/jsp/list/authorities.jsp");
    }

    public void listVocabulary(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/jsp/list/vocabulary.jsp");
    }

    public void searchBibliographic(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/jsp/search/bibliographic.jsp");
    }

    public void searchAuthorities(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/jsp/search/authorities.jsp");
    }

    public void searchVocabulary(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/jsp/search/vocabulary.jsp");
    }

    public void catalogingBibliographic(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/jsp/cataloging/bibliographic.jsp");
    }

    public void catalogingAuthorities(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/jsp/cataloging/authorities.jsp");
    }

    public void catalogingVocabulary(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/jsp/cataloging/vocabulary.jsp");
    }

    public void catalogingLabels(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/jsp/cataloging/labels.jsp");
    }

    public void circulationUser(ExtendedRequest request, ExtendedResponse response) {
        request.setAttribute("userTypes", userTypeBO.list());
        setJspURL("/jsp/circulation/user.jsp");
    }

    public void circulationLending(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/jsp/circulation/lending.jsp");
    }

    public void circulationReservation(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/jsp/circulation/reservation.jsp");
    }

    public void circulationUserReservation(ExtendedRequest request, ExtendedResponse response) {
        Integer loggedUser = request.getLoggedUserId();
        if (loggedUser == null || loggedUser == 0) {
            setJspURL("/jsp/index.jsp");
        } else {
            UserDTO user = userBO.getUserByLoginId(loggedUser);
            if (user != null) {
                request.setAttribute("RESERVATION_USER_ID", user.getId());
            } else {
                request.setAttribute("RESERVATION_USER_ID", 0);
            }

            setJspURL("/jsp/circulation/user_reservation.jsp");
        }
        return;
    }

    public void circulationAccess(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/jsp/circulation/access_control.jsp");
    }

    public void circulationUserCards(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/jsp/circulation/user_cards.jsp");
    }

    public void acquisitionSupplier(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/jsp/acquisition/suppliers.jsp");
    }

    public void acquisitionRequest(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/jsp/acquisition/requests.jsp");
    }

    public void acquisitionQuotation(ExtendedRequest request, ExtendedResponse response) {
        List<SupplierDTO> suppliers = supplierBO.list();
        request.setAttribute("suppliers", suppliers);

        List<RequestDTO> requests = requestBO.list();
        request.setAttribute("requests", requests);

        setJspURL("/jsp/acquisition/quotations.jsp");
    }

    public void acquisitionOrder(ExtendedRequest request, ExtendedResponse response) {
        List<SupplierDTO> suppliers = supplierBO.list();
        request.setAttribute("suppliers", suppliers);

        setJspURL("/jsp/acquisition/orders.jsp");
    }

    public void administrationPassword(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/jsp/administration/password.jsp");
    }

    public void administrationMaintenance(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/jsp/administration/maintenance.jsp");
    }

    public void administrationPermissions(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/jsp/administration/permissions.jsp");
    }

    public void administrationConfigurations(ExtendedRequest request, ExtendedResponse response) {
        request.setAttribute("backupPath", backupBO.getBackupPath());

        File pgDump = DatabaseUtils.getPgDump(SchemaThreadLocal.get());

        String dumpAbsolutePath = (pgDump == null) ? null : pgDump.getAbsolutePath();

        request.setAttribute("dumpAbsolutePath", dumpAbsolutePath);

        setJspURL("/jsp/administration/configurations.jsp");
    }

    public void administrationReports(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/jsp/administration/reports.jsp");
    }

    public void administrationUserTypes(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/jsp/administration/user_types.jsp");
    }

    public void administrationAccessCards(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/jsp/administration/access_cards.jsp");
    }

    public void administrationDatamigration(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/jsp/administration/migration.jsp");
    }

    public void administrationTranslations(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/jsp/administration/translations.jsp");
    }

    public void administrationBriefCustomization(
            ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/jsp/administration/brief_customization.jsp");
    }

    public void administrationFormCustomization(
            ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/jsp/administration/form_customization.jsp");
    }

    public void multiSchemaTranslations(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/jsp/multi_schema/translations.jsp");
    }

    public void multiSchemaBackup(ExtendedRequest request, ExtendedResponse response) {
        String backupPath =
                SchemaThreadLocal.withGlobalSchema(
                        () -> {
                            return backupBO.getBackupPath();
                        });

        request.setAttribute("backupPath", backupPath);

        setJspURL("/jsp/multi_schema/backup.jsp");
    }

    public void multiSchemaConfigurations(ExtendedRequest request, ExtendedResponse response) {
        String backupPath =
                SchemaThreadLocal.withGlobalSchema(
                        () -> {
                            return backupBO.getBackupPath();
                        });

        request.setAttribute("backupPath", backupPath);

        setJspURL("/jsp/multi_schema/configurations.jsp");
    }

    public void multiSchemaManage(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/jsp/multi_schema/manage.jsp");
    }

    public void helpAboutBiblivre(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/jsp/help/about_biblivre.jsp");
    }

    public void setup(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/jsp/setup.jsp");
    }

    @Autowired
    public void setUserBO(UserBO userBO) {
        this.userBO = userBO;
    }

    @Autowired
    public void setSupplierBO(SupplierBO supplierBO) {
        this.supplierBO = supplierBO;
    }

    @Autowired
    public void setRequestBO(RequestBO requestBO) {
        this.requestBO = requestBO;
    }

    @Autowired
    public void setBiblioRecordBO(BiblioRecordBO biblioRecordBO) {
        this.biblioRecordBO = biblioRecordBO;
    }

    @Autowired
    public void setBackupBO(BackupBO backupBO) {
        this.backupBO = backupBO;
    }

    @Autowired
    public void setUserTypeBO(UserTypeBO userTypeBO) {
        this.userTypeBO = userTypeBO;
    }
}
