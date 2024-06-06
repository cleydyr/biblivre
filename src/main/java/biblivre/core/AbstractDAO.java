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
package biblivre.core;

import biblivre.core.exceptions.DAOException;
import biblivre.core.function.UnsafeConsumer;
import biblivre.core.function.UnsafeFunction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractDAO {
    protected final void rollback(Connection con) {
        try {
            if (con != null) {
                con.rollback();
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    protected final void commit(Connection con) {
        try {
            if (con != null) {
                con.commit();
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    public final int getNextSerial(String sequence) {
        String sql = "SELECT nextval('%s') FROM %s".formatted(sequence, sequence);

        try (Connection con = datasource.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();

            if ((rs != null) && rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }

        return -1;
    }

    protected <T> T withTransactionContext(UnsafeFunction<Connection, T> function) {
        try (Connection connection = datasource.getConnection()) {
            try {
                connection.setAutoCommit(false);

                T result = function.apply(connection);

                connection.commit();

                return result;
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    protected void withTransactionContext(UnsafeConsumer<Connection> consumer) {
        try (Connection connection = datasource.getConnection()) {
            try {
                connection.setAutoCommit(false);

                consumer.accept(connection);

                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    protected DataSource datasource;

    @Autowired
    public final void setDataSource(DataSource dataSource) {
        this.datasource = dataSource;
    }
}
