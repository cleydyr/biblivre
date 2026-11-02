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

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CalendarUtils {

    public static java.sql.Timestamp toSqlTimestamp(Date date) {
        return new java.sql.Timestamp(date.getTime());
    }

    /**
     * The number of calendar days to add only depends on the weekday the count starts on, the
     * number of business days requested and which weekdays are business days, so the cache is
     * bounded by 7 entries per configuration instead of growing with every distinct date.
     */
    private record DaysToAddKey(DayOfWeek startDayOfWeek, int days, List<Integer> businessDays) {
        DaysToAddKey {
            businessDays = List.copyOf(businessDays);
        }
    }

    private static final Map<DaysToAddKey, Long> expectedReturnDateCache =
            new ConcurrentHashMap<>();

    public static boolean isMidnight(Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return date.getTime() == cal.getTimeInMillis();
    }

    public static LocalDate calculateExpectedReturnDate(
            LocalDate lendingDate, int days, List<Integer> businessDays) {

        if (days == 0) {
            return lendingDate;
        }

        long daysToAdd =
                expectedReturnDateCache.computeIfAbsent(
                        new DaysToAddKey(lendingDate.getDayOfWeek(), days, businessDays),
                        key -> calculateDaysToAdd(key.startDayOfWeek(), days, businessDays));

        return lendingDate.plusDays(daysToAdd);
    }

    /**
     * Counts {@code days} business days starting on the lending day itself, then moves forward to
     * the next business day.
     *
     * <p>Only invoked as the mapping function of {@link Map#computeIfAbsent} in {@link
     * #calculateExpectedReturnDate}, so it must not mutate the cache itself.
     */
    private static long calculateDaysToAdd(
            DayOfWeek startDayOfWeek, int days, List<Integer> businessDays) {
        // Indexed by DayOfWeek.getValue() (Monday=1 … Sunday=7). Index 0 unused.
        boolean[] isBusinessDay = new boolean[8];

        for (int businessDay : businessDays) {
            isBusinessDay[toDayOfWeekValue(businessDay)] = true;
        }

        int businessDaysPerWeek = countBusinessDays(isBusinessDay);

        if (businessDaysPerWeek == 0) {
            return days;
        }

        // Any 7 consecutive days hold exactly businessDaysPerWeek business days, so whole weeks
        // can be skipped without changing the weekday. At least one business day is left for the
        // loop below. The total is a long because 7 * days overflows an int for large limits.
        int fullWeeks = days > 0 ? (days - 1) / businessDaysPerWeek : 0;

        int remainingDays = days - fullWeeks * businessDaysPerWeek;

        long daysToAdd = 7L * fullWeeks;

        DayOfWeek dayOfWeek = startDayOfWeek;

        while (remainingDays > 0) {
            if (isBusinessDay[dayOfWeek.getValue()]) {
                remainingDays--;
            }

            daysToAdd++;
            dayOfWeek = dayOfWeek.plus(1);
        }

        // The expected return date must itself be a business day.
        while (!isBusinessDay[dayOfWeek.getValue()]) {
            daysToAdd++;
            dayOfWeek = dayOfWeek.plus(1);
        }

        return daysToAdd;
    }

    /**
     * Converts {@link Calendar#DAY_OF_WEEK} values (Sunday=1 … Saturday=7), as stored in {@code
     * general.business_days}, to {@link DayOfWeek#getValue()} (Monday=1 … Sunday=7).
     */
    static int toDayOfWeekValue(int calendarDayOfWeek) {
        if (calendarDayOfWeek == Calendar.SUNDAY) {
            return DayOfWeek.SUNDAY.getValue();
        }
        return calendarDayOfWeek - 1;
    }

    private static int countBusinessDays(boolean[] isBusinessDay) {
        int count = 0;
        for (int d = DayOfWeek.MONDAY.getValue(); d <= DayOfWeek.SUNDAY.getValue(); d++) {
            if (isBusinessDay[d]) {
                count++;
            }
        }
        return count;
    }

    public static LocalDate toLocalDateInDefaultZone(Date lendingDate) {
        return toLocalDateInDefaultZone(lendingDate.toInstant());
    }

    public static LocalDate toLocalDateInDefaultZone(Instant instant) {
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static int calculateDateDifference(Date initialDate, Date finalDate) {
        return calculateDateDifference(
                toLocalDateInDefaultZone(initialDate), toLocalDateInDefaultZone(finalDate));
    }

    public static int calculateDateDifference(LocalDate initialDate, LocalDate finalDate) {
        return (int) ChronoUnit.DAYS.between(initialDate, finalDate);
    }
}
