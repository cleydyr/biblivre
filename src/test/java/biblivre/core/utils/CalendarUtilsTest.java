package biblivre.core.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;

public class CalendarUtilsTest {
    @Test
    void testExpectedReturnDate() {
        assertEquals(
                LocalDate.now(),
                toLocalDateInDefaultZone(
                        CalendarUtils.calculateExpectedReturnDate(
                                new Date(), 0, List.of(2, 3, 4, 5, 6))));

        assertEquals(
                LocalDate.of(2024, 11, 4),
                toLocalDateInDefaultZone(
                        CalendarUtils.calculateExpectedReturnDate(
                                toDate(LocalDate.of(2024, 10, 21)), 10, List.of(2, 3, 4, 5, 6))));

        assertEquals(
                LocalDate.of(2025, 1, 16),
                toLocalDateInDefaultZone(
                        CalendarUtils.calculateExpectedReturnDate(
                                toDate(LocalDate.of(2024, 10, 21)), 50, List.of(3, 4, 5, 6))));
    }

    LocalDate toLocalDateInDefaultZone(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
