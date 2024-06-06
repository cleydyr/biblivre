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
package biblivre.circulation.reservation;

import biblivre.cataloging.RecordDTO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;
import biblivre.core.utils.CalendarUtils;
import java.sql.*;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class ReservationDAOImpl extends AbstractDAO implements ReservationDAO {

    @Override
    public ReservationDTO get(Integer id) {
        try (Connection con = datasource.getConnection()) {
            String sql = "SELECT * FROM reservations WHERE id = ?;";

            PreparedStatement ppst = con.prepareStatement(sql);
            ppst.setInt(1, id);

            ResultSet rs = ppst.executeQuery();
            if (rs.next()) {
                return this.populateDTO(rs);
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }
        return null;
    }

    @Override
    public List<ReservationDTO> list(int defaultSortableGroupId) {
        return this.list(null, null, defaultSortableGroupId);
    }

    @Override
    public List<ReservationDTO> list(UserDTO user, RecordDTO record, int defaultSortableGroupId) {
        List<ReservationDTO> list = new ArrayList<>();
        try (Connection con = datasource.getConnection()) {
            StringBuilder sql = getStringBuilder(user, record);

            PreparedStatement pst = con.prepareStatement(sql.toString());

            int index = 1;
            pst.setInt(index++, defaultSortableGroupId);

            if (user != null) {
                pst.setInt(index++, user.getId());
            }

            if (record != null) {
                pst.setInt(index, record.getId());
            }

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(this.populateDTO(rs));
            }

        } catch (Exception e) {
            throw new DAOException(e);
        }
        return list;
    }

    private static StringBuilder getStringBuilder(UserDTO user, RecordDTO record) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT R.* FROM reservations R INNER JOIN biblio_idx_sort S ");
        sql.append("ON S.record_id = R.record_id WHERE R.expires > localtimestamp ");
        sql.append("AND S.indexing_group_id = ? ");

        if (user != null) {
            sql.append("AND R.user_id = ? ");
        }

        if (record != null) {
            sql.append("AND R.record_id = ? ");
        }

        sql.append("ORDER BY S.phrase ASC;");
        return sql;
    }

    @Override
    public int count() {
        return this.count(null, null);
    }

    @Override
    public int count(UserDTO user, RecordDTO record) {
        try (Connection con = datasource.getConnection()) {
            StringBuilder sql = new StringBuilder();
            sql.append(
                    "SELECT count(*) as total FROM reservations WHERE expires > localtimestamp ");

            if (user != null) {
                sql.append("AND user_id = ? ");
            }

            if (record != null) {
                sql.append("AND record_id = ? ");
            }

            PreparedStatement pst = con.prepareStatement(sql.toString());

            int index = 1;

            if (user != null) {
                pst.setInt(index++, user.getId());
            }

            if (record != null) {
                pst.setInt(index, record.getId());
            }

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }

        return 0;
    }

    @Override
    public boolean deleteExpired() {
        try (Connection con = datasource.getConnection()) {

            String sql = "DELETE FROM reservations WHERE expires < localtimestamp;";

            Statement st = con.createStatement();

            return st.executeUpdate(sql) > 0;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null) {
            return false;
        }

        try (Connection con = datasource.getConnection()) {
            String sql = "DELETE FROM reservations WHERE id = ?;";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, id);

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public boolean delete(Integer userId, Integer recordId) {
        if (userId == null || recordId == null) {
            return false;
        }

        try (Connection con = datasource.getConnection()) {
            String sql =
                    "DELETE FROM reservations WHERE id IN "
                            + "(SELECT id FROM reservations WHERE user_id = ? AND record_id = ? AND expires > localtimestamp "
                            + "ORDER BY expires ASC LIMIT 1);"; // Users can reserve more than one
            // copy of
            // each record

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, userId);
            pst.setInt(2, recordId);

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public int insert(ReservationDTO dto) {
        try (Connection con = datasource.getConnection()) {
            String sql =
                    "INSERT INTO reservations (record_id, user_id, expires, created_by) "
                            + "VALUES (?, ?, ?, ?) ";

            PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setInt(1, dto.getRecordId());
            pst.setInt(2, dto.getUserId());
            pst.setTimestamp(3, CalendarUtils.toSqlTimestamp(dto.getExpires()));
            pst.setInt(4, dto.getCreatedBy());

            pst.executeUpdate();

            ResultSet keys = pst.getGeneratedKeys();
            if (keys.next()) {
                dto.setId(keys.getInt(1));
            }

            return dto.getId();
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    private ReservationDTO populateDTO(ResultSet rs) throws SQLException {
        ReservationDTO dto = new ReservationDTO();

        dto.setId(rs.getInt("id"));
        dto.setRecordId(rs.getInt("record_id"));
        dto.setUserId(rs.getInt("user_id"));
        dto.setExpires(rs.getTimestamp("expires"));

        dto.setCreated(rs.getTimestamp("created"));
        dto.setCreatedBy(rs.getInt("created_by"));

        return dto;
    }

    @Override
    public Map<Integer, List<ReservationDTO>> getReservationsMap(Set<Integer> recordIds) {
        Map<Integer, List<ReservationDTO>> map = new LinkedHashMap<>();

        try (Connection con = datasource.getConnection()) {
            String sql =
                    "SELECT * FROM reservations WHERE "
                            + "record_id in ("
                            + StringUtils.repeat("?", ", ", recordIds.size())
                            + ") AND expires > localtimestamp ORDER BY created ASC;";

            PreparedStatement pst = con.prepareStatement(sql);
            int index = 1;
            for (Integer id : recordIds) {
                pst.setInt(index++, id);
            }

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Integer recordId = rs.getInt("record_id");
                List<ReservationDTO> reservations =
                        map.computeIfAbsent(recordId, k -> new ArrayList<>());
                reservations.add(this.populateDTO(rs));
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }

        return map;
    }
}
