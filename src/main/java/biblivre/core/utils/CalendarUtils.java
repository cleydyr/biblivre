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

    private static final Map<DaysToAddKey, Integer> expectedReturnDateCache =
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

        int daysToAdd =
                expectedReturnDateCache.computeIfAbsent(
                        new DaysToAddKey(lendingDate.getDayOfWeek(), days, businessDays),
                        key -> calculateDaysToAdd(key.startDayOfWeek(), days, businessDays));

        return lendingDate.plusDays(daysToAdd);
    }

    private static int calculateDaysToAdd(
            DayOfWeek startDayOfWeek, int days, List<Integer> businessDays) {
        // Only invoked from {@link Map#computeIfAbsent} below, so the key is known to be
        // absent; the mapping function must not mutate the cache itself.
        int remainingDays = days;

        // Indexed by DayOfWeek.getValue() (Monday=1 … Sunday=7). Index 0 unused.
        boolean[] isBusinessDay = new boolean[8];

        for (int businessDay : businessDays) {
            isBusinessDay[toDayOfWeekValue(businessDay)] = true;
        }

        int businessDaysPerWeek = countBusinessDays(isBusinessDay);

        if (businessDaysPerWeek == 0) {
            return days;
        }

        int daysToAdd = 0;

        for (int i = startDayOfWeek.getValue();
                i <= DayOfWeek.SUNDAY.getValue() && remainingDays > 0;
                i++) {
            daysToAdd++;

            if (isBusinessDay[i]) {
                remainingDays--;
            }
        }

        if (remainingDays > 0) {
            int weeks = remainingDays / businessDaysPerWeek;

            remainingDays -= weeks * businessDaysPerWeek;

            daysToAdd += weeks * 7;
        }

        int i = DayOfWeek.MONDAY.getValue();
        while (remainingDays > 0 || !isBusinessDay[i]) {
            daysToAdd++;

            if (isBusinessDay[i]) {
                remainingDays--;
            }

            i++;
            if (i > DayOfWeek.SUNDAY.getValue()) {
                i = DayOfWeek.MONDAY.getValue();
            }
        }

        assert remainingDays == 0;

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
