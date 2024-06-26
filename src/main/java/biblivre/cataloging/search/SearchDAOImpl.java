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

import biblivre.cataloging.enums.RecordType;
import biblivre.cataloging.enums.SearchOperator;
import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;
import biblivre.core.utils.CalendarUtils;
import biblivre.core.utils.CharPool;
import biblivre.core.utils.TextUtils;
import biblivre.marc.MaterialType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

@Service
public class SearchDAOImpl extends AbstractDAO implements SearchDAO {
    @Override
    public SearchDTO getSearch(Integer searchId, RecordType recordType) {
        SearchDTO search = new SearchDTO(recordType);

        if (searchId == null) {
            return search;
        }

        try (Connection con = datasource.getConnection()) {

            String sql = "SELECT * FROM " + recordType + "_searches " + "WHERE id = ?;";

            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, searchId);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                SearchQueryDTO query = new SearchQueryDTO(rs.getString("parameters"));

                search.setId(rs.getInt("id"));
                search.setQuery(query);
            }

            return search;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public boolean createSearch(SearchDTO search) {
        if (search == null) {
            return false;
        }

        try (Connection con = datasource.getConnection()) {
            StringBuilder sql = new StringBuilder();

            RecordType recordType = search.getRecordType();

            sql.append("INSERT INTO ").append(recordType).append("_searches ");
            sql.append("(id, parameters) VALUES (?, ?);");

            PreparedStatement pst = con.prepareStatement(sql.toString());

            int id = this.getNextSerial(recordType + "_searches_id_seq");

            if (id == 0) {
                return false;
            }

            search.setId(id);

            pst.setInt(1, search.getId());
            pst.setString(2, search.getParameters());

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public boolean populateSimpleSearch(SearchDTO search, boolean deleteOldResults) {
        SearchQueryDTO query = search.getQuery();

        Set<String> terms = search.getQuery().getSimpleTerms();
        List<String> sqlTerms = new ArrayList<>(terms.size());
        List<String> sqlOperators = new ArrayList<>(terms.size());
        List<String[]> exactTerms = new ArrayList<>();

        for (String term : terms) {
            String operator = "=";

            if (term.charAt(0) == '"') {
                String rTerm = term.substring(1, term.length() - 1);

                term = "\\m" + rTerm + "\\M";
                operator = "~";

                exactTerms.add(TextUtils.prepareExactTerms(rTerm));
            } else if (term.endsWith("*")) {
                term = term.substring(0, term.length() - 1) + "%";
                operator = "LIKE";
            }

            sqlTerms.add(term);
            sqlOperators.add(operator);
        }

        String sql = this.createSimpleSelectClause(search, sqlOperators, exactTerms);

        if (sql == null) {
            return false;
        }

        return withTransactionContext(
                con -> {
                    if (deleteOldResults) {
                        String deleteSql =
                                "DELETE FROM "
                                        + search.getRecordType()
                                        + "_search_results "
                                        + "WHERE search_id = ?;";

                        PreparedStatement deletePst = con.prepareStatement(deleteSql);
                        deletePst.setInt(1, search.getId());
                        deletePst.executeUpdate();
                    }

                    PreparedStatement pst = con.prepareStatement(sql);
                    int index = 1;

                    // With statement
                    int j = 0;
                    for (int i = 0; i < sqlTerms.size(); i++) {
                        String term = sqlTerms.get(i);
                        String operator = sqlOperators.get(i);

                        pst.setString(index++, term);

                        if (operator.equals("~")) {
                            String[] eTerms = exactTerms.get(j++);

                            for (String eTerm : eTerms) {
                                pst.setString(index++, eTerm);
                            }
                        }
                    }

                    pst.setString(index++, query.getDatabase().toString());

                    if (query.getMaterialType() != MaterialType.ALL) {
                        pst.setString(index, query.getMaterialType().toString());
                    }

                    int records = pst.executeUpdate();

                    return records > 0;
                });
    }

    @Override
    public boolean populateAdvancedSearch(SearchDTO search, boolean deleteOld) {
        SearchQueryDTO query = search.getQuery();
        String sql = this.createAdvancedSelectClause(search);

        if (sql == null) {
            return false;
        }

        return withTransactionContext(
                con -> {
                    if (deleteOld) {
                        String deleteSql =
                                "DELETE FROM "
                                        + search.getRecordType()
                                        + "_search_results "
                                        + "WHERE search_id = ?;";

                        PreparedStatement deletePst = con.prepareStatement(deleteSql);
                        deletePst.setInt(1, search.getId());
                        deletePst.executeUpdate();
                    }

                    PreparedStatement pst = con.prepareStatement(sql);
                    int index = 1;

                    pst.setString(index++, query.getDatabase().toString());

                    if (query.getMaterialType() != MaterialType.ALL) {
                        pst.setString(index++, query.getMaterialType().toString());
                    }

                    List<SearchTermDTO> searchTerms = search.getQuery().getTerms();
                    for (SearchTermDTO searchTerm : searchTerms) {
                        String field = searchTerm.getField();

                        for (String term : searchTerm.getTerms()) {
                            // See: createAdvancedFilterClause();

                            if (StringUtils.isNumeric(field)) {
                                // Field is a indexing_group_id
                                pst.setString(index++, term);

                                if (!field.equals("0")) {
                                    pst.setInt(index++, TextUtils.defaultInt(field));
                                }

                                continue;
                            }

                            switch (field) {
                                case "record_id" -> pst.setInt(index++, TextUtils.defaultInt(term));
                                case "holding_id" -> {
                                    pst.setString(index++, query.getDatabase().toString());
                                    pst.setInt(index++, TextUtils.defaultInt(term));
                                }
                                case "holding_accession_number" -> {
                                    pst.setString(index++, query.getDatabase().toString());
                                    pst.setString(index++, term);
                                }
                                case "holding_created",
                                        "holding_modified",
                                        "holding_label_never_printed",
                                        "created",
                                        "modified" -> {
                                    if (!field.equals("created") && !field.equals("modified")) {
                                        pst.setString(index++, query.getDatabase().toString());
                                    }
                                    Date startDate = searchTerm.getStartDate();
                                    Date endDate = searchTerm.getEndDate();
                                    if (startDate != null) {
                                        pst.setTimestamp(
                                                index++, CalendarUtils.toSqlTimestamp(startDate));
                                    }
                                    if (endDate != null) {
                                        if (CalendarUtils.isMidnight(endDate)) {
                                            endDate = DateUtils.addDays(endDate, 1);
                                        }

                                        pst.setTimestamp(
                                                index++, CalendarUtils.toSqlTimestamp(endDate));
                                    }
                                }
                            }
                        }
                    }

                    int records = pst.executeUpdate();

                    return records > 0;
                });
    }

    private String createSimpleSelectClause(
            SearchDTO search, List<String> operators, List<String[]> exactTerms) {
        SearchQueryDTO query = search.getQuery();

        if (operators == null || operators.isEmpty()) {
            return null;
        }

        StringBuilder sql = new StringBuilder();
        int cteCount = 0;

        RecordType recordType = search.getRecordType();

        sql.append("INSERT INTO ").append(recordType).append("_search_results ");
        sql.append("(search_id, indexing_group_id, record_id) ");

        sql.append("WITH ");

        int j = 0;
        for (String operator : operators) {
            cteCount++;

            sql.append("Query_").append(cteCount).append(" AS ( ");
            if (operator.equals("~")) {
                sql.append("SELECT indexing_group_id, record_id FROM ")
                        .append(recordType)
                        .append("_idx_sort WHERE phrase ")
                        .append(operator)
                        .append(" ? ");

                String[] eTerms = exactTerms.get(j++);
                sql.append("AND record_id IN ( ");
                sql.append(
                        StringUtils.repeat(
                                "SELECT record_id FROM "
                                        + recordType
                                        + "_idx_fields WHERE word = ? ",
                                " INTERSECT ",
                                eTerms.length));
                sql.append(") ");
            } else {
                sql.append("SELECT indexing_group_id, record_id FROM ")
                        .append(recordType)
                        .append("_idx_fields WHERE word ")
                        .append(operator)
                        .append(" ? ");
            }
            sql.append(") ");

            if (cteCount < operators.size()) {
                sql.append(", ");
            }
        }

        sql.append("SELECT DISTINCT ")
                .append(search.getId())
                .append(", A.indexing_group_id, A.record_id ");
        sql.append("FROM ( ");

        sql.append("SELECT I.indexing_group_id, I.record_id FROM ( ");

        for (int i = 1; i <= cteCount; i++) {
            sql.append("SELECT * FROM Query_").append(i).append(CharPool.SPACE);

            if (i < cteCount) {
                sql.append(" UNION ");
            }
        }

        sql.append(") I WHERE I.record_id in ( ");
        for (int i = 1; i <= cteCount; i++) {
            sql.append("SELECT record_id FROM Query_").append(i).append(CharPool.SPACE);

            if (i < cteCount) {
                sql.append(" INTERSECT ");
            }
        }
        sql.append(") ");

        sql.append(") A INNER JOIN ").append(recordType).append("_records R ");
        sql.append("ON R.id = A.record_id ");
        sql.append("WHERE R.database = ? ");

        if (query.getMaterialType() != MaterialType.ALL) {
            sql.append("AND R.material = ? ");
        }

        if (query.isReservedOnly()) {
            sql.append(
                    "AND R.id in (SELECT DISTINCT record_id FROM reservations WHERE expires > localtimestamp) ");
        }

        return sql.toString();
    }

    private String createAdvancedSelectClause(SearchDTO search) {
        SearchQueryDTO query = search.getQuery();
        List<SearchTermDTO> searchTerms = query.getTerms();

        if (searchTerms == null || searchTerms.size() == 0) {
            return null;
        }

        StringBuilder sql = new StringBuilder();

        RecordType recordType = search.getRecordType();

        sql.append("INSERT INTO ").append(recordType).append("_search_results ");
        sql.append("(search_id, indexing_group_id, record_id) ");

        sql.append("SELECT DISTINCT ")
                .append(search.getId())
                .append(", 0 as indexing_group_id, R.id ");
        sql.append("FROM ").append(recordType).append("_records R ");
        sql.append("WHERE R.database = ? ");

        if (query.getMaterialType() != MaterialType.ALL) {
            sql.append(" AND R.material = ? ");
        }

        sql.append(" AND (");

        for (int i = 0; i < searchTerms.size(); i++) {
            SearchTermDTO searchTerm = searchTerms.get(i);

            SearchOperator operator = searchTerm.getOperator();

            if (operator != null) {
                if (i != 0) {
                    sql.append(operator.getSqlOperator());
                } else if (operator.equals(SearchOperator.AND_NOT)) {
                    sql.append(" NOT ");
                }
            }

            sql.append(CharPool.OPEN_PARENTHESIS);
            sql.append(this.createAdvancedFilterClause(searchTerm, search.getRecordType()));
            sql.append(CharPool.CLOSE_PARENTHESIS);
        }

        sql.append(CharPool.CLOSE_PARENTHESIS);

        return sql.toString();
    }

    private String createAdvancedFilterClause(SearchTermDTO searchTerm, RecordType recordType) {
        StringBuilder clause = new StringBuilder();
        String field = searchTerm.getField();
        Set<String> terms = searchTerm.getTerms();

        // See: populateAdvancedSearch();
        if (StringUtils.isNumeric(field)) {
            clause.append("R.id IN (SELECT record_id FROM (");

            if (field.equals("0")) {
                clause.append(
                        StringUtils.repeat(
                                "SELECT record_id, datafield FROM "
                                        + recordType
                                        + "_idx_fields WHERE word = ? ",
                                " INTERSECT ",
                                terms.size()));
            } else {
                clause.append(
                        StringUtils.repeat(
                                "SELECT record_id, datafield FROM "
                                        + recordType
                                        + "_idx_fields WHERE word = ? AND indexing_group_id = ? ",
                                " INTERSECT ",
                                terms.size()));
            }

            clause.append(") A ) ");
        } else if (field.equals("record_id")) {
            // Field is the record id
            clause.append(StringUtils.repeat("R.id = ? ", " OR ", terms.size()));

        } else if (field.equals("holding_id")
                || field.equals("holding_accession_number")
                || field.equals("holding_created")
                || field.equals("holding_modified")
                || field.equals("holding_label_never_printed")) {
            // Field is the holding id or the holding asset
            clause.append("R.id IN (SELECT record_id FROM biblio_holdings ");
            clause.append("WHERE database = ? AND ");

            switch (field) {
                case "holding_id" ->
                        clause.append(StringUtils.repeat("id = ? ", " OR ", terms.size()));
                case "holding_created", "holding_modified" -> {
                    clause.append(CharPool.OPEN_PARENTHESIS);
                    if (searchTerm.getStartDate() != null) {
                        clause.append(field.substring(8)).append(" >= ? ");
                    }
                    if ((searchTerm.getStartDate() != null) && (searchTerm.getEndDate() != null)) {
                        clause.append("AND ");
                    }
                    if (searchTerm.getEndDate() != null) {
                        clause.append(field.substring(8)).append(" < ? ");
                    }
                    clause.append(CharPool.CLOSE_PARENTHESIS);
                }
                case "holding_label_never_printed" -> clause.append("label_printed = false ");
                default ->
                        clause.append(
                                StringUtils.repeat(
                                        "accession_number ilike ? ", " OR ", terms.size()));
            }

            clause.append(CharPool.CLOSE_PARENTHESIS);

        } else if (field.equals("created") || field.equals("modified")) {
            // Field is a date
            clause.append(CharPool.OPEN_PARENTHESIS);

            if (searchTerm.getStartDate() != null) {
                clause.append("R.").append(field).append(" >= ? ");
            }

            if ((searchTerm.getStartDate() != null) && (searchTerm.getEndDate() != null)) {
                clause.append("AND ");
            }

            if (searchTerm.getEndDate() != null) {
                clause.append("R.").append(field).append(" < ? ");
            }

            clause.append(CharPool.CLOSE_PARENTHESIS);
        }

        return clause.toString();
    }
}
