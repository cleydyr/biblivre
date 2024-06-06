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

import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;
import biblivre.core.utils.CalendarUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LendingFineDAOImpl extends AbstractDAO implements LendingFineDAO {

    @Override
    public void insert(LendingFineDTO fine) {
        try (Connection con = datasource.getConnection()) {

            String sql =
                    "INSERT INTO lending_fines "
                            + "(user_id, lending_id, fine_value, payment_date, created_by) "
                            + "VALUES (?, ?, ?, ?, ?);";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, fine.getUserId());
            pst.setInt(2, fine.getLendingId());
            pst.setFloat(3, fine.getValue());
            if (fine.getPayment() != null) {
                pst.setDate(4, CalendarUtils.toSqlDate(fine.getPayment()));
            } else {
                pst.setNull(4, Types.DATE);
            }
            pst.setInt(5, fine.getCreatedBy());

            pst.executeUpdate();

        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public LendingFineDTO get(Integer lendingFineId) {
        try (Connection con = datasource.getConnection()) {

            String sql = "SELECT * FROM lending_fines WHERE id = ?;";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, lendingFineId);

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
    public LendingFineDTO getByLendingId(Integer lendingId) {
        try (Connection con = datasource.getConnection()) {

            String sql = "SELECT * FROM lending_fines WHERE lending_id = ?;";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, lendingId);

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
    public List<LendingFineDTO> list(UserDTO user, boolean pendingOnly) {
        List<LendingFineDTO> list = new ArrayList<>();
        try (Connection con = datasource.getConnection()) {

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM lending_fines WHERE user_id = ? ");
            if (pendingOnly) {
                sql.append("AND payment is null ");
            }
            sql.append("ORDER BY id DESC;");

            PreparedStatement pst = con.prepareStatement(sql.toString());
            pst.setInt(1, user.getId());

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(populateDTO(rs));
            }

        } catch (Exception e) {
            throw new DAOException(e);
        }
        return list;
    }

    @Override
    public boolean update(LendingFineDTO fine) {
        try (Connection con = datasource.getConnection()) {

            String sql =
                    "UPDATE lending_fines SET payment_date = now(), "
                            + "fine_value = ? WHERE id = ?; ";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setFloat(1, fine.getValue());
            pst.setInt(2, fine.getId());

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public boolean hasPendingFine(int userID) {
        String sql =
                """
                SELECT EXISTS (
                        SELECT 1 FROM lending_fines
                        WHERE user_id = ? AND payment_date IS NULL
                    )
                """;

        try (Connection con = datasource.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, userID);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getBoolean(1);
            }

        } catch (Exception e) {
            throw new DAOException(e);
        }

        return false;
    }

    private LendingFineDTO populateDTO(ResultSet rs) throws SQLException {
        LendingFineDTO dto = new LendingFineDTO();

        dto.setId(rs.getInt("id"));
        dto.setLendingId(rs.getInt("lending_id"));
        dto.setUserId(rs.getInt("user_id"));
        dto.setValue(rs.getFloat("fine_value"));
        dto.setPayment(rs.getTimestamp("payment_date"));

        dto.setCreated(rs.getTimestamp("created"));
        dto.setCreatedBy(rs.getInt("created_by"));

        return dto;
    }
}
