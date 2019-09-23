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

import static biblivre.administration.reports.ReportConstants.*;

public enum ReportType {
	
	ACQUISITION(ACQUISITION_ID, "rp01_", true),
	SUMMARY(SUMMARY_ID, "rp02_", false),
	DEWEY(DEWEY_ID, "rp03_", false),
	HOLDING_CREATION_BY_DATE(HOLDING_CREATION_BY_DATE_ID, "rp04_", true),
	AUTHOR_BIBLIOGRAPHY(AUTHOR_BIBLIOGRAPHY_ID, "rp05_", false),
	USER(USER_ID, "rp06_", false),
	ALL_USERS(ALL_USERS_ID, "rp07_", false),
	LATE_LENDINGS(LATE_LENDINGS_ID, "rp08_", false),
	SEARCHES_BY_DATE(SEARCHES_BY_DATE_ID, "rp09_", true),
	LENDINGS_BY_DATE(LENDINGS_BY_DATE_ID, "rp10_", true),
	RESERVATION(RESERVATION_ID, "rp12_", false),
	ASSET_HOLDING(ASSET_HOLDING_ID, "rp13_", false),
	ASSET_HOLDING_FULL(ASSET_HOLDING_FULL_ID, "rp14_", false),
	TOPOGRAPHIC_FULL(TOPOGRAPHIC_FULL_ID, "rp15_", false),
	ASSET_HOLDING_BY_DATE(ASSET_HOLDING_BY_DATE_ID, "rp16_", true),
	CUSTOM_COUNT(CUSTOM_COUNT_ID, "rp17_", false);

	private String id;
	private String name;
	private boolean timePeriod;

	private ReportType(String id, String name, boolean timePeriod) {
		this.id = id;
		this.name = name;
		this.timePeriod = timePeriod;
	}
	
	public final String getId() {
		return this.id;
	}

	public final String getName() {
		return this.name;
	}

	public boolean isTimePeriod() {
		return this.timePeriod;
	}
	
	public static ReportType getById(final String id) {
		for (ReportType type : values()) {
			if (type.getId().equals(id)) {
				return type;
			}
		}
		return null;
	}
	
}

