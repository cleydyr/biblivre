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

import biblivre.cataloging.enums.RecordType;
import biblivre.core.utils.TextUtils;


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

	public void fixUserNameAscii(Connection con) throws SQLException {
		if (this.checkColumnExistance("users", "name_ascii")) {
			return;
		}

		Statement st = con.createStatement();
		st.executeUpdate("ALTER TABLE users ADD COLUMN name_ascii character varying;");

		st = con.createStatement();
		ResultSet rs = st.executeQuery("SELECT id, name FROM users;");

		PreparedStatement pst = con.prepareStatement("UPDATE users SET name_ascii = ? WHERE id = ?");

		boolean run = false;
		while (rs.next()) {
			run = true;
			pst.setString(1, TextUtils.removeDiacriticals(rs.getString("name")));
			pst.setInt(2, rs.getInt("id"));
			pst.addBatch();
		}

		if (run) {
			pst.executeBatch();
		}
	}

	public void fixBackupTable(Connection con) throws SQLException {
		String sql = "CREATE TABLE global.backups (id serial NOT NULL, " +
					 "created timestamp without time zone NOT NULL DEFAULT now(), " +
					 "path character varying, " +
					 "schemas character varying NOT NULL, " +
					 "type character varying NOT NULL, " +
					 "scope character varying NOT NULL, " +
					 "downloaded boolean NOT NULL DEFAULT false, " +
					 "steps integer, " +
					 "current_step integer, " +
					 "CONSTRAINT \"PK_backups\" PRIMARY KEY (id) " +
					 ") WITH (OIDS=FALSE);";

		String sql2 = "ALTER TABLE global.backups OWNER TO biblivre;";

		Statement st = con.createStatement();
		st.execute(sql);
		st.execute(sql2);
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

	public void fixAuthoritiesAutoComplete() throws SQLException {
		Connection con = null;

		try {
			con = this.getConnection();

			String sql = "UPDATE biblio_form_subfields SET autocomplete_type = 'authorities' WHERE subfield = 'a' AND datafield in ('100', '110', '111');";	
			Statement st = con.createStatement();
			st.execute(sql);
		} finally {
			this.closeConnection(con);
		}
	}


	public void fixVocabularyAutoComplete() throws SQLException {
		Connection con = null;

		try {
			con = this.getConnection();

			String sql = "UPDATE biblio_form_subfields SET autocomplete_type = 'vocabulary' WHERE subfield = 'a' AND datafield in ('600', '610', '611', '630', '650', '651');";	
			Statement st = con.createStatement();
			st.execute(sql);
		} finally {
			this.closeConnection(con);
		}
	}

	public void fixHoldingCreationTable(Connection con) throws SQLException {
		String sql = "UPDATE holding_creation_counter HA " +
				"SET user_name = coalesce(U.name, L.login), user_login = L.login " +
				"FROM holding_creation_counter H " +
				"INNER JOIN logins L ON L.id = H.created_by " +
				"LEFT JOIN users U on U.login_id = H.created_by " +
				"WHERE HA.created_by = H.created_by;";

		Statement st = con.createStatement();
		st.execute(sql);
	}

	public void fixCDDBiblioBriefFormat(Connection con) throws SQLException {
		String sql = "UPDATE biblio_brief_formats " +
				"SET format = '${a}_{ }${2}' " +
				"WHERE format = '${a}_{ }_{2}';";
		Statement st = con.createStatement();
		st.execute(sql);
	}

	public void fixAuthoritiesBriefFormat(Connection con) throws SQLException {
		String sql = "UPDATE authorities_brief_formats " +
				"SET format = '${a}_{; }${b}_{; }${c}_{ - }${d}' " +
				"WHERE datafield = '110';";
		Statement st = con.createStatement();
		st.execute(sql);
	}

	public void addDatafieldSortOrderColumns(Connection con, RecordType recordType) throws SQLException {
		String tableName = recordType + "_form_datafields";

		if (this.checkColumnExistance(tableName, "sort_order")) {
			return;
		}

		_addSortOrderColumnForTable(tableName, con);

		_updateSortOrderForTable(tableName, con);
	}

	public void addSubfieldSortOrderColumns(Connection con, RecordType recordType) throws SQLException {
		String tableName = recordType + "_form_subfields";

		if (this.checkColumnExistance(tableName, "sort_order")) {
			return;
		}

		_addSortOrderColumnForTable(tableName, con);

		_updateSortOrderForTableWithSubfield(tableName, con);
	}	

	public void addBriefFormatSortOrderColumns(Connection con, RecordType recordType) throws SQLException {
		String tableName = recordType + "_brief_formats";

		if (this.checkColumnExistance(tableName, "sort_order")) {
			return;
		}

		_addSortOrderColumnForTable(tableName, con);

		_updateSortOrderForTable(tableName, con);
	}

	private void _updateSortOrderForTable(String tableName, Connection con) throws SQLException {
		StringBuilder updateSql =
				new StringBuilder(3)
					.append("UPDATE ")
					.append(tableName)
					.append(" SET sort_order = (CAST(datafield as INT));");

		try (Statement updateSt = con.createStatement()) {
			updateSt.execute(updateSql.toString());
		}
	}

	private void _updateSortOrderForTableWithSubfield(
			String tableName, Connection con)
		throws SQLException {

		StringBuilder updateSql =
				new StringBuilder(3)
					.append("UPDATE ")
					.append(tableName)
					.append(" SET sort_order = (CAST(datafield as INT) + ASCII(subfield));");

		try (Statement updateSt = con.createStatement()) {
			updateSt.execute(updateSql.toString());
		}
	}

	private void _addSortOrderColumnForTable(String tableName, Connection con) throws SQLException {
		StringBuilder addSortOrderColumnSQL =
				new StringBuilder(3)
					.append("ALTER TABLE ")
					.append(tableName)
					.append(" ADD COLUMN sort_order integer;");

		try (Statement addDatafieldColumnSt = con.createStatement()) {
			addDatafieldColumnSt.execute(addSortOrderColumnSQL.toString());
		}
	}

	public void updateZ3950Address(Connection con, String name, String url) throws SQLException {
		String sql = "UPDATE z3950_addresses SET url = ? WHERE name = ?;";
		PreparedStatement pst = con.prepareStatement(sql);
		pst.setString(1, url);
		pst.setString(2, name);

		pst.execute();
	}

	public void replaceBiblivreVersion(Connection con) throws SQLException {
		con.createStatement().execute("UPDATE translations SET text = replace(text, 'Biblivre 4', 'Biblivre 5'), modified = now() WHERE text like '%Biblivre 4%';");
		con.createStatement().execute("UPDATE translations SET text = replace(text, 'Biblivre4', 'Biblivre 5'), modified = now() WHERE text like '%Biblivre4%'");
		con.createStatement().execute("UPDATE translations SET text = replace(text, 'Biblivre IV', 'Biblivre V'), modified = now() WHERE text like '%Biblivre IV%'");
		con.createStatement().execute("UPDATE translations SET text = replace(text, 'ersão 4.0', 'ersão 5.0'), modified = now() WHERE text like '%ersão 4.0%'");
		con.createStatement().execute("UPDATE translations SET text = replace(text, 'ersión 4.0', 'ersión 5.0'), modified = now() WHERE text like '%ersão 4.0%'");
		con.createStatement().execute("UPDATE translations SET text = replace(text, 'ersion 4.0', 'ersion 5.0'), modified = now() WHERE text like '%ersão 4.0%'");
		con.createStatement().execute("UPDATE configurations SET value = replace(value, 'Biblivre IV', 'Biblivre V'), modified = now() WHERE value like '%Biblivre IV%'");
		con.createStatement().execute("UPDATE configurations SET value = replace(value, 'ersão 4.0', 'ersão 5.0'), modified = now() WHERE value like '%ersão 4.0%'");
		con.createStatement().execute("UPDATE configurations SET value = replace(value, 'ersión 4.0', 'ersión 5.0'), modified = now() WHERE value like '%ersão 4.0%'");
		con.createStatement().execute("UPDATE configurations SET value = replace(value, 'ersion 4.0', 'ersion 5.0'), modified = now() WHERE value like '%ersão 4.0%'");
	}
}