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
package biblivre.marc;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;


public enum MaterialType {
	ALL('a', "m ", true),
	BOOK('a', "m ", true),
	PAMPHLET('a', "m ", true),
	MANUSCRIPT('t', "m ", true),
	THESIS('a', "m ", true),
	PERIODIC('a', "s ", true),
	ARTICLES('a', "b ", true),
	COMPUTER_LEGIBLE('m', "m ", true),
	MAP('e', "m ", true),
	PHOTO('k', "m ", true),
	MOVIE('p', "m ", true),
	SCORE('c', "m ", true),
	MUSIC('j', "m ", true),
	NONMUSICAL_SOUND('i', "m ", true),
	OBJECT_3D('r', "m ", true),
	AUTHORITIES('z', "  ", false),
	VOCABULARY('w', "  ", false),
	HOLDINGS('u', "  ", false);

	private final static List<MaterialType> bibliographicMaterials;
	private final static List<MaterialType> searchableMaterials;
	private final static String javascriptArray;
	
	static {
		List<MaterialType> tempBibliographicMaterials = new LinkedList<MaterialType>();
		List<MaterialType> tempSearchableMaterials = new LinkedList<MaterialType>();

		StringBuffer sb = new StringBuffer();
		sb.append("[");

		for (MaterialType material : MaterialType.values()) {
			if (material.isSearchable()) {
				tempSearchableMaterials.add(material);
				if (!material.equals(MaterialType.ALL)) {
					tempBibliographicMaterials.add(material);
					sb.append("\'").append(material.toString()).append("\',");
				}
			}
		}

		sb.append("]");

		javascriptArray = sb.toString();
		bibliographicMaterials = Collections.unmodifiableList(tempBibliographicMaterials);
		searchableMaterials = Collections.unmodifiableList(tempSearchableMaterials);
	}

	private char typeOfRecord;
	private String implDefined1;
	private boolean searchable;

	private MaterialType(char typeOfRecord, String implDef1, boolean searchable) {
		this.typeOfRecord = typeOfRecord;
		this.implDefined1 = implDef1;
		this.searchable = searchable;
	}

	public char getTypeOfRecord() {
		return this.typeOfRecord;
	}

	public String getImplDefined1() {
		return this.implDefined1;
	}

	public boolean isSearchable() {
		return this.searchable;
	}

	public static MaterialType fromString(String str) {
		if (StringUtils.isBlank(str)) {
			return null;
		}

		str = str.toLowerCase();

		for (MaterialType type : MaterialType.values()) {
			if (str.equals(type.name().toLowerCase())) {
				return type;
			}
		}

		return null;
	}

	public static MaterialType fromTypeAndImplDef(char typeOfRecord, char[] implDef1) {
		String imp = String.valueOf(implDef1);
		
		for (MaterialType type : MaterialType.values()) {
			if (type.getTypeOfRecord() == typeOfRecord && type.getImplDefined1().equals(imp)) {
				return type;
			}
		}
		
		return MaterialType.BOOK;
	}
	
	public static MaterialType fromRecord(Record record) {
		MaterialType mt = null;

		if (record != null) {
			Leader leader = record.getLeader();
			mt = MaterialType.fromTypeAndImplDef(leader.getTypeOfRecord(), leader.getImplDefined1());
		}

		return (mt != null && mt != MaterialType.ALL) ? mt : MaterialType.BOOK;
	}
	
	public static List<MaterialType> bibliographicValues() {
		return bibliographicMaterials;
	}
	
	public static List<MaterialType> searchableValues() {
		return searchableMaterials;
	}
	
	public static String toJavascriptArray(){
		return javascriptArray;
	}
	
	@Override
	public String toString() {
		return this.name().toLowerCase();
	}

	public String getString() {
		return this.toString();
	}
}
