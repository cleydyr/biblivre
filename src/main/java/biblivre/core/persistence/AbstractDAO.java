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
package biblivre.core.persistence;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.sql.DataSource;

import org.postgresql.PGConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biblivre.core.AbstractDTO;
import biblivre.core.CheckedBiConsumer;
import biblivre.core.Context;
import biblivre.core.ContextThreadLocal;
import biblivre.core.DTOCollection;
import biblivre.core.PagingDTO;
import biblivre.core.exceptions.DAOException;
import biblivre.core.persistence.sql.HikariDataSourceConnectionProvider;
import biblivre.core.persistence.sql.NullableSQLObject;
import biblivre.core.persistence.sql.PreparedStatementUtil;
import biblivre.core.utils.CheckedConsumer;
import biblivre.core.utils.CheckedFunction;

public abstract class AbstractDAO {
	private static final String _CHECK_TABLE_EXISTENCE_SQL =
		"SELECT count(*) as count " +
		"FROM information_schema.tables " +
		"WHERE table_schema = ? and table_name = ?";

	private static final String _CHECK_COLUMN_EXISTENCE_SQL =
		"SELECT count(*) as count " +
		"FROM information_schema.columns " +
		"WHERE table_schema = ? and table_name = ? and column_name = ?";

	private static final String _FIX_SEQUENCE_SQL_TPL =
		"SELECT setval('%s', coalesce((SELECT max(%s) + 1 FROM %s), 1), false)";

	private static final String _NEXT_SERIAL_SQL_TPL =
		"SELECT nextval('%s') FROM %s";

	private static Map<String, DataSource> dataSourceMap = new HashMap<>();

	protected static final Logger logger = LoggerFactory.getLogger(AbstractDAO.class);

	protected static CheckedFunction<PreparedStatement, Boolean> EXECUTE =
			pst -> pst.execute();

	private static HashMap<Class<? extends AbstractDAO>, AbstractDAO> INSTANCES =
		new HashMap<>();

	private DataSource datasource;

	protected AbstractDAO() {
	}

	@SuppressWarnings("unchecked")
	public static <T extends AbstractDAO> T getInstance(Class<T> cls) {
		AbstractDAO instance = INSTANCES.computeIfAbsent(cls, __ -> {
			try {
				return cls.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				logger.error(e.getMessage(), e);
			}
			return null;
		});

		return (T) instance;
	}

	public static Map<String, DataSource> getDataSourceMap() {
		return dataSourceMap;
	}

	public static void setDataSourceMap(Map<String, DataSource> dataSourceMap) {
		AbstractDAO.dataSourceMap = dataSourceMap;
	}

	protected Connection getConnection() throws SQLException {
		return this.getDataSource().getConnection();
	}

	protected final Connection getConnection(Context ctx) throws SQLException {
		Connection con = this.getConnection();

		try (Statement st = con.createStatement()) {
			st.execute(
				String.format(
					"SET search_path = '%s', public, pg_catalog",
					ctx.getSchema()));
		}

		return con;
	}

