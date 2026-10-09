package biblivre.core.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    /** Thread-safe replacement for the previously shared {@code SimpleDateFormat} instances. */
    public static final DateTimeFormatter DD_MM_YYYY =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ROOT);

    private static final DateTimeFormatter YYYY_MM_DD =
            DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ROOT);

    /**
     * Formats a {@link Date} as dd/MM/yyyy. Handles {@code java.sql.Date} values, whose {@code
     * toInstant()} throws {@code UnsupportedOperationException}.
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }

        LocalDate localDate;
        if (date instanceof java.sql.Date) {
            localDate = ((java.sql.Date) date).toLocalDate();
        } else {
            localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }

        return DD_MM_YYYY.format(localDate);
    }

    /** Converts an ISO date string (yyyy-MM-dd) to the dd/MM/yyyy display format. */
    public static String formatDate(String date) {
        if (date == null || date.isEmpty()) {
            return "";
        }

        return DD_MM_YYYY.format(LocalDate.parse(date, YYYY_MM_DD));
    }

    public static boolean isOpen(LocalDate returnDate, String schema) {
        // TODO use configuration
        return true;
    }

    public static LocalDate addOpenedDays(LocalDate today, int days, String schema) {
        LocalDate returnDate = today;

        while (days > 0) {
            if (isOpen(returnDate, schema)) {
                days--;
            }

            returnDate = returnDate.plusDays(1);
        }

        return returnDate;
    }
}
