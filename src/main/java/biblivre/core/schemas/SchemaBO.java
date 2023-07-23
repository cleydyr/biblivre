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

import biblivre.administration.backup.RestoreBO;
import biblivre.administration.setup.State;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.UpdatesDAO;
import biblivre.core.utils.Constants;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class SchemaBO {
    private SchemaDAO schemaDAO;

    private Resource databaseTemplate;

    protected static final Logger logger = LoggerFactory.getLogger(SchemaBO.class);

    private static final Collection<String> SCHEMA_BLACKLIST =
            Set.of("schema", "public", "template", "bib4template", "DigitalMediaController");

    private static final Predicate<String> SCHEMA_VALID =
            Pattern.compile("([a-zA-Z0-9_]+)").asMatchPredicate();

    public boolean isNotLoaded(String schema) {
        return !isLoaded(schema);
    }

    public SchemaDTO getSchema(String schema) {
        for (SchemaDTO dto : getSchemas()) {
            if (dto.getSchema().equals(schema)) {
                return dto;
            }
        }

        return null;
    }

    public int countEnabledSchemas() {
        int count = 0;

        for (SchemaDTO d : getSchemas()) {
            if (!d.isDisabled()) {
                count++;
            }
        }

        return count;
    }

    public Set<SchemaDTO> getSchemas() {
        return SchemaThreadLocal.withGlobalSchema(
                () -> {
                    Set<SchemaDTO> schemas = schemaDAO.list();

                    if (schemas.size() == 0) {
                        schemas.add(new SchemaDTO(Constants.SINGLE_SCHEMA, "Biblivre V"));
                    }

                    return schemas;
                });
    }

    public Set<String> getEnabledSchemasList() {
        Set<String> set = new HashSet<>();
        for (SchemaDTO schema : getSchemas()) {
            if (!schema.isDisabled()) {
                set.add(schema.getSchema());
            }
        }

        return set;
    }

    public boolean isLoaded(String schema) {
        if (StringUtils.isBlank(schema)) {
            return false;
        }

        if (schema.equals(Constants.GLOBAL_SCHEMA)) {
            return true;
        }

        for (SchemaDTO dto : getSchemas()) {
            if (schema.equals(dto.getSchema())) {
                return !dto.isDisabled();
            }
        }

        return false;
    }

    public static boolean isInvalidName(String name) {
        if (StringUtils.isBlank(name)) {
            return true;
        }

        if (name.startsWith("pg")) {
            return true;
        }

        if (SCHEMA_BLACKLIST.contains(name)) {
            return true;
        }

        return !SCHEMA_VALID.test(name);
    }

    public boolean disable(SchemaDTO dto) {
        return SchemaThreadLocal.withGlobalSchema(
                () -> {
                    dto.setDisabled(true);

                    return schemaDAO.save(dto);
                });
    }

    public boolean enable(SchemaDTO dto) {
        return SchemaThreadLocal.withGlobalSchema(
                () -> {
                    dto.setDisabled(false);

                    return schemaDAO.save(dto);
                });
    }

    public boolean deleteSchema(SchemaDTO dto) {
        return SchemaThreadLocal.withGlobalSchema(() -> schemaDAO.delete(dto));
    }

    public boolean exists(String schema) {
        return SchemaThreadLocal.withGlobalSchema(() -> schemaDAO.exists(schema));
    }

    public boolean createSchema(SchemaDTO dto, boolean addToGlobal) {
        UpdatesDAO updatesDAO = UpdatesDAO.getInstance();

        try (Connection connection = updatesDAO.beginUpdate();
                Statement statement = connection.createStatement()) {

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

        try (Connection connection = updatesDAO.beginUpdate();
                Statement statement = connection.createStatement()) {
            State.writeLog("Creating schema for '" + dto.getSchema() + "'");

            RestoreBO.processRestore(databaseTemplate.getFile());

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

            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return false;
    }

    @Autowired
    public void setSchemaDAO(SchemaDAO schemaDAO) {
        this.schemaDAO = schemaDAO;
    }

    @Value("classpath:META-INF/sql/biblivre-template.sql")
    public void setDatabaseTemplate(Resource databaseTemplate) {
        this.databaseTemplate = databaseTemplate;
    }
}
