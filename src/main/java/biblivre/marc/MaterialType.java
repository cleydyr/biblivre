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

import biblivre.cataloging.enums.RecordType;
import biblivre.core.utils.BiblivreEnum;
import biblivre.core.utils.CharPool;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import org.apache.commons.lang3.EnumUtils;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;

@Getter
public enum MaterialType implements BiblivreEnum {
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
    AUTHORITIES('z', "  ", false, RecordType.AUTHORITIES),
    VOCABULARY('w', "  ", false, RecordType.VOCABULARY),
    HOLDINGS('u', "  ", false, RecordType.HOLDING);

    private static final List<MaterialType> bibliographicMaterials;
    private static final List<MaterialType> searchableMaterials;
    private static final String javascriptArray;

    static {
        List<MaterialType> tempBibliographicMaterials = new ArrayList<>();
        List<MaterialType> tempSearchableMaterials = new ArrayList<>();

        StringBuilder sb = new StringBuilder();

        sb.append(CharPool.OPEN_BRACKET);

        for (MaterialType material : MaterialType.values()) {
            if (material.isSearchable()) {
                tempSearchableMaterials.add(material);
                if (!material.equals(MaterialType.ALL)) {
                    tempBibliographicMaterials.add(material);
                    sb.append(CharPool.QUOTE);
                    sb.append(material);
                    sb.append("',");
                }
            }
        }

        sb.append(CharPool.CLOSE_BRACKET);

        javascriptArray = sb.toString();
        bibliographicMaterials = Collections.unmodifiableList(tempBibliographicMaterials);
        searchableMaterials = Collections.unmodifiableList(tempSearchableMaterials);
    }

    private final char typeOfRecord;
    private final String implDefined1;
    private final boolean searchable;
    private final RecordType recordType;

    MaterialType(char typeOfRecord, String implDef1, boolean searchable) {
        this(typeOfRecord, implDef1, searchable, RecordType.BIBLIO);
    }

    MaterialType(
            char typeOfRecord, String implDefined1, boolean searchable, RecordType recordType) {
        this.typeOfRecord = typeOfRecord;
        this.implDefined1 = implDefined1;
        this.searchable = searchable;
        this.recordType = recordType;
    }

    public static MaterialType fromString(String str) {
        return EnumUtils.getEnum(MaterialType.class, str.toUpperCase(), null);
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
            mt =
                    MaterialType.fromTypeAndImplDef(
                            leader.getTypeOfRecord(), leader.getImplDefined1());
        }

        return (mt != null && mt != MaterialType.ALL) ? mt : MaterialType.BOOK;
    }

    public static List<MaterialType> bibliographicValues() {
        return bibliographicMaterials;
    }

    public static List<MaterialType> searchableValues() {
        return searchableMaterials;
    }

    public static String toJavascriptArray() {
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
