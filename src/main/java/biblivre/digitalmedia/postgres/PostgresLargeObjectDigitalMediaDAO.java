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

import biblivre.core.file.BiblivreFile;
import biblivre.digitalmedia.BaseDigitalMediaDAO;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
        value = "DIGITAL_MEDIA_DAO_IMPL",
        havingValue = "biblivre.digitalmedia.postgres.PostgresLargeObjectDigitalMediaDAO",
        matchIfMissing = true)
@Slf4j
public class PostgresLargeObjectDigitalMediaDAO extends BaseDigitalMediaDAO {

    @Override
    public void persist(InputStream is, long oid, long size) throws SQLException, IOException {
        withTransactionContext(
                con -> {
                    PGConnection pgcon = con.unwrap(PGConnection.class);

                    LargeObjectManager lobj = pgcon.getLargeObjectAPI();

                    LargeObject obj = lobj.open(oid, LargeObjectManager.WRITE);

                    IOUtils.copy(is, obj.getOutputStream());

                    obj.close();
                });
    }

    @Override
    protected void deleteBlob(long oid) {
        withTransactionContext(
                con -> {
                    PGConnection pgcon = con.unwrap(PGConnection.class);

                    LargeObjectManager lobj = pgcon.getLargeObjectAPI();

                    lobj.delete(oid);
                });
    }

    @Override
    protected BiblivreFile getFile(long oid) throws Exception {
        Connection con = datasource.getConnection();

        con.setAutoCommit(false);

        PGConnection pgcon = con.unwrap(PGConnection.class);

        if (pgcon == null) {
            throw new Exception("Invalid Delegating Connection");
        }

        LargeObjectManager lobj = pgcon.getLargeObjectAPI();

        LargeObject obj = lobj.open(oid);

        return new DatabaseFile(con, obj);
    }
}
