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

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;

import biblivre.core.file.DatabaseFile;

public class DigitalMediaDAO extends BaseDigitalMediaDAO {

	@Override
	public void persistBinary(long oid, InputStream is, long size) throws SQLException, IOException {
		Connection con = this.getConnection();

		con.setAutoCommit(false);

		PGConnection pgcon = this.getPGConnection(con);

		LargeObjectManager lobj = pgcon.getLargeObjectAPI();

		LargeObject obj = lobj.open(oid, LargeObjectManager.WRITE);

		byte buf[] = new byte[4096];
		int bytesRead = 0;
		while ((bytesRead = is.read(buf)) > 0) {
			obj.write(buf, 0, bytesRead);
		}

		obj.close();

		con.close();
	}


	@Override
	public DatabaseFile populateBiblivreFile(ResultSet rs) throws SQLException {
		DatabaseFile file = null;

		Connection con = this.getConnection();

		con.setAutoCommit(false);

		PGConnection pgcon = this.getPGConnection(con);

		LargeObjectManager lobj = pgcon.getLargeObjectAPI();

		if (rs.next()) {
			long oid = rs.getLong("blob");

			LargeObject obj = lobj.open(oid, LargeObjectManager.READ);

			file = new DatabaseFile(con, obj);

			file.setName(rs.getString("name"));
			file.setContentType(rs.getString("content_type"));
			file.setLastModified(rs.getTimestamp("created").getTime());
			file.setSize(rs.getLong("size"));
		}

		con.close();

		return file;
	}

}
