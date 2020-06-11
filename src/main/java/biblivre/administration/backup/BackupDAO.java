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
package biblivre.administration.backup;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObjectManager;

import biblivre.core.AbstractDAO;
import biblivre.core.NullableSQLObject;
import biblivre.core.exceptions.DAOException;

public class BackupDAO extends AbstractDAO {
	private static final String _INSERT_SQL = "INSERT INTO backups (id, path, schemas, type, scope, downloaded, steps, current_step) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

	public static BackupDAO getInstance(String schema) {
		return (BackupDAO) AbstractDAO.getInstance(BackupDAO.class, schema);
	}

	public boolean save(BackupDTO dto) {
		if (dto == null || dto.getType() == null) {
			return false;
		}

		dto.setId(this.getNextSerial("backups_id_seq"));

		if (dto.getBackup() != null) {
			return executeUpdate(
				_INSERT_SQL, dto.getId(), dto.getBackup().getAbsolutePath(),
				dto.getSchemasString(), dto.getType().toString(),
				dto.getBackupScope().toString(), dto.isDownloaded(),
				dto.getSteps(), dto.getCurrentStep());
		} else {
			return executeUpdate(
				_INSERT_SQL, dto.getId(), NullableSQLObject.VARCHAR,
				dto.getSchemasString(), dto.getType().toString(),
				dto.getBackupScope().toString(), dto.isDownloaded(),
				dto.getSteps(), dto.getCurrentStep());
		}
	}

	public BackupDTO get(Integer id) {
		if (id == null) {
			return null;
		}

		BackupDTO dto = null;

		Connection con = null;
		try {
			con = this.getConnection();
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM backups WHERE id = ?;");

			PreparedStatement pst = con.prepareStatement(sql.toString());

			pst.setInt(1, id);

			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				dto = this.populateDTO(rs);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

		return dto;
	}

	public long createOID() {
		Connection con = null;
		try {
			con = this.getConnection();
			con.setAutoCommit(false);

			PGConnection pgcon = getPGConnection(con);

			LargeObjectManager lobj = pgcon.getLargeObjectAPI();
			long oid = lobj.createLO();

			this.commit(con);

			return oid;
		} catch (Exception e) {
			this.rollback(con);
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}

	public Set<String> listDatabaseSchemas() {
		Set<String> set = new TreeSet<String>();

		Connection con = null;
		try {
			con = this.getConnection();
			String sql = "SELECT schema_name FROM information_schema.schemata WHERE schema_name <> 'information_schema' AND schema_name !~ E'^pg_';";

			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);

			while (rs.next()) {
				set.add(rs.getString("schema_name"));
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

		return set;
	}

	public LinkedList<BackupDTO> list() {
		return this.list(0);
	}

	public LinkedList<BackupDTO> list(int limit) {
		LinkedList<BackupDTO> list = new LinkedList<BackupDTO>();

		Connection con = null;
		try {
			con = this.getConnection();
			StringBuilder sql = new StringBuilder();

			sql.append("SELECT * FROM backups ORDER BY created DESC ");

			if (limit > 0) {
				sql.append("LIMIT ?");
			}

			PreparedStatement pst = con.prepareStatement(sql.toString());
			if (limit > 0) {
				pst.setInt(1, limit);
			}

			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				list.add(this.populateDTO(rs));
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

		return list;
	}

	private BackupDTO populateDTO(ResultSet rs) throws SQLException {
		String schemas = rs.getString("schemas");
		BackupType type = BackupType.fromString(rs.getString("type"));
		BackupScope scope = BackupScope.fromString(rs.getString("scope"));

		BackupDTO dto = new BackupDTO(schemas, type, scope);

		dto.setId(rs.getInt("id"));
		dto.setCreated(rs.getTimestamp("created"));

		String path = rs.getString("path");
		if (StringUtils.isNotBlank(path)) {
			dto.setBackup(new File(path));
		}

		dto.setDownloaded(rs.getBoolean("downloaded"));
		dto.setSteps(rs.getInt("steps"));
		dto.setCurrentStep(rs.getInt("current_step"));

		return dto;
	}
}
