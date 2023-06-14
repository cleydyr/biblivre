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

import biblivre.cataloging.enums.RecordDatabase;
import biblivre.cataloging.enums.RecordType;
import biblivre.cataloging.search.SearchDTO;
import biblivre.core.AbstractDAO;
import biblivre.core.DTOCollection;
import biblivre.core.PagingDTO;
import biblivre.core.enums.SearchMode;
import biblivre.core.exceptions.DAOException;
import biblivre.marc.MaterialType;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

public class RecordDAOImpl extends AbstractDAO implements RecordDAO {
    public static RecordDAOImpl getInstance() {
        return AbstractDAO.getInstance(RecordDAOImpl.class);
    }

    @Override
    public boolean save(RecordDTO dto) {
        int id = getNextSerial(dto.getRecordType() + "_records_id_seq");

        dto.setId(id);

        Connection con = null;

        try {
            con = this.getConnection();

            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO ").append(dto.getRecordType()).append("_records ");
            sql.append("(id, iso2709, material, database, created_by) ");
            sql.append("VALUES (?, ?, ?, ?, ?); ");

            PreparedStatement pst = con.prepareStatement(sql.toString());
            pst.setInt(1, dto.getId());
            pst.setString(2, dto.getUTF8Iso2709());
            pst.setString(3, dto.getMaterialType().toString());
            pst.setString(4, dto.getRecordDatabase().toString());
            pst.setInt(5, dto.getCreatedBy());

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            this.closeConnection(con);
        }
    }

    @Override
    public boolean update(RecordDTO dto) {
        Connection con = null;

        try {
            con = this.getConnection();

            StringBuilder sql = new StringBuilder();
            sql.append("UPDATE ").append(dto.getRecordType()).append("_records ");
            sql.append("SET iso2709 = ?, material = ?, modified = now(), modified_by = ? ");
            sql.append("WHERE id = ?;");

            PreparedStatement pst = con.prepareStatement(sql.toString());
            pst.setString(1, dto.getUTF8Iso2709());
            pst.setString(2, dto.getMaterialType().toString());
            pst.setInt(3, dto.getModifiedBy());
            pst.setInt(4, dto.getId());

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            this.closeConnection(con);
        }
    }

