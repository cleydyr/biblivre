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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import biblivre.core.exceptions.DAOException;

public class PermissionDAOImpl implements PermissionDAO {
	private String _schema;
	private DataSource _dataSource;

	private static final String SET_SCHEMA_TEMPLATE = "SET search_path = '%s', public, pg_catalog;";

	private JdbcTemplate _jdbcTemplate;

	public PermissionDAOImpl(DataSource dataSource, String schema) {
		_schema = schema;
		_dataSource = dataSource;
		_jdbcTemplate = new JdbcTemplate(_dataSource);
	}

	private Connection getConnection() throws SQLException {
		Connection connection = _dataSource.getConnection();

		if (_schema != null) {
			try (Statement createStatement = connection.createStatement()) {
				createStatement.execute(String.format(SET_SCHEMA_TEMPLATE, _schema));
			}
		}

		return connection;
	}

	@Override
	public boolean deleteByUserId(int loginId) {
		String sql = "DELETE FROM " + _schema + ".permissions WHERE login_id = ?;";

		int updateCount = _jdbcTemplate.update(
				sql,
				ps -> {
					ps.setInt(1, loginId);
				}
		);

		return updateCount > 0;
	}

	@Override
	public Collection<String> getPermissionsByLoginId(Integer loginid) {
		String sql = new StringBuffer(3)
				.append("SELECT login_id, permission FROM ")
				.append(_schema)
				.append(".permissions WHERE login_id = ?;")
				.toString();

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

	@Override
	public boolean save(int loginid, String permission) {
		String sql = "INSERT INTO permissions (login_id, permission) VALUES (?, ?); ";

		try (Connection con = this.getConnection();
				PreparedStatement pstInsert = con.prepareStatement(sql)) {

			pstInsert.setInt(1, loginid);
			pstInsert.setString(2, permission);

			return pstInsert.executeUpdate() > 0;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	@Override
	public boolean saveAll(int loginId, Collection<String> permissions) {
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
