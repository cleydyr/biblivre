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

import biblivre.core.configurations.Configurations;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

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

    public static Date calculateExpectedReturnDate(Date lendingDate, int days) {
        List<Integer> businessDays =
                Configurations.getIntArray(Constants.CONFIG_BUSINESS_DAYS, "2,3,4,5,6");

        return calculateExpectedReturnDate(lendingDate, days, businessDays);
    }

    public static Date calculateExpectedReturnDate(
            Date lendingDate, int days, List<Integer> businessDays) {
        LocalDate expectedReturnDate = toLocalDateInDefaultZone(lendingDate);

        int remaningDays = days;

        boolean[] isBusinessDay = new boolean[8];

        for (int businessDay : businessDays) {
            isBusinessDay[businessDay] = true;
        }

        while (remaningDays > 0) {
            int expectedReturnDateDayOfWeek = expectedReturnDate.getDayOfWeek().getValue();

            if (isBusinessDay[expectedReturnDateDayOfWeek]) {
                remaningDays--;
            }

            expectedReturnDate = expectedReturnDate.plusDays(1);
        }

        return toDateInDefaultZone(expectedReturnDate);
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
