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
package biblivre.circulation.lending;

import biblivre.cataloging.holding.HoldingDTO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;
import biblivre.core.utils.CalendarUtils;
import jakarta.annotation.Nonnull;
import java.sql.*;
import java.util.*;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class LendingDAOImpl extends AbstractDAO implements LendingDAO {

    @Override
    public LendingDTO get(Integer id) {
        try (Connection con = datasource.getConnection()) {
            String sql = "SELECT * FROM lendings WHERE id = ?;";
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
    public LendingDTO getCurrentLending(HoldingDTO holding) {
        try (Connection con = datasource.getConnection()) {

            String sql =
                    "SELECT * FROM lendings WHERE "
                            + "holding_id = ? AND return_date IS null ORDER BY id DESC;";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, holding.getId());

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return this.populateDTO(rs);
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }

        return null;
    }

    @Override
    public Map<Integer, LendingDTO> getCurrentLendingMap(Set<Integer> ids) {
        Map<Integer, LendingDTO> map = new LinkedHashMap<>();

        try (Connection con = datasource.getConnection()) {

            String sql =
                    "SELECT * FROM lendings WHERE "
                            + "holding_id in ("
                            + StringUtils.repeat("?", ", ", ids.size())
                            + ") AND return_date IS null ORDER BY id DESC;";

            PreparedStatement pst = con.prepareStatement(sql);
            int index = 1;
            for (Integer id : ids) {
                pst.setInt(index++, id);
            }

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                map.put(rs.getInt("holding_id"), this.populateDTO(rs));
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }

        return map;
    }

    @Override
    public List<LendingDTO> listHistory(HoldingDTO holding) {
        List<LendingDTO> list = new ArrayList<>();
        try (Connection con = datasource.getConnection()) {

            String sql =
                    "SELECT * FROM lendings WHERE holding_id = ? "
                            + "AND return_date IS NOT null ORDER BY id DESC;";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, holding.getId());

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(this.populateDTO(rs));
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }
        return list;
    }

    @Override
    public List<LendingDTO> listLendings(UserDTO user) {
        return this.list(user, false);
    }

    @Override
    public List<LendingDTO> listHistory(UserDTO user) {
        return this.list(user, true);
    }

    private List<LendingDTO> list(UserDTO user, boolean history) {
        List<LendingDTO> list = new ArrayList<>();
        try (Connection con = datasource.getConnection()) {

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM lendings WHERE user_id = ? ");
            sql.append("AND return_date IS ");
            if (history) {
                sql.append("NOT ");
            }
            sql.append("null ORDER BY id ASC;");

            PreparedStatement ppst = con.prepareStatement(sql.toString());
            ppst.setInt(1, user.getId());

            ResultSet rs = ppst.executeQuery();
            while (rs.next()) {
                list.add(this.populateDTO(rs));
            }

        } catch (Exception e) {
            throw new DAOException(e);
        }
        return list;
    }

    @Override
    public Integer countLendings(UserDTO user) {
        return this.count(user, false);
    }

    @Override
    public Integer countHistory(UserDTO user) {
        return this.count(user, true);
    }

    private Integer count(UserDTO user, boolean history) {
        try (Connection con = datasource.getConnection()) {

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT COUNT(*) AS total FROM lendings WHERE user_id = ? ");
            sql.append("AND return_date IS ");
            if (history) {
                sql.append("NOT ");
            }
            sql.append("null ORDER BY id DESC;");

            PreparedStatement ppst = con.prepareStatement(sql.toString());
            ppst.setInt(1, user.getId());

            ResultSet rs = ppst.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (Exception e) {
            throw new DAOException(e);
        }
        return 0;
    }

    @Override
    public List<LendingDTO> listByRecordId(int recordId) {
        List<LendingDTO> list = new ArrayList<>();
        try (Connection con = datasource.getConnection()) {

            String sql =
                    "SELECT * FROM lendings l "
                            + "INNER JOIN biblio_holdings h "
                            + "ON l.holding_id = h.id "
                            + "WHERE h.record_id = ?;";

            PreparedStatement ppst = con.prepareStatement(sql);
            ppst.setInt(1, recordId);

            ResultSet rs = ppst.executeQuery();
            while (rs.next()) {
                list.add(this.populateDTO(rs));
            }

        } catch (Exception e) {
            throw new DAOException(e);
        }
        return list;
    }

    @Override
    public List<LendingDTO> listLendings(int offset, int limit) {
        List<LendingDTO> list = new ArrayList<>();
        try (Connection con = datasource.getConnection()) {
            String sql =
                    "SELECT * FROM lendings "
                            + "WHERE return_date IS null "
                            + "ORDER BY return_date ASC "
                            + "LIMIT ? OFFSET ?;";
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, limit);
            pst.setInt(2, offset);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                list.add(this.populateDTO(rs));
            }

        } catch (Exception e) {
            throw new DAOException(e);
        }
        return list;
    }

    @Override
    public Integer countLendings() {
        try (Connection con = datasource.getConnection()) {

            String countSql =
                    "SELECT count(*) as total FROM lendings " + "WHERE return_date IS null;";
            PreparedStatement pstCount = con.prepareStatement(countSql);

            ResultSet rsCount = pstCount.executeQuery();

            int total = 0;
            if (rsCount.next()) {
                total = rsCount.getInt("total");
            }

            return total;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public Integer getCurrentLendingsCount(UserDTO user) {
        try (Connection con = datasource.getConnection()) {

            String sql =
                    "SELECT COUNT(*) FROM lendings "
                            + "WHERE user_id = ? "
                            + "AND return_date IS null;";

            PreparedStatement ppst = con.prepareStatement(sql);
            ppst.setInt(1, user.getId());

            ResultSet rs = ppst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            throw new DAOException(e);
        }
        return 0;
    }

    @Override
    public boolean doLend(LendingDTO lending) {
        try (Connection con = datasource.getConnection()) {
            return this.doLend(lending, con);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public boolean doLend(LendingDTO lending, Connection con) {
        try {
            String sql =
                    "INSERT INTO lendings (holding_id, user_id, previous_lending_id, "
                            + "expected_return_date, created_by) VALUES (?, ?, ?, ?, ?) ";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, lending.getHoldingId());
            pst.setInt(2, lending.getUserId());
            if (lending.getPreviousLendingId() != null) {
                pst.setInt(3, lending.getPreviousLendingId());
            } else {
                pst.setNull(3, Types.INTEGER);
            }
            pst.setTimestamp(4, CalendarUtils.toSqlTimestamp(lending.getExpectedReturnDate()));
            pst.setInt(5, lending.getCreatedBy());

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void doReturn(int lendingId) {
        try (Connection con = datasource.getConnection()) {
            this.doReturn(lendingId, con);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public boolean doReturn(int lendingId, @Nonnull Connection con) {
        try {
            String sql = "UPDATE lendings " + "SET return_date = now() " + "WHERE id = ?;";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, lendingId);

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public boolean doRenew(int lendingId, Date expectedReturnDate, int createdBy) {
        return withTransactionContext(
                con -> {
                    this.doReturn(lendingId, con);

                    LendingDTO oldLending = this.get(lendingId);
                    oldLending.setPreviousLendingId(lendingId);
                    oldLending.setExpectedReturnDate(expectedReturnDate);
                    oldLending.setCreatedBy(createdBy);
                    this.doLend(oldLending, con);

                    return true;
                });
    }

    @Override
    public Integer countLentHoldings(int recordId) {
        try (Connection con = datasource.getConnection()) {

            String sql =
                    "SELECT COUNT(*) FROM lendings L INNER JOIN biblio_holdings H "
                            + "ON L.holding_id = H.id "
                            + "WHERE H.record_id = ? AND H.availability = 'available' AND L.return_date is NULL;";

            PreparedStatement ppst = con.prepareStatement(sql);
            ppst.setInt(1, recordId);

            ResultSet rs = ppst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            throw new DAOException(e);
        }
        return null;
    }

    @Override
    public LendingDTO getLatest(int holdingSerial, int userId) {
        LendingDTO dto = null;
        try (Connection con = datasource.getConnection()) {

            String sql =
                    "SELECT * FROM lendings "
                            + "WHERE holding_id = ? AND user_id = ?"
                            + "ORDER BY id DESC LIMIT 1;";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, holdingSerial);
            pst.setInt(2, userId);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                dto = this.populateDTO(rs);
            }

        } catch (Exception e) {
            throw new DAOException(e);
        }
        return dto;
    }

    @Override
    public boolean hasLateLendings(int userId) {
        String sql =
                """
                SELECT EXISTS (
                        SELECT 1 FROM lendings
                        WHERE user_id = ? AND return_date IS NULL
                        AND expected_return_date < now()
                    )
                """;

        try (Connection con = datasource.getConnection();
                PreparedStatement preparedStatement = con.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);

            ResultSet rs = preparedStatement.executeQuery();

            if (!rs.next()) {
                throw new RuntimeException("Should never happen");
            }

            return rs.getBoolean(1);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    private LendingDTO populateDTO(ResultSet rs) throws SQLException {
        LendingDTO dto = new LendingDTO();

        dto.setId(rs.getInt("id"));
        dto.setHoldingId(rs.getInt("holding_id"));
        dto.setUserId(rs.getInt("user_id"));
        dto.setPreviousLendingId(rs.getInt("previous_lending_id"));
        dto.setExpectedReturnDate(rs.getTimestamp("expected_return_date"));
        dto.setReturnDate(rs.getTimestamp("return_date"));

        dto.setCreated(rs.getTimestamp("created"));
        dto.setCreatedBy(rs.getInt("created_by"));

        return dto;
    }
}
