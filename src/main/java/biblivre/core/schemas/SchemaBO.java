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
import biblivre.core.exceptions.ValidationException;
import biblivre.core.utils.Constants;
import biblivre.core.utils.DatabaseUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SchemaBO {
    private SchemaDAO schemaDAO;

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

    public boolean createSchema(SchemaDTO dto, File template, boolean addToGlobal) {

        File psql = DatabaseUtils.getPsql();

        if (psql == null) {
            throw new ValidationException("administration.maintenance.backup.error.psql_not_found");
        }

        String[] commands =
                new String[] {
                    psql.getAbsolutePath(), // 0
                    "--single-transaction", // 1
                    "--host", // 2
                    DatabaseUtils.getDatabaseHostName(), // 3
                    "--port", // 4
                    DatabaseUtils.getDatabasePort(), // 5
                    "-v", // 6
                    "ON_ERROR_STOP=1", // 7
                    "--file", // 8
                    "-", // 9
                };

        ProcessBuilder pb = new ProcessBuilder(commands);

        pb.redirectErrorStream(true);

        try {
            State.writeLog("Starting psql");

            Process p = pb.start();

            InputStreamReader isr =
                    new InputStreamReader(p.getInputStream(), Constants.DEFAULT_CHARSET);

            OutputStreamWriter osw =
                    new OutputStreamWriter(p.getOutputStream(), Constants.DEFAULT_CHARSET);
            try (BufferedWriter bw = new BufferedWriter(osw)) {

                Thread t =
                        new Thread(
                                () -> {
                                    String outputLine;
                                    try (final BufferedReader br = new BufferedReader(isr)) {
                                        while ((outputLine = br.readLine()) != null) {
                                            State.writeLog(outputLine);
                                        }
                                    } catch (Exception e) {
                                        logger.error("error while creating new schema", e);
                                    }
                                });

                t.start();

                if (exists(dto.getSchema())) {
                    State.writeLog("Dropping old schema");

                    if (!dto.getSchema().equals(Constants.GLOBAL_SCHEMA)) {
                        bw.write("DELETE FROM \"" + dto.getSchema() + "\".digital_media;\n");
                    }

                    bw.write("DROP SCHEMA \"" + dto.getSchema() + "\" CASCADE;\n");
                }

                State.writeLog("Creating schema for '" + dto.getSchema() + "'");

                RestoreBO.processRestore(template, bw);

                State.writeLog("Renaming schema bib4template to " + dto.getSchema());
                bw.write("ALTER SCHEMA \"bib4template\" RENAME TO \"" + dto.getSchema() + "\";\n");

                if (addToGlobal) {
                    bw.write(
                            "INSERT INTO \""
                                    + Constants.GLOBAL_SCHEMA
                                    + "\".schemas (schema, name) VALUES ('"
                                    + dto.getSchema()
                                    + "', E'"
                                    + dto.getName()
                                    + "');\n");
                }

                bw.write("ANALYZE;\n");

                bw.close();

                p.waitFor();

                return p.exitValue() == 0;
            }
        } catch (IOException | InterruptedException e) {
            SchemaBO.logger.error(e.getMessage(), e);
        }

        return false;
    }

    @Autowired
    public void setSchemaDAO(SchemaDAO schemaDAO) {
        this.schemaDAO = schemaDAO;
    }
}
