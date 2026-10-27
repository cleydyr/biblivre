package biblivre.circulation.lending;

import biblivre.core.utils.DateUtils;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

/**
 * Formats the dates printed on lending receipts using the patterns of the active translation.
 *
 * <p>Instants are rendered in the JVM default zone (configured through the TZ environment
 * variable). It is public so the receipt template can call it.
 */
public final class ReceiptDateFormatter {
    private static final DateTimeFormatter DEFAULT_DATE = DateUtils.DD_MM_YYYY;

    private static final DateTimeFormatter DEFAULT_DATE_TIME =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.ROOT);

    private final DateTimeFormatter date;
    private final DateTimeFormatter dateTime;

    public ReceiptDateFormatter(String datePattern, String dateTimePattern) {
        this.date = formatter(datePattern, DEFAULT_DATE);
        this.dateTime = formatter(dateTimePattern, DEFAULT_DATE_TIME).withZone(zone());
    }

    public String formatDate(LocalDate value) {
        return value == null ? "" : date.format(value);
    }

    public String formatDateTime(Instant value) {
        return value == null ? "" : dateTime.format(value);
    }

    /** For the audit timestamps still held as {@link Date} by {@code AbstractDTO}. */
    public String formatDateTime(Date value) {
        return value == null ? "" : dateTime.format(value.toInstant());
    }

    private static ZoneId zone() {
        return ZoneId.systemDefault();
    }

    /**
     * Translations are free text, and some of them hold patterns that were only ever tolerated by
     * {@link java.text.SimpleDateFormat} (the Spanish date format uses {@code aaaa}, which is an
     * AM/PM marker, not a year). Fall back to the default layout instead of failing the receipt.
     */
    private static DateTimeFormatter formatter(String pattern, DateTimeFormatter fallback) {
        if (pattern == null || pattern.isBlank()) {
            return fallback;
        }

        try {
            return DateTimeFormatter.ofPattern(pattern);
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }
}
