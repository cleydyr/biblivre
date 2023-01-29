package biblivre.core.utils;

import java.time.LocalDate;

public class DateUtils {

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
