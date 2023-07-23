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
package biblivre.core.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseUtils {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);

    public static File getPgDump() {
        String pgDumpPath = getPGDumpPath();

        if (StringUtils.isNotBlank(pgDumpPath)) {
            return new File(pgDumpPath);
        }

        return DatabaseUtils.getFromFilesystem(DatabaseUtils.getPgDumpFilename());
    }

    public static File getPsql() {
        String psqlPath = getPsqlPath();

        if (StringUtils.isNotBlank(psqlPath)) {
            return new File(psqlPath);
        }

        return DatabaseUtils.getFromFilesystem(DatabaseUtils.getPsqlFilename());
    }

    public static String getDatabaseHostName() {
        return getConfigFromEnv(
                Constants.DATABASE_HOST_NAME, InetAddress.getLoopbackAddress().getHostName());
    }

    public static String getDatabaseName() {
        return getConfigFromEnv(Constants.DATABASE_NAME, Constants.DEFAULT_DATABASE_NAME);
    }

    public static String getDatabasePort() {
        return getConfigFromEnv(
                Constants.DATABASE_PORT, String.valueOf(Constants.DEFAULT_POSTGRESQL_PORT));
    }

    public static String getDatabasePassword() {
        return getConfigFromEnv(Constants.DATABASE_PASSWORD, Constants.DEFAULT_DATABASE_PASSWORD);
    }

    public static String getDatabaseUsername() {
        return getConfigFromEnv(Constants.DATABASE_USERNAME, Constants.DEFAULT_DATABASE_USERNAME);
    }

    public static String getPsqlPath() {
        return getConfigFromEnv(Constants.PSQL_PATH, null);
    }

    public static String getPGDumpPath() {
        return getConfigFromEnv(Constants.PGDUMP_PATH, null);
    }

    private static String getConfigFromEnv(String key, String defaultValue) {
        String value = System.getenv(key);

        if (value != null) {
            return value;
        }

        return defaultValue;
    }

    private static File getFromFilesystem(String fileName) {
        String os = System.getProperty("os.name").toUpperCase();

        if (os.contains("WINDOWS")) {
            return DatabaseUtils.getWindows(fileName);
        } else if (os.contains("LINUX")) {
            return DatabaseUtils.getLinux(fileName);
        } else if (os.contains("MAC OS X")) {
            return DatabaseUtils.getLinux(fileName);
        } else {
            return null;
        }
    }

    private static String getPgDumpFilename() {
        String os = System.getProperty("os.name").toUpperCase();

        if (os.contains("WINDOWS")) {
            return "pg_dump.exe";
        } else {
            return "pg_dump";
        }
    }

    private static String getPsqlFilename() {
        String os = System.getProperty("os.name").toUpperCase();

        if (os.contains("WINDOWS")) {
            return "psql.exe";
        } else {
            return "psql";
        }
    }

    private static File getMacOs(String filename) {
        String[] commands;

        commands =
                new String[] {
                    "/bin/sh",
                    "-c",
                    "\"/bin/ps axwwww -o comm | grep -v grep | grep postgres$ | sed 's/postgres$//'\""
                };

        String path = DatabaseUtils.processPatternMatcher(commands, "(.*)", 1);
        return new File(path, filename);
    }

    private static File getLinux(String filename) {
        ProcessBuilder pb = whichCommand(filename);

        pb.redirectErrorStream(true);

        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(pb.start().getInputStream()))) {

            String line = reader.readLine();

            return new File(line);
        } catch (Exception e) {
            return null;
        }
    }

    private static ProcessBuilder whichCommand(String filename) {
        String[] commands = new String[] {"/bin/sh", "-c", "which " + filename};

        return new ProcessBuilder(commands);
    }

    private static File getWindows(String filename) {
        String[] commands;

        // Step 1 - Detecting current PostgreSQL service name
        commands =
                new String[] {
                    "tasklist", "/nh", "/svc", "/fi", "imagename eq pg_ctl.exe", "/fo", "csv"
                };

        String postgresServiceName =
                DatabaseUtils.processPatternMatcher(commands, "([^\"]+)\"$", 1);
        if (postgresServiceName == null) {
            return null;
        }

        // Step 2 - Detect PostgreSQL Product Code
        String postgresProductCode = null;
        String[] regkeys =
                new String[] {
                    "HKLM\\SOFTWARE\\PostgreSQL\\Services\\" + postgresServiceName,
                    "HKLM\\SOFTWARE\\Wow6432Node\\PostgreSQL\\Services\\" + postgresServiceName
                };
        for (String regkey : regkeys) {
            postgresProductCode = getRegValue(regkey, "Product Code");
            if (postgresProductCode != null) {
                break;
            }
        }
        if (postgresProductCode == null) {
            return null;
        }

        // Step 3 - Detect PostgreSQL Base Directory
        String postgresBaseDirectory = null;
        regkeys =
                new String[] {
                    "HKLM\\SOFTWARE\\PostgreSQL\\Installations\\" + postgresProductCode,
                    "HKLM\\SOFTWARE\\Wow6432Node\\PostgreSQL\\Installations\\" + postgresProductCode
                };
        for (String regkey : regkeys) {
            postgresBaseDirectory = getRegValue(regkey, "Base Directory");
            if (postgresBaseDirectory != null) {
                break;
            }
        }
        if (postgresBaseDirectory == null) {
            return null;
        }

        File file = new File(postgresBaseDirectory + File.separator + "bin", filename);
        return file.exists() ? file : null;
    }

    private static String getRegValue(String dir, String key) {
        String[] commands = new String[] {"reg", "query", dir, "/V", key};

        return DatabaseUtils.processPatternMatcher(commands, "REG_SZ\\s+(.+)$", 1);
    }

    private static String processPatternMatcher(String[] commands, String regex, int group) {
        return DatabaseUtils.processPatternMatcher(commands, regex, group, null);
    }

    private static String processPatternMatcher(
            String[] commands, String regex, int group, String directory) {
        ProcessBuilder pb = new ProcessBuilder(commands);
        if (directory != null) {
            pb.directory(new File(directory));
        }

        pb.redirectErrorStream(true);

        try (BufferedReader br =
                new BufferedReader(new InputStreamReader(pb.start().getInputStream()))) {
            String line;

            Pattern pattern = Pattern.compile(regex);
            while ((line = br.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        } catch (IOException e) {
            DatabaseUtils.logger.error(e.getMessage(), e);
        }

        return null;
    }
}
