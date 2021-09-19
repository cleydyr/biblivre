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
import biblivre.core.BiblivreInitializer;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.StaticBO;
import biblivre.core.Updates;
import biblivre.core.configurations.Configurations;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.utils.Constants;
import biblivre.core.utils.DatabaseUtils;
import biblivre.z3950.server.Z3950ServerBO;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Schemas extends StaticBO {

    protected static Logger logger = LoggerFactory.getLogger(Schemas.class);

    private static Set<SchemaDTO> schemas;
    private static final List<String> schemaBlacklist = new ArrayList<>();
    private static final String SCHEMA_VALIDATION_REGEX = "([a-zA-Z0-9_]+)";

    static {
        schemaBlacklist.add("schema");
        schemaBlacklist.add("public");
        schemaBlacklist.add("template");
        schemaBlacklist.add("bib4template");
        schemaBlacklist.add("DigitalMediaController");
    }

    private Schemas() {}

    static {
        Schemas.reset();
    }

    public static void reset() {
        Schemas.schemas = null;
    }

    public static boolean isNotLoaded(String schema) {
        return !Schemas.isLoaded(schema);
    }

    public static boolean isMultipleSchemasEnabled() {
        return Configurations.getBoolean(Constants.GLOBAL_SCHEMA, Constants.CONFIG_MULTI_SCHEMA);
    }

    public static void reload() {
    	SchemaThreadLocal.setSchema(Constants.GLOBAL_SCHEMA);

        SchemasDAO dao = SchemasDAO.getInstance();

        Set<SchemaDTO> schemas = dao.list();

        if (schemas.size() == 0) {
            schemas.add(new SchemaDTO(Constants.SINGLE_SCHEMA, "Biblivre V"));
        }

        Schemas.schemas = schemas;

        for (SchemaDTO dto : Schemas.schemas) {
            if (!dto.isDisabled()) {
                Updates.schemaUpdate(dto.getSchema());
            }
        }

        if (!Schemas.isLoaded(Constants.SINGLE_SCHEMA)) {
            for (SchemaDTO dto : Schemas.schemas) {
                if (!dto.isDisabled()) {
                    Constants.SINGLE_SCHEMA = dto.getSchema();
                    break;
                }
            }
        }

        Z3950ServerBO.setSingleSchema(Constants.SINGLE_SCHEMA);
        BiblivreInitializer.reloadZ3950Server();

        SchemaThreadLocal.remove();
    }

    public static SchemaDTO getSchema(String schema) {
        if (Schemas.schemas == null) {
            Schemas.reload();
        }

        for (SchemaDTO dto : Schemas.schemas) {
            if (dto.getSchema().equals(schema)) {
                return dto;
            }
        }

        return null;
    }

    public static int countEnabledSchemas() {
        if (Schemas.schemas == null) {
            Schemas.reload();
        }

        int count = 0;
        for (SchemaDTO d : Schemas.schemas) {
            if (!d.isDisabled()) {
                count++;
            }
        }

        return count;
    }

    public static Set<SchemaDTO> getSchemas() {
        if (Schemas.schemas == null) {
            Schemas.reload();
        }

        return Schemas.schemas;
    }

    public static Set<String> getEnabledSchemasList() {
        if (Schemas.schemas == null) {
            Schemas.reload();
        }

        Set<String> set = new HashSet<>();
        for (SchemaDTO schema : Schemas.schemas) {
            if (!schema.isDisabled()) {
                set.add(schema.getSchema());
            }
        }

        return set;
    }

    public static boolean isLoaded(String schema) {
        if (Schemas.schemas == null) {
            Schemas.reload();
        }

        if (StringUtils.isBlank(schema)) {
            return false;
        }

        if (schema.equals(Constants.GLOBAL_SCHEMA)) {
            return true;
        }

        for (SchemaDTO dto : Schemas.schemas) {
            if (schema.equals(dto.getSchema())) {
                return !dto.isDisabled();
            }
        }

        return false;
    }

    public static boolean isValidName(String name) {
        if (StringUtils.isBlank(name)) {
            return false;
        }

        if (name.startsWith("pg")) {
            return false;
        }

        if (schemaBlacklist.contains(name)) {
            return false;
        }

        if (!name.matches(SCHEMA_VALIDATION_REGEX)) {
            return false;
        }

        return true;
    }

    public static boolean disable(SchemaDTO dto) {
    	SchemaThreadLocal.setSchema(Constants.GLOBAL_SCHEMA);

        SchemasDAO dao = SchemasDAO.getInstance();

        dto.setDisabled(true);

        boolean success = dao.save(dto);

        SchemaThreadLocal.remove();

        Schemas.reset();
        
        return success;
    }

    public static boolean enable(SchemaDTO dto) {
    	SchemaThreadLocal.setSchema(Constants.GLOBAL_SCHEMA);

        SchemasDAO dao = SchemasDAO.getInstance();

        dto.setDisabled(false);

        boolean success = dao.save(dto);

        SchemaThreadLocal.remove();

        Schemas.reset();

        return success;
    }

    public static boolean deleteSchema(SchemaDTO dto) {
    	SchemaThreadLocal.setSchema(Constants.GLOBAL_SCHEMA);

        SchemasDAO dao = SchemasDAO.getInstance();

        StaticBO.resetCache();

        boolean success = dao.delete(dto);

        SchemaThreadLocal.remove();

		return success;
    }

    public static boolean exists(String schema) {
    	SchemaThreadLocal.setSchema(Constants.GLOBAL_SCHEMA);

        SchemasDAO schemasDAO = SchemasDAO.getInstance();

		boolean exists = schemasDAO.exists(schema);

		SchemaThreadLocal.remove();

		return exists;
    }

    public static boolean createSchema(SchemaDTO dto, File template, boolean addToGlobal) {

        File psql = DatabaseUtils.getPsql(Constants.GLOBAL_SCHEMA);

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

        BufferedWriter bw = null;

        try {
            State.writeLog("Starting psql");

            Process p = pb.start();

            InputStreamReader isr =
                    new InputStreamReader(p.getInputStream(), Constants.DEFAULT_CHARSET);

            OutputStreamWriter osw =
                    new OutputStreamWriter(p.getOutputStream(), Constants.DEFAULT_CHARSET);
            bw = new BufferedWriter(osw);

            Thread t =
                    new Thread(
                            new Runnable() {

                                @Override
                                public void run() {
                                    String outputLine;
                                    try (final BufferedReader br = new BufferedReader(isr)) {
                                        while ((outputLine = br.readLine()) != null) {
                                            State.writeLog(outputLine);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

            t.start();

            if (Schemas.exists(dto.getSchema())) {
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

            Schemas.reset();

            return p.exitValue() == 0;
        } catch (IOException e) {
            Schemas.logger.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            Schemas.logger.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(bw);
        }

        return false;
    }
}
