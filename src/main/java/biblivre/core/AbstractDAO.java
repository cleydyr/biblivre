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
import biblivre.core.utils.Constants;
import biblivre.core.utils.DatabaseUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.postgresql.PGConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDAO {
    private static final Map<String, DataSource> dataSourceMap = new HashMap<>();

    protected static final Logger logger = LoggerFactory.getLogger(AbstractDAO.class);
    private String dataSourceName;

    private DataSourceProvider dataSourceProvider;

    private static final Map<String, AbstractDAO> instances = new HashMap<>();

    protected AbstractDAO() {}

    public static <T extends AbstractDAO> T getInstance(
            DataSourceProvider dataSourceProvider, Class<T> cls) {

        return AbstractDAO.getInstance(dataSourceProvider, cls, Constants.DEFAULT_DATABASE_NAME);
    }

    public static <T extends AbstractDAO> T getInstance(Class<T> cls) {

        return AbstractDAO.getInstance(cls, DatabaseUtils.getDatabaseName());
    }

    public static <T extends AbstractDAO> T getInstance(Class<T> cls, String dataSourceName) {

        DataSourceProvider dataSourceProvider = new HikariDataSourceProvider();

        return getInstance(dataSourceProvider, cls, dataSourceName);
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractDAO> T getInstance(
            DataSourceProvider dataSourceProvider, Class<T> cls, String dataSourceName) {

        String schema = SchemaThreadLocal.get();

        String key = cls.getName() + "#" + schema + ":" + dataSourceName;

        AbstractDAO instance = AbstractDAO.instances.get(key);

        if (instance == null) {
            if (!AbstractDAO.class.isAssignableFrom(cls)) {
                throw new IllegalArgumentException(
                        "DAO: getInstance: Class " + cls.getName() + " is not a subclass of DAO.");
            }

            try {
                instance = cls.getDeclaredConstructor().newInstance();
                instance.setDataSourceName(dataSourceName);
                instance.dataSourceProvider = dataSourceProvider;

                AbstractDAO.instances.put(key, instance);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }

        return (T) instance;
    }

    public boolean testDatabaseConnection() {
        Connection con = null;

        try {
            con = this.getConnection();
            Statement st = con.createStatement();
            st.executeQuery("select version();");
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            this.closeConnection(con);
        }
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    protected final Connection getConnection() throws SQLException {
        Connection con = this.getDataSource().getConnection();

        con.createStatement()
                .execute(
                        "SET search_path = '" + SchemaThreadLocal.get() + "', public, pg_catalog;");

        return con;
    }

    private DataSource getDataSource() {
        DataSource ds = dataSourceMap.get(dataSourceName);

        if (ds == null) {
            ds = dataSourceProvider.getDataSource(dataSourceName);
        }

        if (ds == null) {
            this.logger.error("[DAO.Constructor] Data Source not found.");
            throw new RuntimeException("Data Source not found!");
        } else {
            dataSourceMap.put(dataSourceName, ds);
        }

        return ds;
    }

    protected final void closeConnection(Connection con) {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

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

        try (Connection con = this.getConnection();
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

    public final boolean checkTableExistance(String tableName) throws SQLException {
        Connection con = null;

        try {
            con = this.getConnection();

            String sql =
                    "SELECT count(*) as count FROM information_schema.tables WHERE table_schema = ? and table_name = ?;";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, SchemaThreadLocal.get());
            pst.setString(2, tableName);

            ResultSet rs = pst.executeQuery();
            return rs.next() && rs.getInt("count") == 1;
        } finally {
            this.closeConnection(con);
        }
    }

    protected final boolean hasBiblioColumn(ResultSet rs) throws SQLException {
        ResultSetMetaData metadata = rs.getMetaData();
        int columns = metadata.getColumnCount();
        for (int x = 1; x <= columns; x++) {
            if ("biblio".equals(metadata.getColumnName(x))) {
                return true;
            }
        }
        return false;
    }

    protected PGConnection getPGConnection(Connection con) {
        try {
            return con.unwrap(PGConnection.class);
        } catch (Exception e) {
            this.logger.warn("exception when calling getInnermostDelegate Unwrap", e);
        }

        try {
            return _getInnermostDelegateFromConnection(
                    con, "org.apache.tomcat.dbcp.dbcp.DelegatingConnection");
        } catch (Exception e) {
            this.logger.warn("Skipping org.apache.tomcat.dbcp.dbcp.DelegatingConnection", e);
        }

        try {
            return _getInnermostDelegateFromConnection(
                    con, "org.apache.commons.dbcp.DelegatingConnection");
        } catch (Exception e) {
            this.logger.warn("org.apache.commons.dbcp.DelegatingConnection", e);
        }

        return null;
    }

    private PGConnection _getInnermostDelegateFromConnection(Connection con, String className)
            throws ClassNotFoundException, NoSuchMethodException, InstantiationException,
                    IllegalAccessException, InvocationTargetException {

        Class<?> D = Class.forName(className);
        Constructor<?> c = D.getConstructor(Connection.class);
        Object o = c.newInstance(con);
        Method m = D.getMethod("getInnermostDelegate");

        return (PGConnection) m.invoke(o);
    }

    protected <T> T withTransactionContext(UnsafeFunction<Connection, T> function) {
        try (Connection connection = getConnection()) {
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
        try (Connection connection = getConnection()) {
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
}
