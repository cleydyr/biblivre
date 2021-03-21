package biblivre.marc;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;

public class MaterialTypeUtil {
    private static final List<MaterialType> bibliographicMaterials;
    private static final List<MaterialType> searchableMaterials;
    private static final String javascriptArray;

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
            mt =
                    fromTypeAndImplDef(
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
}
