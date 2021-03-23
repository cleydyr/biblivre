package biblivre.marc.material;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;

public class MaterialTypeUtil {
    private static final Collection<MaterialType> bibliographicMaterials;
    private static final Collection<MaterialType> searchableMaterials;
    private static final String javascriptArray;
    private static final Map<String, MaterialType> _materialTypeCacheByKey = new HashMap<>();

    static {
        List<MaterialType> tempBibliographicMaterials = new ArrayList<MaterialType>();
        List<MaterialType> tempSearchableMaterials = new ArrayList<MaterialType>();

        StringBuffer sb = new StringBuffer();

        sb.append("[");

        for (MaterialType material : MaterialType.values()) {
            _materialTypeCacheByKey.putIfAbsent(_getKey(material), material);

            if (!material.isSearchable()) {
                continue;
            }

            tempSearchableMaterials.add(material);

            tempBibliographicMaterials.add(material);

            sb.append("\'").append(material.toString()).append("\',");
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

        return MaterialType.valueOf(str.toUpperCase());
    }

    public static MaterialType fromRecord(Record record) {
        if (record == null) {
            return null;
        }

        Leader leader = record.getLeader();

        String key = _getKey(leader.getTypeOfRecord(), leader.getImplDefined1());

        return _materialTypeCacheByKey.getOrDefault(key, MaterialType.BOOK);
    }

    public static Collection<MaterialType> bibliographicValues() {
        return bibliographicMaterials;
    }

    public static Collection<MaterialType> searchableValues() {
        return searchableMaterials;
    }

    public static String toJavascriptArray() {
        return javascriptArray;
    }

    private static String _getKey(char typeOfRecord, char[] implDef1) {
        StringBuilder sb = new StringBuilder(3);

        sb.append(typeOfRecord);
        sb.append('#');
        sb.append(implDef1);

        return sb.toString();
    }

    private static String _getKey(MaterialType material) {
        return _getKey(material.getTypeOfRecord(), material.getImplDefined1().toCharArray());
    }
}
