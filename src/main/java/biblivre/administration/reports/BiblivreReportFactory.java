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

import biblivre.core.SchemaThreadLocal;
import biblivre.core.translations.TranslationsMap;

public class BiblivreReportFactory {

    private BiblivreReportFactory() {}

    public static IBiblivreReport getBiblivreReport(
            ReportType type, TranslationsMap i18n, ReportsBO reportsBO) {
        BaseBiblivreReport report = null;

        switch (type) {
            case SEARCHES_BY_DATE:
                report = new SearchesByDateReport();
                break;
            case LENDINGS_BY_DATE:
                report = new LendingsByDateReport();
                break;
            case ALL_USERS:
                report = new AllUsersReport();
                break;
            case DEWEY:
                report = new DeweyReport();
                break;
            case LATE_LENDINGS:
                report = new LateReturnLendingsReport();
                break;
            case AUTHOR_BIBLIOGRAPHY:
                report = new BibliographyReport();
                break;
            case HOLDING_CREATION_BY_DATE:
                report = new HoldingCreationByDatetReport();
                break;
            case ACQUISITION:
                report = new RequestsByDateReport();
                break;
            case SUMMARY:
                report = new SummaryReport();
                break;
            case USER:
                report = new UserReport();
                break;
            case RESERVATION:
                report = new ReservationReport();
                break;
            case ASSET_HOLDING:
                report = new AssetHoldingReport();
                break;
            case ASSET_HOLDING_FULL:
                report = new AssetHoldingFullReport(false);
                break;
            case TOPOGRAPHIC_FULL:
                report = new AssetHoldingFullReport(true);
                break;
            case ASSET_HOLDING_BY_DATE:
                report = new AssetHoldingByDateReport();
                break;
            case CUSTOM_COUNT:
                report = new CustomCountReport();
                break;
            default:
                return null;
        }

        report.setI18n(i18n);
        report.setReportsBO(reportsBO);
        report.setSchema(SchemaThreadLocal.get());

        return report;
    }
}
