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
package biblivre.digitalmedia;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;

import biblivre.core.exceptions.DAOException;
import biblivre.core.file.DatabaseFile;
import biblivre.core.file.MemoryFile;
import biblivre.core.persistence.AbstractDAO;

public class DigitalMediaDAO extends AbstractDAO implements IDigitalMediaDAO {

	private static final String _LIST_SQL =
		"SELECT id, blob, name FROM digital_media";

	private static final String _DELETE_SQL =
		"DELETE FROM digital_media WHERE id = ?";

	private static final String _LOAD_SQL =
		"SELECT name, blob, content_type, size, created "
		+ "FROM digital_media "
		+ "WHERE id = ? AND name = ?";

	private static final String _SAVE_SQL =
		"INSERT INTO digital_media "
		+ "(id, name, blob, content_type, size) "
		+ "VALUES (?, ?, ?, ?, ?)";

	@Override
	public long createOID() {
		return onTransactionContext(con -> {
			PGConnection pgcon = getPGConnection(con);

			LargeObjectManager lobj = pgcon.getLargeObjectAPI();
			long oid = lobj.createLO();

			return oid;
		});
	}

	@Override
	public final Integer save(MemoryFile file) {
		return onTransactionContext(con -> {
			try (InputStream is = file.getNewInputStream()) {
				PGConnection pgcon = this.getPGConnection(con);

				if (pgcon == null) {
					throw new Exception("Invalid Delegating Connection");
				}

				Integer serial = file.getId();

				if (serial == null) {
					serial = getNextSerial(con, "digital_media_id_seq");

					file.setId(serial);
				}

				if (serial != 0) {
					LargeObjectManager lobj = pgcon.getLargeObjectAPI();

					long oid = lobj.createLO();

					LargeObject obj = lobj.open(oid, LargeObjectManager.WRITE);

					byte buf[] = new byte[4096];

					int bytesRead = 0;

					while ((bytesRead = is.read(buf)) > 0) {
						obj.write(buf, 0, bytesRead);
					}

					obj.close();

					executeUpdate(
						_SAVE_SQL, serial, file.getName(), oid,
						file.getContentType(), file.getSize());

					file.close();
				}

				return serial;
			}
		});
	}


	@Override
	public final long importFile(File file) {
		Connection con = null;

		try (InputStream is = new FileInputStream(file)) {
			con = this.getConnection();
			con.setAutoCommit(false);

			PGConnection pgcon = this.getPGConnection(con);

			if (pgcon == null) {
				throw new Exception("Invalid Delegating Connection");
			}

			LargeObjectManager lobj = pgcon.getLargeObjectAPI();
			long oid = lobj.createLO();

			LargeObject obj = lobj.open(oid, LargeObjectManager.WRITE);

			byte buf[] = new byte[4096];
			int bytesRead = 0;
			while ((bytesRead = is.read(buf)) > 0) {
				obj.write(buf, 0, bytesRead);
			}

			obj.close();

			this.commit(con);

			return oid;
		} catch (Exception e) {
			this.rollback(con);
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}

	@Override
	public final DatabaseFile load(int id, String name) {
		return onTransactionContext(con -> {
			PGConnection pgcon = this.getPGConnection(con);

			if (pgcon == null) {
				throw new Exception("Invalid Delegating Connection");
			}

			LargeObjectManager lobj = pgcon.getLargeObjectAPI();

			// We check both ID and FILE_NAME for security reasons, so users can't "guess"
			// id's and get the files.

			return fetchOne(rs -> {
				DatabaseFile file = null;

				long oid = rs.getLong("blob");
				LargeObject obj = lobj.open(oid, LargeObjectManager.READ);

				file = new DatabaseFile(con, obj);

				file.setName(rs.getString("name"));
				file.setContentType(rs.getString("content_type"));
				file.setLastModified(rs.getTimestamp("created").getTime());
				file.setSize(rs.getLong("size"));

				return file;
			}, _LOAD_SQL, id, name);
		});
	}

	@Override
	public boolean delete(int id) {
		return executeUpdate(_DELETE_SQL, id);
	}

	@Override
	public List<DigitalMediaDTO> list() {
		return listWith(this::_populateDTO, _LIST_SQL);
	}

	private DigitalMediaDTO _populateDTO(ResultSet rs) throws SQLException {
		DigitalMediaDTO dto = new DigitalMediaDTO();

		dto.setId(rs.getInt("id"));
		dto.setBlob(rs.getLong("blob"));
		dto.setName(rs.getString("name"));

		return dto;
	}
}
