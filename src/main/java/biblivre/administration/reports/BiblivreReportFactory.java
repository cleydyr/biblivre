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


public class BiblivreReportFactory {

	public static IBiblivreReport getBiblivreReport(ReportType type, ReportsDAO dao) {
		switch (type) {
			case SEARCHES_BY_DATE: return new SearchesByDateReport(dao); 
			case LENDINGS_BY_DATE: return new LendingsByDateReport(dao); 
			case ALL_USERS: return new AllUsersReport(dao); 
			case DEWEY: return new DeweyReport(dao); 
			case LATE_LENDINGS: return new LateReturnLendingsReport(dao); 
			case AUTHOR_BIBLIOGRAPHY: return new BibliographyReport(dao); 
			case HOLDING_CREATION_BY_DATE: return new HoldingCreationByDatetReport(dao);
			case ACQUISITION: return new RequestsByDateReport(dao); 
			case SUMMARY: return new SummaryReport(dao); 
			case USER: return new UserReport(dao);
			case RESERVATION: return new ReservationReport(dao); 
			case ASSET_HOLDING: return new AssetHoldingReport(dao); 
			case ASSET_HOLDING_FULL: return new AssetHoldingFullReport(false, dao); 
			case TOPOGRAPHIC_FULL: return new AssetHoldingFullReport(true, dao); 
			case ASSET_HOLDING_BY_DATE: return new AssetHoldingByDateReport(dao); 
			case CUSTOM_COUNT: return new CustomCountReport(dao);
			default:
				return null;
		}
	}

}
