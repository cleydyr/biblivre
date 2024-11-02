/*
 * ******************************************************************************
 *  * Este arquivo é parte do Biblivre5.
 *  *
 *  * Biblivre5 é um software livre; você pode redistribuí-lo e/ou
 *  * modificá-lo dentro dos termos da Licença Pública Geral GNU como
 *  * publicada pela Fundação do Software Livre (FSF); na versão 3 da
 *  * Licença, ou (caso queira) qualquer versão posterior.
 *  *
 *  * Este programa é distribuído na esperança de que possa ser  útil,
 *  * mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 *  * MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
 *  * Licença Pública Geral GNU para maiores detalhes.
 *  *
 *  * Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 *  * com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
 *  *
 *  * @author Cleydyr de Albuquerque <cleydyr@biblivre.cloud>
 *  *****************************************************************************
 */

package biblivre;

import biblivre.core.SchemaThreadLocal;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import javax.sql.DataSource;

public class SchemaAwareDatasourceDecorator implements DataSource {
    private final DataSource datasource;

    public SchemaAwareDatasourceDecorator(DataSource datasource) {
        this.datasource = datasource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = datasource.getConnection();

        setSearchPath(connection);

        return connection;
    }

    private static void setSearchPath(Connection connection) throws SQLException {
        String schema = SchemaThreadLocal.get();

        if (schema == null) {
            return;
        }

        connection
                .createStatement()
                .execute(
                        "SET search_path = '%s', public, pg_catalog;"
                                .formatted(SchemaThreadLocal.get()));
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Connection connection = datasource.getConnection(username, password);

        setSearchPath(connection);

        return connection;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return datasource.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return datasource.isWrapperFor(iface);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return datasource.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        datasource.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        datasource.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return datasource.getLoginTimeout();
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return datasource.getParentLogger();
    }
}
