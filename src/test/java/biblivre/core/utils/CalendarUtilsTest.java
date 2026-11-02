package biblivre.core.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class CalendarUtilsTest {
    private static final List<Integer> MONDAY_TO_FRIDAY = List.of(2, 3, 4, 5, 6);

    @Test
    void testExpectedReturnDate() {
        assertEquals(
                LocalDate.now(),
                CalendarUtils.calculateExpectedReturnDate(LocalDate.now(), 0, MONDAY_TO_FRIDAY));

        assertEquals(
                LocalDate.of(2024, 11, 4),
                CalendarUtils.calculateExpectedReturnDate(
                        LocalDate.of(2024, 10, 21), 10, MONDAY_TO_FRIDAY));

        assertEquals(
                LocalDate.of(2025, 1, 16),
                CalendarUtils.calculateExpectedReturnDate(
                        LocalDate.of(2024, 10, 21), 50, List.of(3, 4, 5, 6)));
    }

    /**
     * When the count ends on the last business day of the week, the expected return date must roll
     * over to the next business day. Friday 2026-10-30 + 1 business day used to return Saturday
     * 2026-10-31 (issue #899).
     */
    @Test
    void calculateExpectedReturnDate_whenCountEndsBeforeWeekend_returnsNextBusinessDay() {
        assertEquals(
                LocalDate.of(2026, 11, 2),
                CalendarUtils.calculateExpectedReturnDate(
                        LocalDate.of(2026, 10, 30), 1, MONDAY_TO_FRIDAY));
    }

    /**
     * Whole weeks are skipped instead of walked day by day, and the offset is a long: 7 *
     * Integer.MAX_VALUE used to overflow into a date millions of years in the past.
     */
    @Test
    @Timeout(5)
    void calculateExpectedReturnDate_withVeryLargeLimit_isFastAndDoesNotOverflow() {
        LocalDate monday = LocalDate.of(2026, 11, 2);

        assertEquals(
                monday.plusWeeks(400_000_000),
                CalendarUtils.calculateExpectedReturnDate(
                        monday, 5 * 400_000_000, MONDAY_TO_FRIDAY));

        assertEquals(
                monday.plusWeeks(Integer.MAX_VALUE),
                CalendarUtils.calculateExpectedReturnDate(monday, Integer.MAX_VALUE, List.of(2)));
    }

    /**
     * Business-day config uses Calendar/Globalize numbering (1=Sunday … 7=Saturday). Including
     * Sunday used to throw ArrayIndexOutOfBoundsException because Sunday was stored at index 0
     * while DayOfWeek.SUNDAY is read at index 7.
     */
    @Test
    void calculateExpectedReturnDate_whenSundayIsBusinessDay_doesNotThrow() {
        // Friday 2026-08-07 — same shape as the production stacktrace
        LocalDate lendingDate = LocalDate.of(2026, 8, 7);
        List<Integer> sundayToFriday = List.of(1, 2, 3, 4, 5, 6);

        assertEquals(
                LocalDate.of(2026, 8, 14),
                CalendarUtils.calculateExpectedReturnDate(lendingDate, 6, sundayToFriday));
    }

    @Test
    void calculateExpectedReturnDate_whenAllWeekdaysAreBusinessDays_doesNotThrow() {
        LocalDate lendingDate = LocalDate.of(2026, 8, 7);
        List<Integer> allDays = List.of(1, 2, 3, 4, 5, 6, 7);

        assertEquals(
                LocalDate.of(2026, 8, 15),
                CalendarUtils.calculateExpectedReturnDate(lendingDate, 8, allDays));
    }

    @Test
    void calculateExpectedReturnDate_whenNoBusinessDayIsConfigured_addsCalendarDays() {
        assertEquals(
                LocalDate.of(2026, 8, 17),
                CalendarUtils.calculateExpectedReturnDate(LocalDate.of(2026, 8, 7), 10, List.of()));
    }

    /**
     * The cache only keys on the weekday, so two different dates that fall on the same weekday must
     * each be offset from their own start date rather than reuse a previous result.
     */
    @Test
    void calculateExpectedReturnDate_cachePathIsConsistentAcrossDatesOnSameWeekday() {
        LocalDate firstMonday = LocalDate.of(2026, 8, 3);
        LocalDate secondMonday = firstMonday.plusWeeks(5);

        LocalDate first =
                CalendarUtils.calculateExpectedReturnDate(firstMonday, 7, MONDAY_TO_FRIDAY);
        LocalDate second =
                CalendarUtils.calculateExpectedReturnDate(secondMonday, 7, MONDAY_TO_FRIDAY);
        LocalDate firstAgain =
                CalendarUtils.calculateExpectedReturnDate(firstMonday, 7, MONDAY_TO_FRIDAY);

        assertEquals(LocalDate.of(2026, 8, 12), first);
        assertEquals(first, firstAgain);
        assertEquals(first.plusWeeks(5), second);
    }

    /**
     * Calendar days added for 0..9 business days, one row per start weekday (Monday first, starting
     * 2026-01-05). Every non-zero offset lands on a business day (e.g. Friday + 1 business day =
     * Monday, not Saturday).
     */
    private static final int[][] MONDAY_TO_FRIDAY_OFFSETS = {
        {0, 1, 2, 3, 4, 7, 8, 9, 10, 11},
        {0, 1, 2, 3, 6, 7, 8, 9, 10, 13},
        {0, 1, 2, 5, 6, 7, 8, 9, 12, 13},
        {0, 1, 4, 5, 6, 7, 8, 11, 12, 13},
        {0, 3, 4, 5, 6, 7, 10, 11, 12, 13},
        {0, 3, 4, 5, 6, 9, 10, 11, 12, 13},
        {0, 2, 3, 4, 5, 8, 9, 10, 11, 12},
    };

    private static final int[][] MONDAY_WEDNESDAY_FRIDAY_OFFSETS = {
        {0, 2, 4, 7, 9, 11, 14, 16, 18, 21},
        {0, 3, 6, 8, 10, 13, 15, 17, 20, 22},
        {0, 2, 5, 7, 9, 12, 14, 16, 19, 21},
        {0, 4, 6, 8, 11, 13, 15, 18, 20, 22},
        {0, 3, 5, 7, 10, 12, 14, 17, 19, 21},
        {0, 4, 6, 9, 11, 13, 16, 18, 20, 23},
        {0, 3, 5, 8, 10, 12, 15, 17, 19, 22},
    };

    @Test
    void calculateExpectedReturnDate_matchesGoldenTable() {
        assertMatches(MONDAY_TO_FRIDAY, MONDAY_TO_FRIDAY_OFFSETS);
        assertMatches(List.of(2, 4, 6), MONDAY_WEDNESDAY_FRIDAY_OFFSETS);
    }

    @Test
    void calculateExpectedReturnDate_isNotAffectedByLaterMutationOfTheBusinessDaysList() {
        List<Integer> mutable = new ArrayList<>(MONDAY_TO_FRIDAY);
        LocalDate monday = LocalDate.of(2026, 8, 3);

        LocalDate before = CalendarUtils.calculateExpectedReturnDate(monday, 5, mutable);

        mutable.clear();
        mutable.add(2);

        assertEquals(
                before, CalendarUtils.calculateExpectedReturnDate(monday, 5, MONDAY_TO_FRIDAY));
    }

    @Test
    void calculateDateDifference_countsCalendarDays() {
        assertEquals(
                3,
                CalendarUtils.calculateDateDifference(
                        LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 4)));
        assertEquals(
                -2,
                CalendarUtils.calculateDateDifference(
                        LocalDate.of(2026, 3, 4), LocalDate.of(2026, 3, 2)));
    }

    /** Day math must not be skewed by a daylight-saving shift between the two dates. */
    @Test
    void calculateDateDifference_acrossDaylightSavingBoundary() {
        // America/Sao_Paulo has no DST today, but America/New_York does (2026-03-08).
        ZoneId zone = ZoneId.of("America/New_York");
        Instant before = LocalDate.of(2026, 3, 7).atTime(23, 30).atZone(zone).toInstant();
        Instant after = LocalDate.of(2026, 3, 9).atTime(0, 30).atZone(zone).toInstant();

        assertEquals(
                2,
                CalendarUtils.calculateDateDifference(
                        before.atZone(zone).toLocalDate(), after.atZone(zone).toLocalDate()));
    }

    @Test
    void toLocalDateInDefaultZone_usesTheSystemZone() {
        Instant instant = Instant.parse("2026-08-07T12:00:00Z");

        assertEquals(
                instant.atZone(ZoneId.systemDefault()).toLocalDate(),
                CalendarUtils.toLocalDateInDefaultZone(instant));
    }

    private static void assertMatches(List<Integer> businessDays, int[][] expectedOffsets) {
        for (int weekday = 0; weekday < expectedOffsets.length; weekday++) {
            // Repeat over several weeks so cached and uncached paths are both exercised.
            for (int week = 0; week < 3; week++) {
                LocalDate start = LocalDate.of(2026, 1, 5).plusDays(weekday).plusWeeks(week);

                for (int days = 0; days < expectedOffsets[weekday].length; days++) {
                    assertEquals(
                            start.plusDays(expectedOffsets[weekday][days]),
                            CalendarUtils.calculateExpectedReturnDate(start, days, businessDays),
                            "start=%s (%s) days=%d".formatted(start, start.getDayOfWeek(), days));
                }
            }
        }
    }
}
