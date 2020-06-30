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
package biblivre.digitalmedia.postgres;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;
import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;

import biblivre.core.exceptions.DAOException;
import biblivre.core.file.BiblivreFile;
import biblivre.digitalmedia.DigitalMediaDAO;

public class PostgresLargeObjectDigitalMediaDAO extends DigitalMediaDAO {

	@Override
	public void persist(InputStream is, long oid, long size)
		throws SQLException, IOException {

		try (Connection con = this.getConnection()) {

			con.setAutoCommit(false);

			PGConnection pgcon = this.getPGConnection(con);

			LargeObjectManager lobj = pgcon.getLargeObjectAPI();

			LargeObject obj = lobj.open(oid, LargeObjectManager.WRITE);

			IOUtils.copy(is, obj.getOutputStream());

			obj.close();

			this.commit(con);
		}
	}

	@Override
	protected void deleteBlob(long oid) {
		try (Connection con = this.getConnection()) {

			con.setAutoCommit(false);

			PGConnection pgcon = this.getPGConnection(con);

			LargeObjectManager lobj = pgcon.getLargeObjectAPI();

			lobj.delete(oid);

			this.commit(con);
		} catch (SQLException e) {
			e.printStackTrace();

			throw new DAOException(e);
		}
	}

	@Override
	protected BiblivreFile getFile(long oid) throws Exception {
		Connection con = this.getConnection();

		con.setAutoCommit(false);

		PGConnection pgcon = this.getPGConnection(con);

		if (pgcon == null) {
			throw new Exception("Invalid Delegating Connection");
		}

		LargeObjectManager lobj = pgcon.getLargeObjectAPI();

		LargeObject obj = lobj.open(oid);

		return new DatabaseFile(con, obj);
	}
}
