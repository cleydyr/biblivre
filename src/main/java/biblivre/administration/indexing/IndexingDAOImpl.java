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
package biblivre.administration.indexing;

import biblivre.cataloging.AutocompleteDTO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.enums.RecordType;
import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;
import biblivre.core.utils.TextUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class IndexingDAOImpl extends AbstractDAO implements IndexingDAO {

    @Override
    public Integer countIndexed(RecordType recordType) {
        try (Connection con = datasource.getConnection()) {
            String sql =
                    "SELECT count(DISTINCT record_id) as total FROM " + recordType + "_idx_sort";

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }

        return 0;
    }

    @Override
    public void clearIndexes(RecordType recordType) {
        try (Connection con = datasource.getConnection()) {

            String sql = "TRUNCATE TABLE " + recordType + "_idx_fields";
            String sql2 = "TRUNCATE TABLE " + recordType + "_idx_sort";
            String sql3 =
                    "DELETE FROM " + recordType + "_idx_autocomplete WHERE record_id is not null";

            Statement st = con.createStatement();
            st.execute(sql);
            st.execute(sql2);
            st.execute(sql3);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void insertIndexes(RecordType recordType, List<IndexingDTO> indexes) {
        int total = 0;
        for (IndexingDTO index : indexes) {
            total += index.getCount();
        }

        if (total == 0) {
            return;
        }

        try (Connection con = datasource.getConnection()) {
            String sql =
                    "INSERT INTO "
                            + recordType
                            + "_idx_fields "
                            + "(record_id, indexing_group_id, word, datafield) VALUES (?, ?, ?, ?);";

            PreparedStatement pst = con.prepareStatement(sql);

            for (IndexingDTO index : indexes) {
                final int recordId = index.getRecordId();
                final int groupId = index.getIndexingGroupId();

                Map<Integer, Set<String>> wordsGroups = index.getWords();
                for (Entry<Integer, Set<String>> entry : wordsGroups.entrySet()) {
                    Set<String> words = entry.getValue();

                    for (String word : words) {
                        pst.setInt(1, recordId);
                        pst.setInt(2, groupId);
                        pst.setString(3, word);
                        pst.setInt(4, entry.getKey());
                        pst.addBatch();
                    }
                }
            }

            pst.executeBatch();
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void insertSortIndexes(RecordType recordType, List<IndexingDTO> sortIndexes) {
        int total = sortIndexes.size();

        if (total == 0) {
            return;
        }

        try (Connection con = datasource.getConnection()) {
            String sql =
                    "INSERT INTO "
                            + recordType
                            + "_idx_sort "
                            + "(record_id, indexing_group_id, phrase, ignore_chars_count) VALUES (?, ?, ?, ?);";

            PreparedStatement pst = con.prepareStatement(sql);

            for (IndexingDTO sortIndex : sortIndexes) {
                pst.setInt(1, sortIndex.getRecordId());
                pst.setInt(2, sortIndex.getIndexingGroupId());
                pst.setString(3, sortIndex.getPhrase());
                pst.setInt(4, sortIndex.getIgnoreCharsCount());
                pst.addBatch();
            }

            pst.executeBatch();
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void insertAutocompleteIndexes(
            RecordType recordType, List<AutocompleteDTO> autocompleteIndexes) {
        if (autocompleteIndexes.size() == 0) {
            return;
        }

        boolean batched = false;

        try (Connection con = datasource.getConnection()) {
            String sql =
                    "INSERT INTO "
                            + recordType
                            + "_idx_autocomplete "
                            + "(datafield, subfield, word, phrase, record_id) VALUES (?, ?, ?, ?, ?);";

            PreparedStatement pst = con.prepareStatement(sql);

            for (AutocompleteDTO index : autocompleteIndexes) {
                final Integer recordId = index.getRecordId();
                final String datafield = index.getDatafield();
                final String subfield = index.getSubfield();
                final String phrase = index.getPhrase();

                for (String word : TextUtils.prepareAutocomplete(phrase)) {
                    if (StringUtils.isBlank(word) || word.length() < 2) {
                        continue;
                    }

                    pst.setString(1, datafield);
                    pst.setString(2, subfield);
                    pst.setString(3, word);
                    pst.setString(4, phrase);
                    pst.setInt(5, recordId);

                    pst.addBatch();
                    batched = true;
                }
            }

            if (batched) {
                pst.executeBatch();
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void reindexAutocompleteFixedTable(
            RecordType recordType, String datafield, String subfield, List<String> phrases) {
        boolean batched = false;

        try (Connection con = datasource.getConnection()) {
            con.setAutoCommit(false);

            StringBuilder sql = new StringBuilder();

            sql.append("DELETE FROM ").append(recordType).append("_idx_autocomplete ");
            sql.append("WHERE datafield = ? and subfield = ? and record_id is null;");

            PreparedStatement pst = con.prepareStatement(sql.toString());
            pst.setString(1, datafield);
            pst.setString(2, subfield);

            pst.executeUpdate();

            sql = new StringBuilder();
            sql.append("INSERT INTO ").append(recordType).append("_idx_autocomplete ");
            sql.append("(datafield, subfield, word, phrase, record_id) VALUES (?, ?, ?, ?, null);");

            pst = con.prepareStatement(sql.toString());

            for (String phrase : phrases) {
                for (String word : TextUtils.prepareAutocomplete(phrase)) {
                    if (StringUtils.isBlank(word) || word.length() < 2) {
                        continue;
                    }

                    pst.setString(1, datafield);
                    pst.setString(2, subfield);
                    pst.setString(3, word);
                    pst.setString(4, phrase);

                    pst.addBatch();
                    batched = true;
                }
            }

            if (batched) {
                pst.executeBatch();
            }

            con.commit();
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public boolean deleteIndexes(RecordType recordType, RecordDTO dto) {
        return withTransactionContext(
                con -> {
                    StringBuilder sql = new StringBuilder();
                    sql.append("DELETE FROM ").append(recordType).append("_idx_fields ");
                    sql.append("WHERE record_id = ?;");

                    PreparedStatement pst = con.prepareStatement(sql.toString());
                    pst.setInt(1, dto.getId());
                    pst.executeUpdate();

                    sql = new StringBuilder();
                    sql.append("DELETE FROM ").append(recordType).append("_idx_sort ");
                    sql.append("WHERE record_id = ?;");

                    pst = con.prepareStatement(sql.toString());
                    pst.setInt(1, dto.getId());
                    pst.executeUpdate();

                    sql = new StringBuilder();
                    sql.append("DELETE FROM ").append(recordType).append("_idx_autocomplete ");
                    sql.append("WHERE record_id = ?;");

                    pst = con.prepareStatement(sql.toString());
                    pst.setInt(1, dto.getId());
                    pst.executeUpdate();

                    return true;
                });
    }

    @Override
    public void reindexDatabase(RecordType recordType) {
        try (Connection con = datasource.getConnection()) {

            Statement st = con.createStatement();

            st.execute("REINDEX TABLE " + recordType + "_idx_fields");
            st.execute("REINDEX TABLE " + recordType + "_idx_sort");
            st.execute("ANALYZE " + recordType + "_idx_fields");
            st.execute("ANALYZE " + recordType + "_idx_sort");
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<String> searchExactTerms(
            RecordType recordType, int indexingGroupId, List<String> terms) {
        List<String> list = new ArrayList<>();

        try (Connection con = datasource.getConnection()) {

            String sql =
                    "SELECT phrase FROM "
                            + recordType
                            + "_idx_sort WHERE indexing_group_id = ? AND phrase in ("
                            + StringUtils.repeat("?", ", ", terms.size())
                            + ");";

            PreparedStatement pst = con.prepareStatement(sql);

            int index = 1;
            pst.setInt(index++, indexingGroupId);

            for (String term : terms) {
                pst.setString(index++, term.toLowerCase());
            }

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                list.add(rs.getString("phrase"));
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }

        return list;
    }
}
