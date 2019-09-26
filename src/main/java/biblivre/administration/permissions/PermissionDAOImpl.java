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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import biblivre.circulation.user.UserDTO;
import biblivre.core.exceptions.DAOException;

public class PermissionDAOImpl {

	private void closeConnection(Connection con) {
		// TODO Auto-generated method stub

	}

	private void rollback(Connection con) {
		// TODO Auto-generated method stub

	}

	private Connection getConnection() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean delete(UserDTO user) {
		String sql = "DELETE FROM permissions WHERE login_id = ?;";

		try (Connection con = this.getConnection()) {
			con.setAutoCommit(false);

			try (PreparedStatement pst = con.prepareStatement(sql)) {
				pst.setInt(1, user.getLoginId());
				pst.executeUpdate();
				con.commit();
				return true;
			}
			catch (Exception e) {
				con.rollback();
				throw new DAOException(e);
			}
		} catch (SQLException sqle) {
			throw new DAOException(sqle);
		}
	}

	public Collection<String> getByLoginId(Integer loginid) {

		String sql = "SELECT login_id, permission FROM permissions WHERE login_id = ?;";

		try (Connection con = this.getConnection();
				PreparedStatement pst = con.prepareStatement(sql)) {

			pst.setInt(1, loginid);

			try (ResultSet rs = pst.executeQuery()) {
				List<String> list = new ArrayList<String>();

				while (rs.next()) {
					list.add(rs.getString("permission"));
				}

				return list;
			}
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	public boolean save(int loginid, String permission) {
		String sqlInsert = "INSERT INTO permissions (login_id, permission) VALUES (?, ?); ";

		try (Connection con = this.getConnection();
				PreparedStatement pstInsert = con.prepareStatement(sqlInsert)) {

			pstInsert.setInt(1, loginid);
			pstInsert.setString(2, permission);

			return pstInsert.executeUpdate() > 0;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	public boolean save(int loginId, Collection<String> permissions) {
		String sql = "INSERT INTO permissions (login_id, permission) VALUES (?, ?); ";

		try (Connection con = this.getConnection();
				PreparedStatement pst = con.prepareStatement(sql)) {

			for (String permission : permissions) {
				pst.setInt(1, loginId);
				pst.setString(2, permission);
				pst.addBatch();
			}

			pst.executeBatch();

			return true;
		} catch (SQLException e) {
			throw new DAOException(e);
		}
	}

}
