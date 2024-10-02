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
package biblivre.core.schemas;

import biblivre.administration.backup.RestoreService;
import biblivre.administration.setup.State;
import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;
import biblivre.core.utils.Constants;
import java.sql.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SchemasDAOImpl extends AbstractDAO implements SchemaDAO {

    private Set<SchemaDTO> schemas;
    private RestoreService restoreService;

    @Override
    public Set<SchemaDTO> list() {
        if (schemas != null) {
            return schemas;
        }

        Set<SchemaDTO> set = new HashSet<>();

        try (Connection con = datasource.getConnection();
                Statement st = con.createStatement()) {
            String sql = "SELECT * FROM global.schemas";

            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                set.add(this.populateDTO(rs));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            throw new DAOException(e);
        }

        schemas = Collections.unmodifiableSet(set);

        return set;
    }

    @Override
    public boolean delete(SchemaDTO dto) {
        return withTransactionContext(
                con -> {
                    con.setAutoCommit(false);

                    String sql = "DELETE FROM schemas WHERE schema = ? AND name = ?;";

                    PreparedStatement pst = con.prepareStatement(sql);

                    pst.setString(1, dto.getSchema());
                    pst.setString(2, dto.getName());

                    boolean success = pst.executeUpdate() > 0;

                    if (success) {
                        PGConnection unwrap = con.unwrap(PGConnection.class);

                        String dropSql = "DROP SCHEMA ? CASCADE;";

                        PreparedStatement dropSt = con.prepareStatement(dropSql);

                        dropSt.setString(1, dto.getSchema());

                        dropSt.executeUpdate();
                    }

                    return success;
                });
    }

    @Override
    public boolean save(SchemaDTO dto) {
        try (Connection con = datasource.getConnection()) {
            String sql = "UPDATE schemas SET name = ?, disabled = ? WHERE schema = ?;";

            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, dto.getName());
            pst.setBoolean(2, dto.isDisabled());
            pst.setString(3, dto.getSchema());

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public boolean exists(String schema) {
        try (Connection con = datasource.getConnection()) {
            String sql = "SELECT * FROM schemas WHERE schema = ?;";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, schema);

            ResultSet rs = pst.executeQuery();

            return rs.next();
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public boolean createSchema(SchemaDTO dto, boolean addToGlobal, Resource databaseTemplate) {
        boolean success = false;

        try (Connection connection = datasource.getConnection();
                Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);

            if (exists(dto.getSchema())) {
                State.writeLog("Dropping old schema");

                if (!dto.getSchema().equals(Constants.GLOBAL_SCHEMA)) {
                    statement.addBatch("DELETE FROM \"" + dto.getSchema() + "\".digital_media;\n");
                }

                statement.addBatch("DROP SCHEMA \"" + dto.getSchema() + "\" CASCADE;\n");
            }

            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (Connection connection = datasource.getConnection();
                Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);

            State.writeLog("Creating schema for '" + dto.getSchema() + "'");

            restoreService.processRestore(databaseTemplate.getFile());

            State.writeLog("Renaming schema bib4template to " + dto.getSchema());

            statement.addBatch(
                    "ALTER SCHEMA \"bib4template\" RENAME TO \"" + dto.getSchema() + "\";\n");

            if (addToGlobal) {
                statement.addBatch(
                        "INSERT INTO \""
                                + Constants.GLOBAL_SCHEMA
                                + "\".schemas (schema, name) VALUES ('"
                                + dto.getSchema()
                                + "', E'"
                                + dto.getName()
                                + "');\n");
            }

            statement.addBatch("ANALYZE;\n");

            statement.executeBatch();

            connection.commit();

            schemas = null;

            success = true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return success;
    }

    private SchemaDTO populateDTO(ResultSet rs) throws SQLException {
        SchemaDTO dto = new SchemaDTO();

        dto.setSchema(rs.getString("schema"));
        dto.setName(rs.getString("name"));
        dto.setDisabled(rs.getBoolean("disabled"));

        return dto;
    }

    @Autowired
    public void setRestoreService(RestoreService restoreService) {
        this.restoreService = restoreService;
    }
}
