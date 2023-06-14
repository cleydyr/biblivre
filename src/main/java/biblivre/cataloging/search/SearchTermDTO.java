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
package biblivre.cataloging.search;

import biblivre.cataloging.enums.SearchOperator;
import biblivre.core.AbstractDTO;
import biblivre.core.utils.TextUtils;
import java.io.Serial;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.json.JSONObject;

public class SearchTermDTO extends AbstractDTO {
    @Serial private static final long serialVersionUID = 1L;

    private Set<String> terms;
    private String field;
    private SearchOperator operator;
    private Date startDate;
    private Date endDate;

    public Set<String> getTerms() {
        return this.terms;
    }

    public boolean addTerm(String term) {
        if (this.terms == null) {
            this.terms = new HashSet<>();
        }

        return this.terms.add(term);
    }

    public boolean addTerms(Set<String> terms) {
        if (this.terms == null) {
            this.terms = new HashSet<>();
        }

        return this.terms.addAll(terms);
    }

    public String getField() {
        return StringUtils.defaultString(this.field);
    }

    public void setField(String field) {
        this.field = field;
    }

    public SearchOperator getOperator() {
        return this.operator;
    }

    public void setOperator(SearchOperator operator) {
        this.operator = operator;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(String startDate) throws ParseException {
        this.startDate = null;
        if (StringUtils.isNotBlank(startDate)) {
            this.startDate = TextUtils.parseDate(startDate);
        }
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(String endDate) throws ParseException {
        this.endDate = null;
        if (StringUtils.isNotBlank(endDate)) {
            this.endDate = TextUtils.parseDate(endDate);
        }
    }

    public boolean isAndNot() {
        return this.operator == SearchOperator.AND_NOT;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();

        json.append("terms", StringUtils.join(this.getTerms(), " "));
        json.putOpt("field", this.getField());
        json.putOpt("operator", this.getOperator());

        if (this.getStartDate() != null) {
            json.putOpt(
                    "start_date",
                    DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(this.getStartDate()));
        }

        if (this.getEndDate() != null) {
            json.putOpt(
                    "end_date",
                    DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(this.getEndDate()));
        }

        return json;
    }
}
