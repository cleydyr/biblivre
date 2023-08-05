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
package biblivre.administration.reports;

import org.apache.commons.lang3.StringUtils;

public final class ReportUtils {

    public static String formatDeweyString(final String dewey, final int digits) {
        if (StringUtils.isBlank(dewey)) {
            return "";
        }

        if (digits == -1) {
            return dewey;
        }

        StringBuilder formattedDewey =
                dewey.chars()
                        .filter(Character::isDigit)
                        .limit(digits)
                        .collect(
                                StringBuilder::new,
                                StringBuilder::appendCodePoint,
                                StringBuilder::append);

        if (digits < 3 && formattedDewey.length() < 3) {
            formattedDewey.append("0".repeat(Math.max(0, 3 - formattedDewey.length())));
        }

        return formattedDewey.toString();
    }
}