    @Override
    public boolean listContainsPrivateRecord(Set<Integer> ids, RecordType recordType) {
        Connection con = null;

        try {
            con = this.getConnection();
            con.setAutoCommit(false);

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT count(*) as total FROM ").append(recordType).append("_records ");
            sql.append("WHERE database = ? AND id IN (");
            sql.append(StringUtils.repeat("?", ", ", ids.size()));
            sql.append(");");

            int i = 1;
            PreparedStatement pst = con.prepareStatement(sql.toString());
            pst.setString(i++, RecordDatabase.PRIVATE.toString());

            for (Integer id : ids) {
                pst.setInt(i++, id);
            }

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }

            return false;
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            this.closeConnection(con);
        }
    }

    @Override
    public boolean moveRecords(
            Set<Integer> ids, int modifiedBy, RecordDatabase database, RecordType recordType) {
        Connection con = null;

        try {
            con = this.getConnection();
            con.setAutoCommit(false);

            StringBuilder sql = new StringBuilder();
            sql.append("UPDATE ").append(recordType).append("_records ");
            sql.append("SET database = ?, modified = now(), modified_by = ? ");
            sql.append("WHERE id IN (");
            sql.append(StringUtils.repeat("?", ", ", ids.size()));
            sql.append(");");

            int i = 1;
            PreparedStatement pst = con.prepareStatement(sql.toString());
            pst.setString(i++, database.toString());
            pst.setInt(i++, modifiedBy);

            for (Integer id : ids) {
                pst.setInt(i++, id);
            }

            pst.executeUpdate();

            if (recordType == RecordType.BIBLIO) {
                String holdingSQL =
                        """
                        UPDATE biblio_holdings SET database = B.database
                        FROM biblio_records B
                        WHERE biblio_holdings.record_id = B.id
                        AND biblio_holdings.database <> B.database""";

                con.createStatement().executeUpdate(holdingSQL);
            }

            con.commit();
            return true;
        } catch (Exception e) {
            this.rollback(con);
            throw new DAOException(e);
        } finally {
            this.closeConnection(con);
        }
    }

    @Override
    public boolean delete(RecordDTO dto) {
        Connection con = null;

        try {
            con = this.getConnection();

            StringBuilder sql = new StringBuilder();
            sql.append("DELETE FROM ").append(dto.getRecordType()).append("_records ");
            sql.append("WHERE id = ?;");

            PreparedStatement pst = con.prepareStatement(sql.toString());
            pst.setInt(1, dto.getId());

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            this.closeConnection(con);
        }
    }

    @Override
    public Integer count(SearchDTO search) {
        Connection con = null;

        boolean useDatabase = false;
        boolean useMaterialType = false;
        boolean reservedOnly = false;

        if (search != null && search.getQuery() != null) {
            useDatabase = search.getQuery().getDatabase() != null;
            useMaterialType = search.getQuery().getMaterialType() != MaterialType.ALL;
            reservedOnly = search.getQuery().isReservedOnly();
        }

        try {
            con = this.getConnection();

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT count(*) as total FROM ")
                    .append(search.getRecordType())
                    .append("_records WHERE 1 = 1 ");

            if (useDatabase) {
                sql.append("AND database = ? ");
            }

            if (useMaterialType) {
                sql.append("AND material = ? ");
            }

            if (reservedOnly) {
                sql.append(
                        "AND id in (SELECT DISTINCT record_id FROM reservations WHERE expires > localtimestamp) ");
            }

            PreparedStatement pst = con.prepareStatement(sql.toString());

            int index = 1;

            if (useDatabase && search != null) {
                pst.setString(index++, search.getQuery().getDatabase().toString());
            }

            if (useMaterialType && search != null) {
                pst.setString(index++, search.getQuery().getMaterialType().toString());
            }

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            this.closeConnection(con);
        }

        return 0;
    }

    @Override
    public Map<Integer, RecordDTO> map(Set<Integer> ids, RecordType recordType) {
        Map<Integer, RecordDTO> map = new HashMap<>();

        Connection con = null;
        try {
            con = this.getConnection();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM ").append(recordType).append("_records ");
            sql.append("WHERE id in (");
            sql.append(StringUtils.repeat("?", ", ", ids.size()));
            sql.append(");");

            PreparedStatement pst = con.prepareStatement(sql.toString());
            int index = 1;
            for (Integer id : ids) {
                pst.setInt(index++, id);
            }

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                RecordDTO dto = this.populateDTO(rs, recordType.getRecordClass());
                map.put(dto.getId(), dto);
            }
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            this.closeConnection(con);
        }

        return map;
    }

    @Override
    public List<RecordDTO> list(int offset, int limit, RecordType recordType) {
        return this.list(offset, limit, null, recordType);
    }

    @Override
    public List<RecordDTO> list(
            int offset, int limit, RecordDatabase database, RecordType recordType) {
        List<RecordDTO> list = new ArrayList<>();

        Connection con = null;
        try {
            con = this.getConnection();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM ").append(recordType).append("_records ");

            if (database != null) {
                sql.append("WHERE database = ? ");
            }

            sql.append("ORDER BY id ");
            sql.append("OFFSET ? LIMIT ?; ");

            PreparedStatement pst = con.prepareStatement(sql.toString());

            int index = 1;

            if (database != null) {
                pst.setString(index++, database.toString());
            }

            pst.setInt(index++, offset);
            pst.setInt(index++, limit);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                try {
                    list.add(this.populateDTO(rs, recordType.getRecordClass()));
                } catch (Exception e) {
                    this.logger.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            this.closeConnection(con);
        }

        return list;
    }

    @Override
    public List<RecordDTO> listByLetter(char letter, int order, RecordType recordType) {
        List<RecordDTO> list = new ArrayList<>();

        Connection con = null;
        try {
            con = this.getConnection();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM ").append(recordType).append("_records R ");
            sql.append("LEFT JOIN ").append(recordType).append("_idx_sort S ");
            sql.append("ON S.record_id = R.id AND S.indexing_group_id = ? ");
            sql.append("WHERE S.phrase ~ ? ORDER BY S.phrase NULLS LAST, R.id ASC;");

            PreparedStatement pst = con.prepareStatement(sql.toString());

            pst.setInt(1, order);
            if (CharUtils.isAsciiAlpha(letter)) {
                pst.setString(2, "^" + Character.toLowerCase(letter));
            } else {
                pst.setString(2, "^[^a-zA-Z]");
            }

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                try {
                    list.add(this.populateDTO(rs, recordType.getRecordClass()));
                } catch (Exception e) {
                    this.logger.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            this.closeConnection(con);
        }

        return list;
    }

    @Override
    public Map<Integer, Integer> countSearchResults(SearchDTO search) {
        Map<Integer, Integer> count = new HashMap<>();
        if (search == null) {
            return count;
        }

        if (search.getSearchMode() == SearchMode.LIST_ALL) {
            count.put(0, this.count(search));
            return count;
        }

        Connection con = null;
        try {
            con = this.getConnection();
            StringBuilder sql = new StringBuilder();

            sql.append("SELECT 0 as indexing_group_id, COUNT(DISTINCT record_id) as total FROM ");
            RecordType recordType = search.getRecordType();
            sql.append(recordType).append("_search_results ");
            sql.append("WHERE search_id = ? ");
            sql.append("UNION ");
            sql.append("SELECT indexing_group_id, COUNT(DISTINCT record_id) as total FROM ");
            sql.append(recordType).append("_search_results ");
            sql.append("WHERE search_id = ? and indexing_group_id <> 0 GROUP BY indexing_group_id");

            PreparedStatement pst = con.prepareStatement(sql.toString());
            pst.setInt(1, search.getId());
            pst.setInt(2, search.getId());

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                count.put(rs.getInt("indexing_group_id"), rs.getInt("total"));
            }

            return count;
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            this.closeConnection(con);
        }
    }

    @Override
    public List<RecordDTO> getSearchResults(SearchDTO search) {
        List<RecordDTO> list = new ArrayList<>();

        if (search == null) {
            return list;
        }

        PagingDTO paging = search.getPaging();

        if (paging == null) {
            return list;
        }

        boolean useSearchResult = (search.getSearchMode() != SearchMode.LIST_ALL);
        boolean useIndexingGroup = (search.getIndexingGroup() != 0);
        boolean useMaterialType = (search.getQuery().getMaterialType() != MaterialType.ALL);
        boolean useLimit = (paging.getRecordLimit() > 0);
        boolean reservedOnly = search.getQuery().isReservedOnly();

        Connection con = null;
        try {
            con = this.getConnection();
            StringBuilder sql = new StringBuilder();

            sql.append("SELECT R.*, trim(substr(S.phrase, ignore_chars_count + 1)) as sort FROM ");

            RecordType recordType = search.getRecordType();

            if (useSearchResult) {

                sql.append(recordType).append("_records R ");
                sql.append("INNER JOIN ( ");
                sql.append("SELECT DISTINCT record_id FROM ")
                        .append(recordType)
                        .append("_search_results ");
                sql.append("WHERE search_id = ? ");

                if (useIndexingGroup) {
                    sql.append("AND indexing_group_id = ? ");
                }

                sql.append("ORDER BY record_id DESC ");

                if (useLimit) {
                    sql.append("LIMIT ? ");
                }

                sql.append(") SR ON SR.record_id = R.id ");
            } else {
                sql.append("(");
                sql.append("SELECT * FROM ").append(recordType).append("_records ");
                sql.append("WHERE database = ? ");

                if (useMaterialType) {
                    sql.append("AND material = ? ");
                }

                if (reservedOnly) {
                    sql.append(
                            "AND id in (SELECT DISTINCT record_id FROM reservations WHERE expires > localtimestamp) ");
                }

                sql.append("ORDER BY id DESC ");

                if (useLimit) {
                    sql.append("LIMIT ? ");
                }

                sql.append(") R ");
            }

            sql.append("LEFT JOIN ").append(recordType).append("_idx_sort S ");
            sql.append("ON S.record_id = R.id AND S.indexing_group_id = ? ");

            sql.append("ORDER BY sort NULLS LAST, R.id ASC OFFSET ? LIMIT ?;");

            int index = 1;

            PreparedStatement pst = con.prepareStatement(sql.toString());

            if (useSearchResult) {
                pst.setInt(index++, search.getId());

                if (useIndexingGroup) {
                    pst.setInt(index++, search.getIndexingGroup());
                }
            } else {
                pst.setString(index++, search.getQuery().getDatabase().toString());

                if (useMaterialType) {
                    pst.setString(index++, search.getQuery().getMaterialType().toString());
                }
            }

            if (useLimit) {
                pst.setInt(index++, search.getRecordLimit());
            }

            pst.setInt(index++, search.getSort());

            pst.setInt(index++, paging.getRecordOffset());
            pst.setInt(index++, paging.getRecordsPerPage());

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                list.add(this.populateDTO(rs, recordType.getRecordClass()));
            }

            return list;
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            this.closeConnection(con);
        }
    }

    @Override
    public List<String> phraseAutocomplete(
            String datafield,
            String subfield,
            String[] terms,
            int limit,
            boolean startsWith,
            RecordType recordType) {
        List<String> list = new ArrayList<>();

        if (terms == null || terms.length == 0) {
            return list;
        }

        Connection con = null;
        try {
            con = this.getConnection();
            StringBuilder sql = new StringBuilder();

            sql.append("SELECT phrase FROM ").append(recordType).append("_idx_autocomplete ");
            sql.append("WHERE datafield = ? and subfield = ? and word like ? ");

            if (startsWith) {
                sql.append(" and phrase ilike ?");
            } else {
                sql.append(" and phrase not ilike ?");
            }

            StringBuilder completeSQL = new StringBuilder();
            completeSQL.append("SELECT DISTINCT phrase FROM ( ");
            completeSQL.append(StringUtils.repeat(sql.toString(), " INTERSECT ", terms.length));
            completeSQL.append(" ) A ORDER BY A.phrase ASC LIMIT ?");

            PreparedStatement pst = con.prepareStatement(completeSQL.toString());

            int index = 1;

            for (String term : terms) {
                pst.setString(index++, datafield);
                pst.setString(index++, subfield);
                pst.setString(index++, term + "%");
                pst.setString(index++, terms[0] + "%");
            }

            pst.setInt(index++, limit);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                list.add(rs.getString("phrase"));
            }

            return list;
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            this.closeConnection(con);
        }
    }

    @Override
    public DTOCollection<AutocompleteDTO> recordAutocomplete(
            String datafield,
            String subfield,
            String[] terms,
            int limit,
            boolean startsWith,
            RecordType recordType) {
        DTOCollection<AutocompleteDTO> list = new DTOCollection<>();

        if (terms == null || terms.length == 0) {
            return list;
        }

        Connection con = null;
        try {
            con = this.getConnection();
            StringBuilder sql = new StringBuilder();

            sql.append("SELECT record_id, phrase FROM ")
                    .append(recordType)
                    .append("_idx_autocomplete ");
            sql.append("WHERE datafield = ? and subfield = ? and word like ? ");

            if (startsWith) {
                sql.append(" and phrase ilike ?");
            } else {
                sql.append(" and phrase not ilike ?");
            }

            StringBuilder completeSQL = new StringBuilder();
            completeSQL.append("SELECT DISTINCT A.record_id, A.phrase, R.iso2709 FROM ( ");
            completeSQL.append(StringUtils.repeat(sql.toString(), " INTERSECT ", terms.length));
            completeSQL.append(" ) A ");
            completeSQL
                    .append("INNER JOIN ")
                    .append(recordType)
                    .append("_records R ON A.record_id = R.id ");
            completeSQL.append("ORDER BY A.phrase ASC LIMIT ?");

            PreparedStatement pst = con.prepareStatement(completeSQL.toString());

            int index = 1;

            for (String term : terms) {
                pst.setString(index++, datafield);
                pst.setString(index++, subfield);
                pst.setString(index++, term + "%");
                pst.setString(index++, terms[0] + "%");
            }

            pst.setInt(index++, limit);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                list.add(this.populateAutocompleteDTO(rs));
            }

            return list;
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            this.closeConnection(con);
        }
    }

    protected RecordDTO populateDTO(ResultSet rs, Class<? extends RecordDTO> recordClass)
            throws Exception {
        Constructor<? extends RecordDTO> constructor = recordClass.getConstructor();

        RecordDTO dto = constructor.newInstance();

        dto.setIso2709(rs.getBytes("iso2709"));
        dto.setCreated(rs.getTimestamp("created"));
        dto.setCreatedBy(rs.getInt("created_by"));
        dto.setModified(rs.getTimestamp("modified"));
        dto.setModifiedBy(rs.getInt("modified_by"));
        dto.setMaterialType(rs.getString("material"));
        dto.setRecordDatabase(rs.getString("database"));
        dto.setId(rs.getInt("id"));

        return dto;
    }

    protected AutocompleteDTO populateAutocompleteDTO(ResultSet rs) throws SQLException {
        AutocompleteDTO dto = new AutocompleteDTO();

        RecordDTO rdto = new RecordDTO();

        rdto.setIso2709(rs.getBytes("iso2709"));

        dto.setPhrase(rs.getString("phrase"));
        dto.setRecord(rdto);
        dto.setRecordId(rdto.getId());

        return dto;
    }
}
