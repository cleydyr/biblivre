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
import biblivre.administration.indexing.IndexingGroupBO;
import biblivre.administration.usertype.UserTypeBO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.TabFieldsBO;
import biblivre.cataloging.bibliographic.BiblioRecordBO;
import biblivre.cataloging.enums.RecordType;
import biblivre.circulation.user.UserBO;
import biblivre.circulation.user.UserDTO;
import biblivre.circulation.user.UserFieldBO;
import biblivre.core.AbstractHandler;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.properties.MenuPropertiesService;
import biblivre.core.schemas.SchemaBO;
import biblivre.core.translations.LanguageBO;
import biblivre.core.translations.LanguageDTO;
import biblivre.core.utils.Constants;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Handler extends AbstractHandler {
    private UserBO userBO;
    private SupplierBO supplierBO;
    private RequestBO requestBO;
    private BiblioRecordBO biblioRecordBO;
    private BackupBO backupBO;
    private UserTypeBO userTypeBO;
    private UserFieldBO userFieldBO;
    private IndexingGroupBO indexingGroupBO;
    private TabFieldsBO tabFieldsBO;
    private LanguageBO languageBO;
    private SchemaBO schemaBO;

    @Autowired private MenuPropertiesService menuPropertiesService;

    public void i18n(ExtendedRequest request, ExtendedResponse response) {
        if (request.getBoolean("from_translations")) {
            setJspURL("/WEB-INF/jsp/administration/translations.jsp");
        } else {
            setJspURL("/WEB-INF/jsp/index.jsp");
        }
    }

    public void listBibliographic(ExtendedRequest request, ExtendedResponse response) {

        String letter = request.getString("letter");
        Integer order =
                request.getInteger(
                        "order", indexingGroupBO.getDefaultSortableGroupId(RecordType.BIBLIO));

        if (StringUtils.isBlank(letter)) {
            letter = "a";
        }

        List<RecordDTO> records = biblioRecordBO.listByLetter(letter.charAt(0), order);

        request.setAttribute("records", records);

        setJspURL("/WEB-INF/jsp/list/bibliographic.jsp");
    }

    public void listAuthorities(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/WEB-INF/jsp/list/authorities.jsp");
    }

    public void listVocabulary(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/WEB-INF/jsp/list/vocabulary.jsp");
    }

    public void searchBibliographic(ExtendedRequest request, ExtendedResponse response) {
        String language = (String) request.getAttribute("language");

        request.setAttribute(
                "bibliographicSearchableGroupsText",
                indexingGroupBO.getSearchableGroupsText(RecordType.BIBLIO, language));

        request.setAttribute(
                "biblioCacheFileName",
                tabFieldsBO.getFormFields(RecordType.BIBLIO).getCacheFileName());

        request.setAttribute(
                "holdingCacheFileName",
                tabFieldsBO.getFormFields(RecordType.HOLDING).getCacheFileName());

        request.setAttribute("biblioIndexingGroups", indexingGroupBO.getGroups(RecordType.BIBLIO));

        setJspURL("/WEB-INF/jsp/search/bibliographic.jsp");
    }

    public void searchAuthorities(ExtendedRequest request, ExtendedResponse response) {
        String language = (String) request.getAttribute("language");

        request.setAttribute(
                "authoritiesCacheFileName",
                tabFieldsBO.getFormFields(RecordType.AUTHORITIES).getCacheFileName());

        request.setAttribute(
                "authoritiesSearchableGroupsText",
                indexingGroupBO.getSearchableGroupsText(RecordType.AUTHORITIES, language));

        setJspURL("/WEB-INF/jsp/search/authorities.jsp");
    }

    public void searchVocabulary(ExtendedRequest request, ExtendedResponse response) {
        String language = (String) request.getAttribute("language");

        request.setAttribute(
                "vocabulariesCacheFileName",
                tabFieldsBO.getFormFields(RecordType.VOCABULARY).getCacheFileName());

        request.setAttribute(
                "vocabulariesSearchableGroupsText",
                indexingGroupBO.getSearchableGroupsText(RecordType.VOCABULARY, language));

        setJspURL("/WEB-INF/jsp/search/vocabulary.jsp");
    }

    public void catalogingBibliographic(ExtendedRequest request, ExtendedResponse response) {
        request.setAttribute(
                "biblioCacheFileName",
                tabFieldsBO.getFormFields(RecordType.BIBLIO).getCacheFileName());
        request.setAttribute(
                "holdingCacheFileName",
                tabFieldsBO.getFormFields(RecordType.HOLDING).getCacheFileName());
        request.setAttribute("biblioIndexingGroups", indexingGroupBO.getGroups(RecordType.BIBLIO));

        setJspURL("/WEB-INF/jsp/cataloging/bibliographic.jsp");
    }

    public void catalogingAuthorities(ExtendedRequest request, ExtendedResponse response) {
        request.setAttribute(
                "authoritiesCacheFileName",
                tabFieldsBO.getFormFields(RecordType.AUTHORITIES).getCacheFileName());

        setJspURL("/WEB-INF/jsp/cataloging/authorities.jsp");
    }

    public void catalogingVocabulary(ExtendedRequest request, ExtendedResponse response) {
        request.setAttribute(
                "vocabulariesCacheFileName",
                tabFieldsBO.getFormFields(RecordType.VOCABULARY).getCacheFileName());

        setJspURL("/WEB-INF/jsp/cataloging/vocabulary.jsp");
    }

    public void catalogingLabels(ExtendedRequest request, ExtendedResponse response) {
        request.setAttribute("biblioIndexingGroups", indexingGroupBO.getGroups(RecordType.BIBLIO));

        setJspURL("/WEB-INF/jsp/cataloging/labels.jsp");
    }

    public void circulationUser(ExtendedRequest request, ExtendedResponse response) {
        request.setAttribute("userTypes", userTypeBO.list());

        request.setAttribute("searchableFields", userFieldBO.getSearchableFields());

        request.setAttribute("cacheFileName", userFieldBO.getFields().getCacheFileName());

        setJspURL("/WEB-INF/jsp/circulation/user.jsp");
    }

    public void circulationLending(ExtendedRequest request, ExtendedResponse response) {
        request.setAttribute("searchableFields", userFieldBO.getSearchableFields());

        request.setAttribute(
                "currencySymbol", configurationBO.getString(Constants.CONFIG_CURRENCY));

        setJspURL("/WEB-INF/jsp/circulation/lending.jsp");
    }

    public void circulationReservation(ExtendedRequest request, ExtendedResponse response) {
        request.setAttribute("searchableFields", userFieldBO.getSearchableFields());

        setJspURL("/WEB-INF/jsp/circulation/reservation.jsp");
    }

    public void circulationUserReservation(ExtendedRequest request, ExtendedResponse response) {
        int loggedUser = request.getLoggedUserId();

        if (loggedUser == 0) {
            setJspURL("/WEB-INF/jsp/index.jsp");
        } else {
            UserDTO user = userBO.getUserByLoginId(loggedUser);
            if (user != null) {
                request.setAttribute("RESERVATION_USER_ID", user.getId());
            } else {
                request.setAttribute("RESERVATION_USER_ID", 0);
            }

            request.setAttribute("searchableFields", userFieldBO.getSearchableFields());

            setJspURL("/WEB-INF/jsp/circulation/user_reservation.jsp");
        }
    }

    public void circulationAccess(ExtendedRequest request, ExtendedResponse response) {
        request.setAttribute("searchableFields", userFieldBO.getSearchableFields());

        setJspURL("/WEB-INF/jsp/circulation/access_control.jsp");
    }

    public void circulationUserCards(ExtendedRequest request, ExtendedResponse response) {
        request.setAttribute("searchableFields", userFieldBO.getSearchableFields());

        request.setAttribute("cacheFileName", userFieldBO.getFields().getCacheFileName());

        setJspURL("/WEB-INF/jsp/circulation/user_cards.jsp");
    }

    public void acquisitionSupplier(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/WEB-INF/jsp/acquisition/suppliers.jsp");
    }

    public void acquisitionRequest(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/WEB-INF/jsp/acquisition/requests.jsp");
    }

    public void acquisitionQuotation(ExtendedRequest request, ExtendedResponse response) {
        Collection<SupplierDTO> suppliers = supplierBO.list();
        request.setAttribute("suppliers", suppliers);

        Collection<RequestDTO> requests = requestBO.list();
        request.setAttribute("requests", requests);

        setJspURL("/WEB-INF/jsp/acquisition/quotations.jsp");
    }

    public void acquisitionOrder(ExtendedRequest request, ExtendedResponse response) {
        Collection<SupplierDTO> suppliers = supplierBO.list();
        request.setAttribute("suppliers", suppliers);

        setJspURL("/WEB-INF/jsp/acquisition/orders.jsp");
    }

    public void administrationPassword(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/WEB-INF/jsp/administration/password.jsp");
    }

    public void administrationMaintenance(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/WEB-INF/jsp/administration/maintenance.jsp");
    }

    public void administrationPermissions(ExtendedRequest request, ExtendedResponse response) {
        request.setAttribute("searchableFields", userFieldBO.getSearchableFields());

        setJspURL("/WEB-INF/jsp/administration/permissions.jsp");
    }

    public void administrationConfigurations(ExtendedRequest request, ExtendedResponse response) {
        request.setAttribute("backupPath", backupBO.getBackupPath());

        request.setAttribute("languages", languageBO.getLanguages());

        String defaultLanguage = configurationBO.getString(Constants.CONFIG_DEFAULT_LANGUAGE);

        LanguageDTO language = languageBO.getLanguage(defaultLanguage);

        if (language != null) {
            request.setAttribute("default_language", language.getName());
        }

        request.setAttribute(
                "isMultipleSchemasEnabled", configurationBO.isMultipleSchemasEnabled());

        setJspURL("/WEB-INF/jsp/administration/configurations.jsp");
    }

    public void administrationReports(ExtendedRequest request, ExtendedResponse response) {
        request.setAttribute(
                "biblioCacheFileName",
                tabFieldsBO.getFormFields(RecordType.BIBLIO).getCacheFileName());

        setJspURL("/WEB-INF/jsp/administration/reports.jsp");
    }

    public void administrationCustomReports(ExtendedRequest request, ExtendedResponse response) {
        if (menuPropertiesService.isEnabled("administration_custom_reports")) {
            setJspURL("/WEB-INF/jsp/administration/custom_reports.jsp");

            return;
        }

        throw new IllegalArgumentException("Custom reports are not enabled.");
    }

    public void administrationUserTypes(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/WEB-INF/jsp/administration/user_types.jsp");
    }

    public void administrationAccessCards(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/WEB-INF/jsp/administration/access_cards.jsp");
    }

    public void administrationDatamigration(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/WEB-INF/jsp/administration/migration.jsp");
    }

    public void administrationTranslations(ExtendedRequest request, ExtendedResponse response) {
        request.setAttribute("languages", languageBO.getLanguages());

        setJspURL("/WEB-INF/jsp/administration/translations.jsp");
    }

    public void administrationBriefCustomization(
            ExtendedRequest request, ExtendedResponse response) {
        request.setAttribute(
                "biblioCacheFileName",
                tabFieldsBO.getFormFields(RecordType.BIBLIO).getCacheFileName());
        request.setAttribute(
                "authoritiesCacheFileName",
                tabFieldsBO.getFormFields(RecordType.AUTHORITIES).getCacheFileName());
        request.setAttribute(
                "vocabulariesCacheFileName",
                tabFieldsBO.getFormFields(RecordType.VOCABULARY).getCacheFileName());

        setJspURL("/WEB-INF/jsp/administration/brief_customization.jsp");
    }

    public void administrationFormCustomization(
            ExtendedRequest request, ExtendedResponse response) {
        request.setAttribute(
                "biblioCacheFileName",
                tabFieldsBO.getFormFields(RecordType.BIBLIO).getCacheFileName());
        request.setAttribute(
                "authoritiesCacheFileName",
                tabFieldsBO.getFormFields(RecordType.AUTHORITIES).getCacheFileName());
        request.setAttribute(
                "vocabulariesCacheFileName",
                tabFieldsBO.getFormFields(RecordType.VOCABULARY).getCacheFileName());

        setJspURL("/WEB-INF/jsp/administration/form_customization.jsp");
    }

    public void multiSchemaTranslations(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/WEB-INF/jsp/multi_schema/translations.jsp");
    }

    public void multiSchemaBackup(ExtendedRequest request, ExtendedResponse response) {
        String backupPath = SchemaThreadLocal.withGlobalSchema(() -> backupBO.getBackupPath());

        request.setAttribute("backupPath", backupPath);

        request.setAttribute("enabledSchemas", schemaBO.getEnabledSchemasList());

        setJspURL("/WEB-INF/jsp/multi_schema/backup.jsp");
    }

    public void multiSchemaConfigurations(ExtendedRequest request, ExtendedResponse response) {
        String backupPath = SchemaThreadLocal.withGlobalSchema(backupBO::getBackupPath);

        request.setAttribute("backupPath", backupPath);

        request.setAttribute("languages", languageBO.getLanguages());

        String defaultLanguage = configurationBO.getString(Constants.CONFIG_DEFAULT_LANGUAGE);

        LanguageDTO language = languageBO.getLanguage(defaultLanguage);

        if (language != null) {
            request.setAttribute("default_language", language.getName());
        }

        request.setAttribute(
                "isMultipleSchemasEnabled", configurationBO.isMultipleSchemasEnabled());

        setJspURL("/WEB-INF/jsp/multi_schema/configurations.jsp");
    }

    public void multiSchemaManage(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/WEB-INF/jsp/multi_schema/manage.jsp");
    }

    public void helpAboutBiblivre(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/WEB-INF/jsp/help/about_biblivre.jsp");
    }

    public void setup(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/WEB-INF/jsp/setup.jsp");
    }

    public void catalogingImport(ExtendedRequest request, ExtendedResponse response) {
        setJspURL("/WEB-INF/jsp/cataloging/import.jsp");
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

    @Autowired
    public void setUserFieldBO(UserFieldBO userFieldBO) {
        this.userFieldBO = userFieldBO;
    }

    @Autowired
    public void setIndexingGroupBO(IndexingGroupBO indexingGroupBO) {
        this.indexingGroupBO = indexingGroupBO;
    }

    @Autowired
    public void setFieldsBO(TabFieldsBO tabFieldsBO) {
        this.tabFieldsBO = tabFieldsBO;
    }

    @Autowired
    public void setLanguageBO(LanguageBO languageBO) {
        this.languageBO = languageBO;
    }

    @Autowired
    public void setSchemaBO(SchemaBO schemaBO) {
        this.schemaBO = schemaBO;
    }
}
