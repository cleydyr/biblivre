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
package biblivre.circulation.user;

import biblivre.core.AbstractDTO;
import biblivre.core.enums.SearchMode;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.utils.TextUtils;
import java.io.Serial;
import java.text.ParseException;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

@Getter
public final class UserSearchDTO extends AbstractDTO {
    @Serial private static final long serialVersionUID = 1L;

    @Setter private SearchMode searchMode;
    @Setter private String field;
    @Setter private String query;
    @Setter private Integer type;
    @Setter private boolean isPendingFines;
    @Setter private boolean isLateLendings;
    @Setter private boolean isLoginAccess;
    @Setter private boolean isUserCardNeverPrinted;
    @Setter private boolean isInactiveOnly;
    private Date createdStartDate;
    private Date createdEndDate;
    private Date modifiedStartDate;
    private Date modifiedEndDate;

    public UserSearchDTO() {}

    public UserSearchDTO(String jsonString) throws ValidationException {
        try {
            this.fromJson(jsonString);
        } catch (Exception e) {
            throw new ValidationException("cataloging.error.invalid_search_parameters");
        }
    }

    private void fromJson(String jsonString) throws JSONException, ParseException {
        JSONObject json = new JSONObject(jsonString);

        this.setSearchMode(SearchMode.fromString(json.optString("mode")));
        this.setField(json.optString("field"));
        this.setQuery(json.optString("query"));
        this.setType(json.optInt("type"));
        this.setPendingFines(json.optBoolean("users_with_pending_fines", false));
        this.setLateLendings(json.optBoolean("users_with_late_lendings", false));
        this.setLoginAccess(json.optBoolean("users_who_have_login_access", false));
        this.setUserCardNeverPrinted(json.optBoolean("users_without_user_card", false));
        this.setInactiveOnly(json.optBoolean("inactive_users_only", false));
        this.setCreatedStartDate(json.optString("created_start"));
        this.setCreatedEndDate(json.optString("created_end"));
        this.setModifiedStartDate(json.optString("modified_start"));
        this.setModifiedEndDate(json.optString("modified_end"));
    }

    public void setCreatedStartDate(String createdStartDate) throws ParseException {
        this.createdStartDate = null;
        if (StringUtils.isNotBlank(createdStartDate)) {
            this.createdStartDate = TextUtils.parseDate(createdStartDate);
        }
    }

    public void setCreatedEndDate(String createdEndDate) throws ParseException {
        this.createdEndDate = null;
        if (StringUtils.isNotBlank(createdEndDate)) {
            this.createdEndDate = TextUtils.parseDate(createdEndDate);
        }
    }

    public void setModifiedStartDate(String modifiedStartDate) throws ParseException {
        this.modifiedStartDate = null;
        if (StringUtils.isNotBlank(modifiedStartDate)) {
            this.modifiedStartDate = TextUtils.parseDate(modifiedStartDate);
        }
    }

    public void setModifiedEndDate(String modifiedEndDate) throws ParseException {
        this.modifiedEndDate = null;
        if (StringUtils.isNotBlank(modifiedEndDate)) {
            this.modifiedEndDate = TextUtils.parseDate(modifiedEndDate);
        }
    }

    public boolean isSimpleSearch() {
        return this.searchMode == SearchMode.SIMPLE;
    }

    public boolean isListAll() {
        return this.query.isEmpty();
    }

    public boolean isNameOrIdSearch() {
        return field.isEmpty();
    }

    public boolean isSearchById() {
        return StringUtils.isNumeric(query) && field.isEmpty();
    }
}
