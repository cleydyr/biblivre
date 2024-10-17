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
package biblivre.core.auth;

import static biblivre.core.auth.AuthorizationPointTypes.*;

import biblivre.core.SchemaThreadLocal;
import biblivre.core.utils.Constants;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

public class AuthorizationPoints implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    private static AuthorizationPoints notLoggedMultiSchemaInstance;
    private static AuthorizationPoints notLoggedSingleSchemaInstance;

    private final Map<Pair<String, String>, Boolean> points;
    private Collection<String> permissions;
    @Getter @Setter private boolean admin;
    @Setter @Getter private boolean logged;

    @Setter @Getter private boolean employee;
    @Setter @Getter private String schema;

    public static AuthorizationPoints getNotLoggedInstance() {
        String schema = SchemaThreadLocal.get();

        if (Constants.GLOBAL_SCHEMA.equals(schema)) {
            if (AuthorizationPoints.notLoggedMultiSchemaInstance == null) {
                AuthorizationPoints.notLoggedMultiSchemaInstance =
                        new AuthorizationPoints(Constants.GLOBAL_SCHEMA, false, false, null);
            }

            return AuthorizationPoints.notLoggedMultiSchemaInstance;
        }

        if (AuthorizationPoints.notLoggedSingleSchemaInstance == null) {
            AuthorizationPoints.notLoggedSingleSchemaInstance =
                    new AuthorizationPoints("*", false, false, null);
        }

        return AuthorizationPoints.notLoggedSingleSchemaInstance;
    }

    public AuthorizationPoints(
            String schema, boolean logged, boolean employee, Collection<String> permissions) {
        this.schema = schema;
        this.admin = false;
        this.employee = employee;
        this.permissions = permissions;
        this.logged = logged;

        if (this.permissions == null) {
            this.permissions = Collections.emptySet();
        }

        this.points = new HashMap<>();

        setupLoginAuthorizations();

        setupMenuModuleAuthorizations();

        setupBibliographicCatalogingAuthorizations();

        setupHoldingCatalogingAuthorizations();

        setupCatalogingAuthorizations();

        setupAuthorityCatalogingAuthorizations();

        setupVocabularyCatalogingAuthorizations();

        setupLabelAuthorizations();

        setupCirculationAuthorizations();

        setupAcquisitionAuthorizations();

        setupAdministrationAuthorizations();

        setupMultiSchemaAuthorization();

        setupDigitalMediaAuthorization();
    }

    private void setupDigitalMediaAuthorization() {
        this.addAuthPoint("digitalmedia", "upload", DIGITALMEDIA_UPLOAD);
        this.addAuthPoint("digitalmedia", "download", DIGITALMEDIA_DOWNLOAD);
    }

    private void setupLoginAuthorizations() {
        this.addAuthPoint("login", "login", LOGIN);
        this.addAuthPoint("login", "logout", LOGIN);
        this.addAuthPoint("login", "change_password", LOGIN_CHANGE_PASSWORD);
    }

    private void setupCatalogingAuthorizations() {
        this.addAuthPoint(
                "cataloging",
                "import_upload",
                CATALOGING_BIBLIOGRAPHIC_SAVE,
                CATALOGING_AUTHORITIES_SAVE,
                CATALOGING_VOCABULARY_SAVE);
        this.addAuthPoint(
                "cataloging",
                "save_import",
                CATALOGING_BIBLIOGRAPHIC_SAVE,
                CATALOGING_AUTHORITIES_SAVE,
                CATALOGING_VOCABULARY_SAVE);
        this.addAuthPoint(
                "cataloging",
                "parse_marc",
                CATALOGING_BIBLIOGRAPHIC_SAVE,
                CATALOGING_AUTHORITIES_SAVE,
                CATALOGING_VOCABULARY_SAVE);
        this.addAuthPoint(
                "cataloging",
                "import_search",
                CATALOGING_BIBLIOGRAPHIC_SAVE,
                CATALOGING_AUTHORITIES_SAVE,
                CATALOGING_VOCABULARY_SAVE);
    }

    private void setupMultiSchemaAuthorization() {
        this.addAuthPoint("multi_schema", "create", ADMINISTRATION_MULTI_SCHEMA);
        this.addAuthPoint("multi_schema", "toggle", ADMINISTRATION_MULTI_SCHEMA);
        this.addAuthPoint("multi_schema", "delete_schema", ADMINISTRATION_MULTI_SCHEMA);
    }

    private void setupAdministrationAuthorizations() {
        this.addAuthPoint("administration.configurations", "save", ADMINISTRATION_CONFIGURATIONS);
        this.addAuthPoint("administration.configurations", "ignore_update", MENU_OTHER);

        this.addAuthPoint("administration.indexing", "reindex", ADMINISTRATION_INDEXING);
        this.addAuthPoint("administration.indexing", "progress", ADMINISTRATION_INDEXING);

        this.addAuthPoint("administration.translations", "dump", ADMINISTRATION_TRANSLATIONS);
        this.addAuthPoint(
                "administration.translations", "download_dump", ADMINISTRATION_TRANSLATIONS);
        this.addAuthPoint("administration.translations", "load", ADMINISTRATION_TRANSLATIONS);
        this.addAuthPoint("administration.translations", "save", ADMINISTRATION_TRANSLATIONS);
        this.addAuthPoint(
                "administration.translations",
                "save_language_translations",
                ADMINISTRATION_TRANSLATIONS);
        this.addAuthPoint("administration.translations", "list", ADMINISTRATION_TRANSLATIONS);

        this.addAuthPoint("administration.backup", "list", ADMINISTRATION_BACKUP);
        this.addAuthPoint("administration.backup", "prepare", ADMINISTRATION_BACKUP);
        this.addAuthPoint("administration.backup", "backup", ADMINISTRATION_BACKUP);
        this.addAuthPoint("administration.backup", "download", ADMINISTRATION_BACKUP);
        this.addAuthPoint("administration.backup", "progress", ADMINISTRATION_BACKUP);

        this.addAuthPoint("administration.usertype", "search", ADMINISTRATION_USERTYPE_LIST);
        this.addAuthPoint("administration.usertype", "paginate", ADMINISTRATION_USERTYPE_LIST);
        this.addAuthPoint("administration.usertype", "save", ADMINISTRATION_USERTYPE_SAVE);
        this.addAuthPoint("administration.usertype", "delete", ADMINISTRATION_USERTYPE_DELETE);

        this.addAuthPoint("administration.accesscards", "search", ADMINISTRATION_ACCESSCARDS_LIST);
        this.addAuthPoint(
                "administration.accesscards", "paginate", ADMINISTRATION_ACCESSCARDS_LIST);
        this.addAuthPoint("administration.accesscards", "save", ADMINISTRATION_ACCESSCARDS_SAVE);
        this.addAuthPoint(
                "administration.accesscards", "change_status", ADMINISTRATION_ACCESSCARDS_SAVE);
        this.addAuthPoint(
                "administration.accesscards", "delete", ADMINISTRATION_ACCESSCARDS_DELETE);

        this.addAuthPoint("administration.permissions", "search", ADMINISTRATION_PERMISSIONS);
        this.addAuthPoint("administration.permissions", "open", ADMINISTRATION_PERMISSIONS);
        this.addAuthPoint("administration.permissions", "save", ADMINISTRATION_PERMISSIONS);
        this.addAuthPoint("administration.permissions", "delete", ADMINISTRATION_PERMISSIONS);

        this.addAuthPoint("administration.reports", "user_search", ADMINISTRATION_REPORTS);
        this.addAuthPoint("administration.reports", "author_search", ADMINISTRATION_REPORTS);
        this.addAuthPoint("administration.reports", "generate", ADMINISTRATION_REPORTS);
        this.addAuthPoint("administration.reports", "download_report", ADMINISTRATION_REPORTS);

        this.addAuthPoint(
                "administration.customization", "save_brief_formats", ADMINISTRATION_CUSTOMIZATION);
        this.addAuthPoint(
                "administration.customization",
                "insert_brief_formats",
                ADMINISTRATION_CUSTOMIZATION);
        this.addAuthPoint(
                "administration.customization",
                "delete_brief_formats",
                ADMINISTRATION_CUSTOMIZATION);

        this.addAuthPoint(
                "administration.customization",
                "save_form_datafields",
                ADMINISTRATION_CUSTOMIZATION);

        this.addAuthPoint(
                "administration.customization",
                "insert_form_datafields",
                ADMINISTRATION_CUSTOMIZATION);

        this.addAuthPoint(
                "administration.customization",
                "delete_form_datafields",
                ADMINISTRATION_CUSTOMIZATION);
    }

    private void setupAcquisitionAuthorizations() {
        this.addAuthPoint("acquisition.supplier", "search", ACQUISITION_SUPPLIER_LIST);
        this.addAuthPoint("acquisition.supplier", "paginate", ACQUISITION_SUPPLIER_LIST);
        this.addAuthPoint("acquisition.supplier", "save", ACQUISITION_SUPPLIER_SAVE);
        this.addAuthPoint("acquisition.supplier", "delete", ACQUISITION_SUPPLIER_DELETE);

        this.addAuthPoint("acquisition.request", "search", ACQUISITION_REQUEST_LIST);
        this.addAuthPoint("acquisition.request", "paginate", ACQUISITION_REQUEST_LIST);
        this.addAuthPoint("acquisition.request", "open", ACQUISITION_REQUEST_LIST);
        this.addAuthPoint("acquisition.request", "save", ACQUISITION_REQUEST_SAVE);
        this.addAuthPoint("acquisition.request", "delete", ACQUISITION_REQUEST_DELETE);

        this.addAuthPoint("acquisition.quotation", "search", ACQUISITION_QUOTATION_LIST);
        this.addAuthPoint("acquisition.quotation", "list", ACQUISITION_QUOTATION_LIST);
        this.addAuthPoint("acquisition.quotation", "paginate", ACQUISITION_QUOTATION_LIST);
        this.addAuthPoint("acquisition.quotation", "save", ACQUISITION_QUOTATION_SAVE);
        this.addAuthPoint("acquisition.quotation", "delete", ACQUISITION_QUOTATION_DELETE);

        this.addAuthPoint("acquisition.order", "search", ACQUISITION_ORDER_LIST);
        this.addAuthPoint("acquisition.order", "paginate", ACQUISITION_ORDER_LIST);
        this.addAuthPoint("acquisition.order", "save", ACQUISITION_ORDER_SAVE);
        this.addAuthPoint("acquisition.order", "delete", ACQUISITION_ORDER_DELETE);
    }

    private void setupLabelAuthorizations() {
        this.addAuthPoint("cataloging.labels", "create_pdf", CATALOGING_PRINT_LABELS);
        this.addAuthPoint("cataloging.labels", "download_pdf", CATALOGING_PRINT_LABELS);
    }

    private void setupCirculationAuthorizations() {
        this.addAuthPoint("circulation.user", "search", CIRCULATION_LIST);
        this.addAuthPoint("circulation.user", "paginate", CIRCULATION_LIST);
        this.addAuthPoint("circulation.user", "save", CIRCULATION_SAVE);
        this.addAuthPoint("circulation.user", "delete", CIRCULATION_DELETE);
        this.addAuthPoint(
                "circulation.user",
                "load_tab_data",
                CIRCULATION_LENDING_LIST,
                CIRCULATION_RESERVATION_LIST);
        this.addAuthPoint("circulation.user", "block", CIRCULATION_SAVE);
        this.addAuthPoint("circulation.user", "unblock", CIRCULATION_SAVE);

        this.addAuthPoint("circulation.user_cards", "create_pdf", CIRCULATION_PRINT_USER_CARDS);
        this.addAuthPoint("circulation.user_cards", "download_pdf", CIRCULATION_PRINT_USER_CARDS);

        this.addAuthPoint("circulation.lending", "search", CIRCULATION_LENDING_LIST);
        this.addAuthPoint("circulation.lending", "user_search", CIRCULATION_LENDING_LIST);
        this.addAuthPoint("circulation.lending", "list", CIRCULATION_LENDING_LIST);
        this.addAuthPoint("circulation.lending", "lend", CIRCULATION_LENDING_LEND);
        this.addAuthPoint("circulation.lending", "renew_lending", CIRCULATION_LENDING_LEND);
        this.addAuthPoint("circulation.lending", "return_lending", CIRCULATION_LENDING_RETURN);
        this.addAuthPoint(
                "circulation.lending",
                "print_receipt",
                CIRCULATION_LENDING_LEND,
                CIRCULATION_LENDING_RETURN);
        this.addAuthPoint("circulation.lending", "pay_fine", CIRCULATION_SAVE);

        this.addAuthPoint("circulation.reservation", "search", CIRCULATION_RESERVATION_LIST);
        this.addAuthPoint("circulation.reservation", "paginate", CIRCULATION_RESERVATION_LIST);
        this.addAuthPoint("circulation.reservation", "user_search", CIRCULATION_RESERVATION_LIST);
        this.addAuthPoint("circulation.reservation", "reserve", CIRCULATION_RESERVATION_RESERVE);
        this.addAuthPoint("circulation.reservation", "delete", CIRCULATION_RESERVATION_RESERVE);

        this.addAuthPoint("circulation.reservation", "self_open", CIRCULATION_USER_RESERVATION);
        this.addAuthPoint("circulation.reservation", "self_search", CIRCULATION_USER_RESERVATION);
        this.addAuthPoint("circulation.reservation", "self_reserve", CIRCULATION_USER_RESERVATION);
        this.addAuthPoint("circulation.reservation", "self_delete", CIRCULATION_USER_RESERVATION);

        this.addAuthPoint(
                "circulation.accesscontrol", "card_search", CIRCULATION_ACCESS_CONTROL_LIST);
        this.addAuthPoint(
                "circulation.accesscontrol", "user_search", CIRCULATION_ACCESS_CONTROL_LIST);
        this.addAuthPoint("circulation.accesscontrol", "bind", CIRCULATION_ACCESS_CONTROL_BIND);
        this.addAuthPoint("circulation.accesscontrol", "unbind", CIRCULATION_ACCESS_CONTROL_BIND);
    }

    private void setupVocabularyCatalogingAuthorizations() {
        this.addAuthPoint("cataloging.vocabulary", "search", CATALOGING_VOCABULARY_LIST);
        this.addAuthPoint("cataloging.vocabulary", "paginate", CATALOGING_VOCABULARY_LIST);
        this.addAuthPoint("cataloging.vocabulary", "open", CATALOGING_VOCABULARY_LIST);
        this.addAuthPoint("cataloging.vocabulary", "item_count", CATALOGING_VOCABULARY_LIST);
        this.addAuthPoint("cataloging.vocabulary", "autocomplete", CATALOGING_VOCABULARY_LIST);
        this.addAuthPoint("cataloging.vocabulary", "convert", CATALOGING_VOCABULARY_SAVE);
        this.addAuthPoint("cataloging.vocabulary", "save", CATALOGING_VOCABULARY_SAVE);
        this.addAuthPoint("cataloging.vocabulary", "delete", CATALOGING_VOCABULARY_DELETE);
        this.addAuthPoint("cataloging.vocabulary", "move_records", CATALOGING_VOCABULARY_MOVE);
        this.addAuthPoint("cataloging.vocabulary", "export_records", CATALOGING_VOCABULARY_LIST);
        this.addAuthPoint("cataloging.vocabulary", "download_export", CATALOGING_VOCABULARY_LIST);
        this.addAuthPoint(
                "cataloging.vocabulary", "list_brief_formats", ADMINISTRATION_CUSTOMIZATION);
    }

    private void setupAuthorityCatalogingAuthorizations() {
        this.addAuthPoint("cataloging.authorities", "search", CATALOGING_AUTHORITIES_LIST);
        this.addAuthPoint("cataloging.authorities", "paginate", CATALOGING_AUTHORITIES_LIST);
        this.addAuthPoint("cataloging.authorities", "open", CATALOGING_AUTHORITIES_LIST);
        this.addAuthPoint("cataloging.authorities", "item_count", CATALOGING_AUTHORITIES_LIST);
        this.addAuthPoint("cataloging.authorities", "autocomplete", CATALOGING_AUTHORITIES_LIST);
        this.addAuthPoint("cataloging.authorities", "convert", CATALOGING_AUTHORITIES_SAVE);
        this.addAuthPoint("cataloging.authorities", "save", CATALOGING_AUTHORITIES_SAVE);
        this.addAuthPoint("cataloging.authorities", "delete", CATALOGING_AUTHORITIES_DELETE);
        this.addAuthPoint("cataloging.authorities", "move_records", CATALOGING_AUTHORITIES_MOVE);
        this.addAuthPoint("cataloging.authorities", "export_records", CATALOGING_AUTHORITIES_LIST);
        this.addAuthPoint("cataloging.authorities", "download_export", CATALOGING_AUTHORITIES_LIST);
        this.addAuthPoint("cataloging.authorities", "search_author", ADMINISTRATION_REPORTS);
        this.addAuthPoint(
                "cataloging.authorities", "list_brief_formats", ADMINISTRATION_CUSTOMIZATION);
    }

    private void setupHoldingCatalogingAuthorizations() {
        this.addAuthPoint("cataloging.holding", "list", CATALOGING_BIBLIOGRAPHIC_LIST);
        this.addAuthPoint("cataloging.holding", "open", CATALOGING_BIBLIOGRAPHIC_LIST);
        this.addAuthPoint("cataloging.holding", "convert", CATALOGING_BIBLIOGRAPHIC_SAVE);
        this.addAuthPoint("cataloging.holding", "save", CATALOGING_BIBLIOGRAPHIC_SAVE);
        this.addAuthPoint("cataloging.holding", "delete", CATALOGING_BIBLIOGRAPHIC_DELETE);
        this.addAuthPoint(
                "cataloging.holding", "create_automatic_holding", CATALOGING_BIBLIOGRAPHIC_SAVE);
    }

    private void setupBibliographicCatalogingAuthorizations() {
        this.addAuthPoint("cataloging.bibliographic", "search", CATALOGING_BIBLIOGRAPHIC_LIST);
        this.addAuthPoint("cataloging.bibliographic", "paginate", CATALOGING_BIBLIOGRAPHIC_LIST);
        this.addAuthPoint("cataloging.bibliographic", "open", CATALOGING_BIBLIOGRAPHIC_LIST);
        this.addAuthPoint("cataloging.bibliographic", "item_count", CATALOGING_BIBLIOGRAPHIC_LIST);
        this.addAuthPoint(
                "cataloging.bibliographic", "autocomplete", CATALOGING_BIBLIOGRAPHIC_LIST);
        this.addAuthPoint("cataloging.bibliographic", "convert", CATALOGING_BIBLIOGRAPHIC_SAVE);
        this.addAuthPoint("cataloging.bibliographic", "save", CATALOGING_BIBLIOGRAPHIC_SAVE);
        this.addAuthPoint("cataloging.bibliographic", "delete", CATALOGING_BIBLIOGRAPHIC_DELETE);
        this.addAuthPoint(
                "cataloging.bibliographic", "move_records", CATALOGING_BIBLIOGRAPHIC_MOVE);
        this.addAuthPoint(
                "cataloging.bibliographic", "export_records", CATALOGING_BIBLIOGRAPHIC_MOVE);
        this.addAuthPoint(
                "cataloging.bibliographic", "download_export", CATALOGING_BIBLIOGRAPHIC_LIST);
        this.addAuthPoint(
                "cataloging.bibliographic", "add_attachment", CATALOGING_BIBLIOGRAPHIC_SAVE);
        this.addAuthPoint(
                "cataloging.bibliographic", "remove_attachment", CATALOGING_BIBLIOGRAPHIC_SAVE);
        this.addAuthPoint(
                "cataloging.bibliographic", "list_brief_formats", ADMINISTRATION_CUSTOMIZATION);

        this.addAuthPoint(
                "cataloging.bibliographic",
                "private_database_access",
                CATALOGING_BIBLIOGRAPHIC_PRIVATE_DATABASE_ACCESS);
    }

    private void setupMenuModuleAuthorizations() {
        this.addAuthPoint("menu", "list_bibliographic", MENU_SEARCH);
        this.addAuthPoint("menu", "search_bibliographic", MENU_SEARCH);
        this.addAuthPoint("menu", "search_authorities", MENU_SEARCH);
        this.addAuthPoint("menu", "search_vocabulary", MENU_SEARCH);
        this.addAuthPoint(
                "menu",
                "cataloging_bibliographic",
                CATALOGING_BIBLIOGRAPHIC_SAVE,
                CATALOGING_BIBLIOGRAPHIC_DELETE);
        this.addAuthPoint(
                "menu",
                "cataloging_authorities",
                CATALOGING_AUTHORITIES_SAVE,
                CATALOGING_AUTHORITIES_DELETE);
        this.addAuthPoint(
                "menu",
                "cataloging_vocabulary",
                CATALOGING_VOCABULARY_SAVE,
                CATALOGING_VOCABULARY_DELETE);
        this.addAuthPoint(
                "menu",
                "cataloging_import",
                CATALOGING_BIBLIOGRAPHIC_SAVE,
                CATALOGING_AUTHORITIES_SAVE,
                CATALOGING_VOCABULARY_SAVE);
        this.addAuthPoint("menu", "cataloging_labels", CATALOGING_PRINT_LABELS);

        this.addAuthPoint(
                "menu", "circulation_user", CIRCULATION_LIST, CIRCULATION_SAVE, CIRCULATION_DELETE);
        this.addAuthPoint(
                "menu",
                "circulation_lending",
                CIRCULATION_LENDING_LIST,
                CIRCULATION_LENDING_LEND,
                CIRCULATION_LENDING_RETURN);
        this.addAuthPoint(
                "menu",
                "circulation_reservation",
                CIRCULATION_RESERVATION_LIST,
                CIRCULATION_RESERVATION_RESERVE);
        this.addAuthPoint("menu", "circulation_access", CIRCULATION_LIST);
        this.addAuthPoint("menu", "circulation_user_cards", CIRCULATION_PRINT_USER_CARDS);

        this.addAuthPoint("menu", "circulation_user_reservation", CIRCULATION_USER_RESERVATION);

        this.addAuthPoint(
                "menu",
                "acquisition_order",
                ACQUISITION_ORDER_LIST,
                ACQUISITION_ORDER_SAVE,
                ACQUISITION_ORDER_DELETE);
        this.addAuthPoint(
                "menu",
                "acquisition_quotation",
                ACQUISITION_QUOTATION_LIST,
                ACQUISITION_QUOTATION_SAVE,
                ACQUISITION_QUOTATION_DELETE);
        this.addAuthPoint(
                "menu",
                "acquisition_request",
                ACQUISITION_REQUEST_LIST,
                ACQUISITION_REQUEST_SAVE,
                ACQUISITION_REQUEST_DELETE);
        this.addAuthPoint(
                "menu",
                "acquisition_supplier",
                ACQUISITION_SUPPLIER_LIST,
                ACQUISITION_SUPPLIER_SAVE,
                ACQUISITION_SUPPLIER_DELETE);

        this.addAuthPoint("menu", "administration_password", LOGIN_CHANGE_PASSWORD);
        this.addAuthPoint("menu", "administration_maintenance", ADMINISTRATION_INDEXING);
        this.addAuthPoint(
                "menu",
                "administration_user_types",
                ADMINISTRATION_USERTYPE_LIST,
                ADMINISTRATION_USERTYPE_SAVE,
                ADMINISTRATION_USERTYPE_DELETE);
        this.addAuthPoint(
                "menu",
                "administration_access_cards",
                ADMINISTRATION_ACCESSCARDS_LIST,
                ADMINISTRATION_ACCESSCARDS_SAVE,
                ADMINISTRATION_ACCESSCARDS_DELETE);
        this.addAuthPoint("menu", "administration_configurations", ADMINISTRATION_CONFIGURATIONS);
        this.addAuthPoint("menu", "administration_permissions", ADMINISTRATION_PERMISSIONS);
        this.addAuthPoint("menu", "administration_reports", ADMINISTRATION_REPORTS);
        this.addAuthPoint("menu", "administration_custom_reports", ADMINISTRATION_REPORTS);
        this.addAuthPoint("menu", "administration_translations", ADMINISTRATION_TRANSLATIONS);
        this.addAuthPoint(
                "menu", "administration_brief_customization", ADMINISTRATION_CUSTOMIZATION);
        this.addAuthPoint(
                "menu", "administration_form_customization", ADMINISTRATION_CUSTOMIZATION);

        this.addAuthPoint("menu", "multi_schema_manage", ADMINISTRATION_MULTI_SCHEMA);
        this.addAuthPoint("menu", "multi_schema_configurations", ADMINISTRATION_MULTI_SCHEMA);
        this.addAuthPoint("menu", "multi_schema_translations", ADMINISTRATION_MULTI_SCHEMA);
        this.addAuthPoint("menu", "multi_schema_backup", ADMINISTRATION_MULTI_SCHEMA);

        this.addAuthPoint("menu", "help_about_biblivre", MENU_HELP);
        this.addAuthPoint("menu", "ping", MENU_OTHER);
        this.addAuthPoint("menu", "i18n", MENU_OTHER);
        this.addAuthPoint("menu", "test", MENU_OTHER);
        this.addAuthPoint("menu", "setup", ADMINISTRATION_RESTORE);
        this.addAuthPoint(
                "administration.setup",
                "clean_install",
                AuthorizationPointTypes.ADMINISTRATION_RESTORE);
        this.addAuthPoint(
                "administration.setup",
                "list_restores",
                AuthorizationPointTypes.ADMINISTRATION_RESTORE);
        this.addAuthPoint(
                "administration.setup",
                "upload_biblivre4",
                AuthorizationPointTypes.ADMINISTRATION_RESTORE);
        this.addAuthPoint(
                "administration.setup", "restore", AuthorizationPointTypes.ADMINISTRATION_RESTORE);
    }

    private void addAuthPoint(String module, String action, AuthorizationPointTypes... types) {
        Pair<String, String> pair = Pair.of(module, action);

        boolean matchesThisSchema = false;

        for (AuthorizationPointTypes type : types) {
            if (type.getSchemaScope() == AuthorizationSchemaScope.ANY) {
                matchesThisSchema = true;
                break;
            }

            if (this.schema.equals(Constants.GLOBAL_SCHEMA)
                    && type.getSchemaScope() == AuthorizationSchemaScope.GLOBAL_SCHEMA) {
                matchesThisSchema = true;
                break;
            }

            if (!this.schema.equals(Constants.GLOBAL_SCHEMA)
                    && type.getSchemaScope() == AuthorizationSchemaScope.SINGLE_SCHEMA) {
                matchesThisSchema = true;
                break;
            }
        }

        if (!matchesThisSchema) {
            return;
        }

        boolean matchesUserAffiliation = false;

        for (AuthorizationPointTypes type : types) {
            if (type.getUserScope() == AuthorizationUserScope.ANY) {
                matchesUserAffiliation = true;
                break;
            }

            if (this.employee && type.getUserScope() == AuthorizationUserScope.EMPLOYEE) {
                matchesUserAffiliation = true;
                break;
            }
            if (!this.employee && type.getUserScope() == AuthorizationUserScope.READER) {
                matchesUserAffiliation = true;
                break;
            }
        }

        if (!matchesUserAffiliation) {
            return;
        }

        boolean allowed = false;

        for (AuthorizationPointTypes type : types) {
            allowed =
                    type.isPublic()
                            || (type.isPublicForLoggedUsers() && this.isLogged())
                            || this.permissions.contains(type.name());

            if (allowed) {
                break;
            }
        }

        this.points.put(pair, allowed);
    }

    public boolean isAllowed(String module, String action) {
        Pair<String, String> moduleAction = Pair.of(module, action);

        Boolean allowed = this.points.get(moduleAction);

        if (allowed == null) {
            return false;
        }

        if (admin) {
            return true;
        }

        return allowed;
    }

    public boolean isAllowed(AuthorizationPointTypes type) {
        return this.admin || this.permissions.contains(type.name());
    }
}
