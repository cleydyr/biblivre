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

    /**
     * Business-day config uses Calendar/Globalize numbering (1=Sunday … 7=Saturday). Including
     * Sunday used to throw ArrayIndexOutOfBoundsException because Sunday was stored at index 0
     * while DayOfWeek.SUNDAY is read at index 7.
     */
    @Test
    void calculateExpectedReturnDate_whenSundayIsBusinessDay_doesNotThrow() {
        // Friday 2026-08-07 — same shape as the production stacktrace
        Date lendingDate = toDate(LocalDate.of(2026, 8, 7));
        List<Integer> sundayToFriday = List.of(1, 2, 3, 4, 5, 6);

        assertEquals(
                LocalDate.of(2026, 8, 14),
                toLocalDateInDefaultZone(
                        CalendarUtils.calculateExpectedReturnDate(lendingDate, 6, sundayToFriday)));
    }

    @Test
    void calculateExpectedReturnDate_whenAllWeekdaysAreBusinessDays_doesNotThrow() {
        Date lendingDate = toDate(LocalDate.of(2026, 8, 7));
        List<Integer> allDays = List.of(1, 2, 3, 4, 5, 6, 7);

        assertEquals(
                LocalDate.of(2026, 8, 15),
                toLocalDateInDefaultZone(
                        CalendarUtils.calculateExpectedReturnDate(lendingDate, 8, allDays)));
    }

    LocalDate toLocalDateInDefaultZone(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
