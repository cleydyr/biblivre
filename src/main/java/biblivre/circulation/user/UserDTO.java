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
import java.io.Serial;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.json.JSONObject;

@Setter
@Getter
public class UserDTO extends AbstractDTO {
    @Serial private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private Integer type;
    private String photoId;
    private UserStatus status;
    private Integer loginId;
    private boolean isUserCardPrinted;

    private Map<String, String> fields;

    private transient Integer currentLendings;
    private transient String usertypeName;

    public UserDTO() {
        this.fields = new HashMap<>();
    }

    public String getEnrollment() {
        return StringUtils.leftPad(String.valueOf(this.getId()), 5, "0");
    }

    public void addField(String key, String value) {
        this.fields.put(key, value);
    }

    public boolean isInactive() {
        return this.getStatus() == UserStatus.INACTIVE;
    }

    public boolean hasLogin() {
        return loginId != null && loginId > 0;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();

        json.putOpt("id", this.getId());
        json.putOpt("enrollment", this.getEnrollment());
        json.putOpt("name", this.getName());
        json.putOpt("loginId", this.getLoginId());
        json.putOpt("status", this.getStatus());
        json.putOpt("type", this.getType());
        json.putOpt("type_name", this.getUsertypeName());
        json.putOpt("photo_id", this.getPhotoId());
        json.putOpt("fields", this.getFields());
        json.putOpt("current_lendings", this.getCurrentLendings());

        json.putOpt(
                "created", DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(getCreated()));
        json.putOpt(
                "modified",
                DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(getModified()));

        return json;
    }
}
