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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import javax.measure.MetricPrefix;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import org.apache.commons.lang3.time.DateParser;
import org.apache.commons.lang3.time.DatePrinter;
import org.apache.commons.lang3.time.FastDateFormat;
import tech.units.indriya.unit.Units;

public class Constants {

    public static final int ADMIN_LOGGED_USER_ID = 0;
    public static final int DEFAULT_POSTGRESQL_PORT = 5432;
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final Charset WINDOWS_CHARSET = Charset.forName("cp1252");

    public static final String BIBLIVRE = "Biblivre";
    public static final String BIBLIVRE_VERSION = "5.0.5";
    public static final String UPDATE_URL = "http://update.biblivre.org.br";
    public static final String DOWNLOAD_URL = "http://update.biblivre.org.br";

    public static final DateParser DEFAULT_DATE_FORMAT =
            FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss");
    public static final DateParser DEFAULT_DATE_FORMAT_TIMEZONE =
            FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    public static final DatePrinter DEFAULT_DATE_PRINTER_TIMEZONE =
            FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss");

    public static final String LINE_BREAK = System.getProperty("line.separator");
    public static final float MM_UNIT = 72.0f / 25.4f;

    // Configurations
    public static final String CONFIG_DEFAULT_LANGUAGE = "general.default_language";
    public static final String CONFIG_MULTI_SCHEMA = "general.multi_schema";
    public static final String CONFIG_TITLE = "general.title";
    public static final String CONFIG_SUBTITLE = "general.subtitle";
    public static final String CONFIG_UID = "general.uid";
    public static final String CONFIG_BUSINESS_DAYS = "general.business_days";
    public static final String CONFIG_CURRENCY = "general.currency";

    public static final String CONFIG_NEW_LIBRARY = "setup.new_library";

    public static final String CONFIG_ACCESSION_NUMBER_PREFIX =
            "cataloging.accession_number_prefix";

    public static final String CONFIG_BACKUP_PATH = "general.backup_path";

    public static final String CONFIG_SEARCH_RESULTS_PER_PAGE = "search.results_per_page";
    public static final String CONFIG_SEARCH_RESULT_LIMIT = "search.result_limit";

    public static final String CONFIG_LENDING_PRINTER_TYPE =
            "circulation.lending_receipt.printer.type";

    // Translations
    public static final String TRANSLATION_RECORD_TAB_FIELD_LABEL =
            "cataloging.tab.record.custom.field_label.";
    public static final String TRANSLATION_INDEXING_GROUP = "cataloging.custom.indexing_group.";
    public static final String TRANSLATION_USER_FIELD = "circulation.custom.user_field.";
    public static final String TRANSLATION_FORMAT_DATE = "format.date";
    public static final String TRANSLATION_FORMAT_DATETIME = "format.datetime";

    // Media server
    public static final int DEFAULT_BUFFER_SIZE = 10240; // 10 KB
    public static final long DEFAULT_EXPIRE_TIME = 9676800000L; // 16 weeks
    public static final String MULTIPART_BOUNDARY = "MULTIPART_BYTERANGES";

    public static final String GLOBAL_SCHEMA = "global";

    // The constants below should not be final
    public static final String SINGLE_SCHEMA = "single";

    // Label print configuration
    public static final String CONFIG_LABEL_PRINT_PARAGRAPH_ALIGNMENT =
            "holding.label_print_paragraph_alignment";

    public static final Unit<Length> USER_UNIT =
            MetricPrefix.CENTI(Units.METRE).multiply(2.54).divide(72);
    public static final Function<Quantity<Length>, Float> FROM_CM =
            q -> q.to(USER_UNIT).getValue().floatValue();

    public static final String JSON_EMPTY_OBJECT_STR = "{}";

    public static final String DATABASE_HOST_NAME = "DATABASE_HOST_NAME";
    public static final String DATABASE_PORT = "DATABASE_PORT";
    public static final String DATABASE_NAME = "PGDATABASE";
    public static final String DATABASE_PASSWORD = "PGPASSWORD";
    public static final String DATABASE_USERNAME = "PGUSER";
    public static final String PSQL_PATH = "PSQL_PATH";
    public static final String PGDUMP_PATH = "PGDUMP_PATH";
    public static final String DEFAULT_DATABASE_NAME = "biblivre4";
    public static final String DEFAULT_DATABASE_PASSWORD = "abracadabra";
    public static final String DEFAULT_DATABASE_USERNAME = "biblivre";

    public static final char RECORD_SEPARATOR = 0x1e;
    public static final String DEFAULT_CHARSET_NAME = DEFAULT_CHARSET.name();
}
