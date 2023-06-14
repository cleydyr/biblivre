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
package biblivre.administration.permissions;

import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PermissionDAOImpl extends AbstractDAO implements PermissionDAO {

    private static final String SELECT_PERMISSION_BY_LOGIN =
            "SELECT permission FROM permissions WHERE login_id = ?;";

    public static PermissionDAO getInstance() {
        return AbstractDAO.getInstance(PermissionDAOImpl.class);
    }

    @Override
    public boolean delete(UserDTO user) {
        Connection con = null;

        try {
            con = this.getConnection();
            con.setAutoCommit(false);

            PreparedStatement pst =
                    con.prepareStatement("DELETE FROM permissions WHERE login_id = ?;");
            pst.setInt(1, user.getLoginId());
            pst.executeUpdate();

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
    public Collection<String> getByLoginId(Integer loginId) {
        Set<String> permissions = new HashSet<>();

        try (Connection con = this.getConnection();
                PreparedStatement pst = con.prepareStatement(SELECT_PERMISSION_BY_LOGIN)) {

            pst.setInt(1, loginId);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                permissions.add(rs.getString("permission"));
            }

            return Collections.unmodifiableSet(permissions);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public boolean save(int loginid, String permission) {
        Connection con = null;
        try {
            con = this.getConnection();
            String sqlInsert = "INSERT INTO permissions (login_id, permission) VALUES (?, ?); ";

            PreparedStatement pstInsert = con.prepareStatement(sqlInsert);
            pstInsert.setInt(1, loginid);
            pstInsert.setString(2, permission);
            return pstInsert.executeUpdate() > 0;
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            this.closeConnection(con);
        }
    }

    @Override
    public boolean save(int loginId, List<String> permissions) {
        Connection con = null;
        try {
            con = this.getConnection();
            String sql = "INSERT INTO permissions (login_id, permission) VALUES (?, ?); ";

            PreparedStatement pst = con.prepareStatement(sql);

            for (String permission : permissions) {
                pst.setInt(1, loginId);
                pst.setString(2, permission);
                pst.addBatch();
            }

            pst.executeBatch();
            return true;
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            this.closeConnection(con);
        }
    }
}
