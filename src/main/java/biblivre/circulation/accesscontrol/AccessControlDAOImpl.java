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
package biblivre.circulation.accesscontrol;

import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.springframework.stereotype.Service;

@Service
public class AccessControlDAOImpl extends AbstractDAO implements AccessControlDAO {

    @Override
    public boolean save(AccessControlDTO dto) {
        try (Connection con = datasource.getConnection()) {

            String sql =
                    "INSERT INTO access_control "
                            + "(access_card_id, user_id, created_by) "
                            + "VALUES (?, ?, ?);";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, dto.getAccessCardId());
            pst.setInt(2, dto.getUserId());
            pst.setInt(3, dto.getCreatedBy());

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public boolean update(AccessControlDTO dto) {
        try (Connection con = datasource.getConnection()) {
            String sql =
                    "UPDATE access_control "
                            + "SET departure_time = now(), "
                            + "modified = now(), "
                            + "modified_by = ? "
                            + "WHERE id = ?;";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, dto.getModifiedBy());
            pst.setInt(2, dto.getId());

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public AccessControlDTO getByCardId(Integer cardId) {
        try (Connection con = datasource.getConnection()) {
            String sql =
                    "SELECT * FROM access_control "
                            + "WHERE access_card_id = ? AND "
                            + "departure_time is null;";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, cardId);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return this.populateDto(rs);
            }

        } catch (Exception e) {
            throw new DAOException(e);
        }

        return null;
    }

    @Override
    public AccessControlDTO getByUserId(Integer userId) {
        try (Connection con = datasource.getConnection()) {
            String sql =
                    "SELECT * FROM access_control "
                            + "WHERE user_id = ? and "
                            + "departure_time is null;";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, userId);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return this.populateDto(rs);
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }
        return null;
    }

    private AccessControlDTO populateDto(ResultSet rs) throws Exception {
        AccessControlDTO dto = new AccessControlDTO();
        dto.setId(rs.getInt("id"));
        dto.setAccessCardId(rs.getInt("access_card_id"));
        dto.setUserId(rs.getInt("user_id"));
        dto.setArrivalTime(rs.getTimestamp("arrival_time"));
        dto.setDepartureTime(rs.getTimestamp("departure_time"));
        dto.setCreated(rs.getTimestamp("created"));
        dto.setCreatedBy(rs.getInt("created_by"));
        dto.setModified(rs.getTimestamp("modified"));
        dto.setModifiedBy(rs.getInt("modified_by"));
        return dto;
    }
}
