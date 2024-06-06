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

import biblivre.core.exceptions.DAOException;
import jakarta.annotation.Nonnull;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class UpdatesDAO extends AbstractDAO {
    public Set<String> getInstalledVersions() throws SQLException {
        String sql = "SELECT installed_versions FROM versions;";

        try (Connection connection = datasource.getConnection();
                Statement st = connection.createStatement()) {
            ResultSet rs = st.executeQuery(sql);

            Set<String> set = new HashSet<>();
            while (rs.next()) {
                set.add(rs.getString("installed_versions"));
            }
            return set;
        }
    }

    public Connection beginUpdate() throws SQLException {
        Connection con = datasource.getConnection();
        con.setAutoCommit(false);

        return con;
    }

    public void commitUpdate(String version, Connection con) throws SQLException {
        this.commitUpdate(version, con, true);
    }

    public void commitUpdate(String version, Connection con, boolean insert) throws SQLException {
        try (con) {
            if (insert) {
                try (PreparedStatement insertIntoVersions =
                        con.prepareStatement(
                                "INSERT INTO versions (installed_versions) VALUES (?);")) {

                    PreparedStatementUtil.setAllParameters(insertIntoVersions, version);

                    insertIntoVersions.executeUpdate();
                }

                this.commit(con);
            }
        }
    }

    public void rollbackUpdate(@Nonnull Connection con) {
        try (con) {
            this.rollback(con);
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public void createArrayAgg() throws SQLException {
        try (Connection con = datasource.getConnection()) {
            String sql =
                    "CREATE AGGREGATE public.array_agg(anyelement) (SFUNC=array_append, STYPE=anyarray, INITCOND=’{}’);";

            Statement st = con.createStatement();
            st.execute(sql);
        }
    }

    public void create81ArrayAgg() throws SQLException {

        try (Connection con = datasource.getConnection();
                Statement st = con.createStatement()) {

            String sql =
                    "CREATE AGGREGATE public.array_agg (SFUNC = array_append, BASETYPE = anyelement, STYPE = anyarray, INITCOND = '{}');";

            st.execute(sql);
        }
    }
}