	private final DataSource getDataSource() {
		if (datasource == null) {
			datasource = HikariDataSourceConnectionProvider.getDataSource();
		}

		if (datasource == null) {
			logger.error("[DAO.Constructor] Data Source not found.");
			throw new RuntimeException("Data Source not found!");
		}

		return datasource;
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

	public final Integer getNextSerial(Connection con, String sequence) {
		String sql = String.format(_NEXT_SERIAL_SQL_TPL, sequence, sequence);

		try (PreparedStatement pst = con.prepareStatement(sql);
			ResultSet rs = pst.executeQuery()) {

			rs.next();

			return rs.getInt(1);
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	public final void fixSequence(String sequence, String tableName, String tableIdColumnName) {
		Connection con = null;
		String sql = String.format(_FIX_SEQUENCE_SQL_TPL, sequence, tableIdColumnName, tableName);

		try {
			con = this.getConnection();

			PreparedStatement pst = con.prepareStatement(sql);

			pst.executeQuery();
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

	}

	public final boolean checkFunctionExistance(String functionName) throws SQLException {
		Connection con = null;

		try {
			con = this.getConnection();

			String sql = "SELECT count(*) as count FROM pg_catalog.pg_proc WHERE proname = ?;";

			PreparedStatement pst = con.prepareStatement(sql);
			pst.setString(1, functionName);

			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				int count = rs.getInt("count");

				return count > 0;
			}

			return false;
		} finally {
			this.closeConnection(con);
		}
	}

	public final boolean checkColumnExistence(
		Context ctx, String tableName, String columnName)
		throws SQLException {

		return fetchOne(
			rs -> rs.getInt("count") == 1, _CHECK_COLUMN_EXISTENCE_SQL,
			ctx.getSchema(), tableName, columnName);
	}

	public final boolean checkTableExistance(Context ctx, String tableName)
		throws SQLException {

		return fetchOne(
			rs -> rs.getInt("count") == 1, _CHECK_TABLE_EXISTENCE_SQL,
			ctx.getSchema(), tableName);
	}


	public final String getPostgreSQLVersion() throws SQLException {
		return fetchOne(
			rs -> rs.getString("version"), "SELECT version() as version");
	}

	protected final boolean hasColumn(ResultSet rs, String columnName)
		throws SQLException {

	    ResultSetMetaData metadata = rs.getMetaData();

	    int columns = metadata.getColumnCount();

	    for (int i = 1; i <= columns; i++) {
	        if (columnName.equals(metadata.getColumnName(i))) {
	            return true;
	        }
	    }

	    return false;
	}

	protected PGConnection getPGConnection(Connection con) {
		PGConnection pgcon = null;

		try {
			pgcon = (PGConnection) con.unwrap(PGConnection.class);

			return pgcon;
		} catch (Exception e) {
			logger.info("getInnermostDelegate Unwrap");
			e.printStackTrace();
		}

		try {
			pgcon = _getInnermostDelegateFromConnection(
					con, "org.apache.tomcat.dbcp.dbcp.DelegatingConnection");

			return pgcon;
		} catch (Exception e) {
			logger.info("Skipping org.apache.tomcat.dbcp.dbcp.DelegatingConnection");

			e.printStackTrace();
		}

		try {
			pgcon =	 _getInnermostDelegateFromConnection(
					con, "org.apache.commons.dbcp.DelegatingConnection");

			return pgcon;
		} catch (Exception e) {
			logger.info("org.apache.commons.dbcp.DelegatingConnection");

			e.printStackTrace();
		}

		return null;
	}

	private PGConnection _getInnermostDelegateFromConnection(
			Connection con, String className)
		throws ClassNotFoundException, NoSuchMethodException, InstantiationException,
			IllegalAccessException,	InvocationTargetException {

		PGConnection pgcon;
		Class<?> D = Class.forName(className);
		Constructor<?> c = D.getConstructor(Connection.class);
		Object o = c.newInstance(con);
		Method m = D.getMethod("getInnermostDelegate");

		pgcon =	 (PGConnection) m.invoke(o);
		return pgcon;
	}

	public <T> T fetchOne(
		CheckedFunction<ResultSet, T> f, String sql, Object... parameters)
		throws DAOException {

		return executeQuery(pst -> {
			PreparedStatementUtil.setAllParameters(pst, parameters);

			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					return f.apply(rs);
				}
			}

			return null;
		}, sql, parameters) ;
	}

	public <T> T executeQuery(
		CheckedFunction<PreparedStatement, T> f, String sql,
		Object... parameters)
		throws DAOException {

		Context ctx = ContextThreadLocal.getContext();

		try (Connection con = this.getConnection(ctx);
			PreparedStatement pst = con.prepareStatement(sql)) {

			PreparedStatementUtil.setAllParameters(pst, parameters);

			return f.apply(pst);
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	public boolean executeUpdate(
		String sql, Object... parameters) {

		return executeQuery(
			pst -> pst.executeUpdate() > 0, sql, parameters);
	}

	public <T> boolean executeBatchUpdate(
		CheckedBiConsumer<PreparedStatement, T> consumer, Collection<T> items,
		String sql) {

		return executeQuery(pst -> {
			for (T item : items) {
				consumer.accept(pst, item);

				pst.addBatch();
			}

			pst.executeBatch();

			return true;
		}, sql);
	}

	@SafeVarargs
	public final <T> boolean executeBatchUpdate(
		Collection<?> items, Class<T> target, String sql,
		Function<T, ?>... fs) {

		return executeQuery(pst -> {
			for (Object item : items) {
				T targetItem = target.cast(item);

				List<Object> parameters = new ArrayList<>();

				for (Function<T, ?> f : fs) {
					parameters.add(f.apply(targetItem));
				}

				PreparedStatementUtil.setAllParameters(
					pst, parameters.toArray());

				pst.addBatch();
			}

			pst.executeBatch();

			return true;
		}, sql);
	}

	public <T extends AbstractDTO> DTOCollection<T> pagedListWith(
		CheckedFunction<ResultSet, T> mapper, String sql,
		int limit, int offset, Object... parameters) {

		DTOCollection<T> list = new DTOCollection<>();

		executeQuery(pst -> {
			Object[] newParameters = new Object[parameters.length + 2];

			System.arraycopy(
				parameters, 0, newParameters, 0, parameters.length);

			newParameters[parameters.length] = limit;
			newParameters[parameters.length + 1] = offset;

			PreparedStatementUtil.setAllParameters(pst, newParameters);

			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					list.add(mapper.apply(rs));
				}
			}

			return null;
		}, sql);

		String countSql = sql
			.substring(0, sql.lastIndexOf("ORDER BY"))
			.replaceAll("SELECT .*? FROM", "SELECT count(*) FROM");

		executeQuery(pst -> {
			PreparedStatementUtil.setAllParameters(pst, parameters);

			try (ResultSet rs = pst.executeQuery()) {
				rs.next();

				int count = rs.getInt(1);

				PagingDTO paging = new PagingDTO(count, limit, offset);

				list.setPaging(paging);
			}

			return null;
		}, countSql);

		return list;
	}

	public <T> List<T> listWith(
		CheckedFunction<ResultSet, T> mapper, String sql,
		Object... parameters)
		throws DAOException {

		return executeQuery(pst -> {
			List<T> list = new ArrayList<>();

			PreparedStatementUtil.setAllParameters(pst, parameters);

			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					list.add(mapper.apply(rs));
				}
			}

			return list;
		}, sql, parameters);
	}

	public final void onTransactionContext(
		CheckedConsumer<Connection> consumer) {

		Context ctx = ContextThreadLocal.getContext();

		Connection con = null;

		try {
			con  = this.getConnection(ctx);

			con.setAutoCommit(false);

			consumer.accept(con);

			con.commit();
		} catch (Exception e) {
			this.rollback(con);
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}

	public final <T> T onTransactionContext(
		CheckedFunction<Connection, T> function) {

		Context ctx = ContextThreadLocal.getContext();

		Connection con = null;

		try {
			con  = this.getConnection(ctx);

			con.setAutoCommit(false);

			T result = function.apply(con);

			con.commit();

			return result;
		} catch (Exception e) {
			this.rollback(con);
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}

	protected Object dateOrNullable(Date value) {
		return getNullableObject(value, NullableSQLObject.DATE);
	}

	protected Object stringOrNullable(String value) {
		return getNullableObject(value, NullableSQLObject.VARCHAR);
	}

	protected Object floatOrNullable(Float value) {
		return getNullableObject(value, NullableSQLObject.FLOAT);
	}

	protected Object integerOrNullable(Integer value) {
		return getNullableObject(value, NullableSQLObject.INTEGER);
	}

	private Object getNullableObject(Object value, NullableSQLObject deflt) {
		return value != null ? value : deflt;
	}
}
