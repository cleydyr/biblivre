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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class CalendarUtils {

    public static java.sql.Date toSqlDate(Date date) {
        return new java.sql.Date(date.getTime());
    }

    public static java.sql.Timestamp toSqlTimestamp(Date date) {
        return new java.sql.Timestamp(date.getTime());
    }

    public static boolean isMidnight(Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return date.getTime() == cal.getTimeInMillis();
    }

    public static Date calculateExpectedReturnDate(
            Date lendingDate, int days, List<Integer> businessDays) {
        LocalDate expectedReturnDate = toLocalDateInDefaultZone(lendingDate);

        int remainingDays = days;

        boolean[] isBusinessDay = new boolean[8];

        for (int businessDay : businessDays) {
            isBusinessDay[businessDay - 1] = true;
        }

        int daysToAdd = 0;

        for (int i = expectedReturnDate.getDayOfWeek().getValue();
                i <= DayOfWeek.SUNDAY.getValue() && remainingDays > 0;
                i++) {
            daysToAdd++;

            if (isBusinessDay[i]) {
                remainingDays--;
            }
        }

        if (remainingDays > 0) {
            int businessDaysPerWeek = businessDays.size();

            int weeks = remainingDays / businessDaysPerWeek;

            remainingDays -= weeks * businessDaysPerWeek;

            daysToAdd += weeks * 7;
        }

        for (int i = DayOfWeek.MONDAY.getValue();
                (remainingDays > 0 || !isBusinessDay[i]) && i <= DayOfWeek.SUNDAY.getValue();
                i++) {

            daysToAdd++;

            if (isBusinessDay[i]) {
                remainingDays--;
            }
        }

        assert remainingDays == 0;

        return toDateInDefaultZone(expectedReturnDate.plusDays(daysToAdd));
    }

    public static Date toDateInDefaultZone(java.time.LocalDate expectedReturnDate) {
        return Date.from(
                expectedReturnDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static java.time.LocalDate toLocalDateInDefaultZone(Date lendingDate) {
        return lendingDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static int calculateDateDifference(Date initialDate, Date finalDate) {
        LocalDate initialLocalDate = toLocalDateInDefaultZone(initialDate);

        LocalDate finalLocalDate = toLocalDateInDefaultZone(finalDate);

        return (int) ChronoUnit.DAYS.between(initialLocalDate, finalLocalDate);
    }
}
