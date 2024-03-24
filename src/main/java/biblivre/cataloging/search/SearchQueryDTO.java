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

import biblivre.cataloging.enums.RecordDatabase;
import biblivre.cataloging.enums.SearchOperator;
import biblivre.core.AbstractDTO;
import biblivre.core.enums.SearchMode;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.utils.TextUtils;
import biblivre.marc.MaterialType;
import java.io.Serial;
import java.text.ParseException;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public final class SearchQueryDTO extends AbstractDTO {
    public static final Pattern PATTERN = Pattern.compile("\\s*(\"[^\"]+\"|[^\\s\"]+)");
    @Serial private static final long serialVersionUID = 1L;

    @Getter @Setter private SearchMode searchMode;
    @Setter @Getter private RecordDatabase database;
    @Setter private MaterialType materialType;
    @Getter private List<SearchTermDTO> terms;
    @Getter private String parameters;
    @Setter @Getter private boolean holdingSearch;
    @Setter @Getter private boolean reservedOnly;
    private static final Collection<String> DATE_FIELDS =
            List.of("created", "modified", "holding_created", "holding_modified");

    public SearchQueryDTO(String jsonString) {
        this.parameters = jsonString;

        this.terms = new ArrayList<>();

        try {
            this.fromJson(jsonString);
        } catch (ParseException e) {
            throw new ValidationException("cataloging.error.invalid_search_parameters", e);
        }

        if (this.getDatabase() == null) {
            throw new ValidationException("cataloging.error.invalid_database");
        }
    }

    public SearchQueryDTO(RecordDatabase database) {
        this.setDatabase(database);

        if (this.getDatabase() == null) {
            throw new ValidationException("cataloging.error.invalid_database");
        }
    }

    private void fromJson(String jsonString) throws ParseException {
        JSONObject json = new JSONObject(jsonString);

        setBasicFields(json);

        JSONArray searchTerms = json.optJSONArray("search_terms");

        if (searchTerms == null || searchTerms.isEmpty()) {
            // If no search terms were specified, force a "list all" search
            this.setSearchMode(SearchMode.LIST_ALL);
            return;
        }

        for (int i = 0; i < searchTerms.length(); i++) {
            JSONObject searchTerm = searchTerms.optJSONObject(i);

            processSearchTerm(searchTerm);
        }

        if (this.terms.isEmpty()) {
            throw new ValidationException("cataloging.error.no_valid_terms");
        }
    }

    private void processSearchTerm(JSONObject searchTerm) throws ParseException {
        if (searchTerm == null) {
            return;
        }

        String field = searchTerm.optString("field");

        String f = StringUtils.defaultString(field);

        String query = getActualQuery(searchTerm, f);

        String sanitizedQuery = TextUtils.preparePhrase(query);

        Set<String> validTerms = getValidTerms(f, sanitizedQuery);

        if (validTerms.isEmpty()) {
            return;
        }

        String operator = searchTerm.optString("operator");
        String endDate = searchTerm.optString("end_date");
        String startDate = searchTerm.optString("start_date");

        SearchTermDTO dto = buildSearchTermDTO(field, operator, startDate, endDate, validTerms);

        this.addTerm(dto);
    }

    private String getActualQuery(JSONObject searchTerm, String f) {
        String query = searchTerm.optString("query");

        if (DATE_FIELDS.stream().anyMatch(f::equals)) {
            query = "date";
        }
        return query;
    }

    private void setBasicFields(JSONObject json) {
        this.setSearchMode(SearchMode.fromString(json.optString("search_mode")));
        this.setDatabase(RecordDatabase.fromString(json.optString("database")));
        this.setMaterialType(MaterialType.fromString(json.optString("material_type")));
        this.setHoldingSearch(json.optBoolean("holding_search"));
        this.setReservedOnly(json.optBoolean("reserved_only"));
    }

    private static Set<String> getValidTerms(String f, String sanitizedQuery) {
        return PATTERN.matcher(sanitizedQuery)
                .results()
                .map(MatchResult::group)
                .map(
                        group ->
                                isTermsGroupedByQuotes(group)
                                                || "holding_accession_number".equals(f)
                                        ? new String[] {group}
                                        : TextUtils.prepareWords(group))
                .flatMap(Arrays::stream)
                .filter(term -> term.length() > 1 || NumberUtils.isDigits(term))
                .collect(Collectors.toSet());
    }

    private static boolean isTermsGroupedByQuotes(String group) {
        return group.charAt(0) == '"' && group.indexOf(' ') != -1;
    }

    private static SearchTermDTO buildSearchTermDTO(
            String field, String operator, String startDate, String endDate, Set<String> validTerms)
            throws ParseException {
        SearchTermDTO dto = new SearchTermDTO();

        dto.setField(field);
        dto.setOperator(SearchOperator.fromString(operator));
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        dto.addTerms(validTerms);

        return dto;
    }

    public MaterialType getMaterialType() {
        return Objects.requireNonNullElse(this.materialType, MaterialType.ALL);
    }

    public Set<String> getSimpleTerms() {
        if (this.terms == null || this.terms.isEmpty()) {
            return Collections.emptySet();
        }

        return this.terms.getFirst().getTerms();
    }

    public void addTerm(SearchTermDTO dto) {
        if (dto == null) {
            return;
        }

        this.terms.add(dto);
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();

        json.putOpt("search_mode", this.getSearchMode());
        json.putOpt("database", this.getDatabase());
        json.putOpt("material_type", this.getMaterialType());
        json.putOpt("holding_search", this.isHoldingSearch());
        json.putOpt("terms", this.getTerms());

        return json;
    }
}
