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
package biblivre.cataloging;

import biblivre.core.AbstractDTO;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

@Getter
public class FormTabDatafieldDTO extends AbstractDTO implements Comparable<FormTabDatafieldDTO> {

    @Serial private static final long serialVersionUID = 1L;

    @Setter private String datafield;
    @Setter private boolean collapsed;
    @Setter private boolean repeatable;
    @Setter private String indicator1;
    @Setter private String indicator2;
    @Setter private String materialType;
    @Setter private Integer sortOrder;
    private final List<FormTabSubfieldDTO> subfields;

    public FormTabDatafieldDTO(JSONObject jsonObject) {
        super(jsonObject);
        this.subfields = new ArrayList<>();

        if (jsonObject.has("subfields")) {
            JSONArray array = jsonObject.getJSONArray("subfields");
            for (int i = 0; i < array.length(); i++) {
                JSONObject subfield = array.getJSONObject(i);

                this.subfields.add(new FormTabSubfieldDTO(subfield));
            }
        }
    }

    public FormTabDatafieldDTO() {
        this.subfields = new ArrayList<>();
    }

    public void addSubfield(FormTabSubfieldDTO subfield) {
        this.subfields.add(subfield);
    }

    public String[] getIndicator1Values() {
        return StringUtils.split(this.getIndicator1(), ",");
    }

    public String[] getIndicator2Values() {
        return StringUtils.split(this.getIndicator2(), ",");
    }

    public String[] getMaterialTypeValues() {
        return StringUtils.split(this.getMaterialType(), ",");
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();

        json.putOpt("datafield", this.getDatafield());
        json.putOpt("collapsed", this.isCollapsed());
        json.putOpt("repeatable", this.isRepeatable());
        json.putOpt("sortOrder", this.getSortOrder());

        if (StringUtils.isNotBlank(this.getIndicator1())) {
            json.putOpt("indicator1", new JSONArray(this.getIndicator1Values()));
        }

        if (StringUtils.isNotBlank(this.getIndicator2())) {
            json.putOpt("indicator2", new JSONArray(this.getIndicator2Values()));
        }

        if (StringUtils.isNotBlank(this.getMaterialType())) {
            json.putOpt("material_type", new JSONArray(this.getMaterialTypeValues()));
        }

        for (FormTabSubfieldDTO subfield : this.getSubfields()) {
            json.append("subfields", subfield.toJSONObject());
        }

        return json;
    }

    @Override
    public int compareTo(FormTabDatafieldDTO other) {
        if (other == null) {
            return -1;
        }

        if (this.getSortOrder() != null && other.getSortOrder() != null) {
            return this.getSortOrder().compareTo(other.getSortOrder());
        }

        return 0;
    }
}
