/*******************************************************************************
 * Este arquivo é parte do Biblivre5.
 *
 * Biblivre5 é um software livre; você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da Licença Pública Geral GNU como
 * publicada pela Fundação do Software Livre (FSF); na versão 3 da
 * Licença, ou (caso queira) qualquer versão posterior.
 *
 * Este programa é distribuído na esperança de que possa ser útil,
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
package biblivre.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.TreeSet;

public class UpdatesDAO extends AbstractDAO {

	public static UpdatesDAO getInstance(String schema) {
		return (UpdatesDAO) AbstractDAO.getInstance(UpdatesDAO.class, schema);
	}

	public Set<String> getInstalledVersions() throws SQLException {
		Connection con = null;

		try {
			con = this.getConnection();

			String sql = "SELECT installed_versions FROM versions;";

			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);

			Set<String> set = new TreeSet<String>();
			while (rs.next()) {
				set.add(rs.getString("installed_versions"));
			}
			return set;
		} finally {
			this.closeConnection(con);
		}
	}	

	public Connection beginUpdate() throws SQLException {
		Connection con = this.getConnection();
		con.setAutoCommit(false);

		return con;
	}	

	public void commitUpdate(String version, Connection con) throws SQLException {
		this.commitUpdate(version, con, true);
	}


	public void commitUpdate(String version, Connection con, boolean insert) throws SQLException {
		try {
			if (insert) {
				try (PreparedStatement insertIntoVersions = con.prepareStatement(
						"INSERT INTO versions (installed_versions) VALUES (?);")) {

					PreparedStatementUtil.setAllParameters(insertIntoVersions, version);

					insertIntoVersions.executeUpdate();
				}

				this.commit(con);
			}
		}
		finally {
			this.closeConnection(con);
		}
	}	


	public void rollbackUpdate(Connection con) {
		try {
			this.rollback(con);
		} finally {
			this.closeConnection(con);
		}
	}	

	public void createArrayAgg() throws SQLException {
		Connection con = null;

		try {
			con = this.getConnection();

			String sql = "CREATE AGGREGATE public.array_agg(anyelement) (SFUNC=array_append, STYPE=anyarray, INITCOND=’{}’);";

			Statement st = con.createStatement();
			st.execute(sql);
		} finally {
			this.closeConnection(con);
		}
	}	

	public void create81ArrayAgg() throws SQLException {

		try (Connection con = this.getConnection();
				Statement st = con.createStatement(); ) {

			String sql = "CREATE AGGREGATE public.array_agg (SFUNC = array_append, BASETYPE = anyelement, STYPE = anyarray, INITCOND = '{}');";

			st.execute(sql);
		}
	}

	public void fixVersionsTable() throws SQLException {
		Connection con = null;

		try {
			con = this.getConnection();

			String sql = "CREATE TABLE versions (" +
						 "installed_versions character varying NOT NULL, CONSTRAINT \"PK_versions\" PRIMARY KEY (installed_versions))" +
						 "WITH (OIDS=FALSE);";

			String sql2 = "ALTER TABLE backups OWNER TO biblivre;";

			Statement st = con.createStatement();
			st.execute(sql);
			st.execute(sql2);
		} finally {
			this.closeConnection(con);
		}
	}
}