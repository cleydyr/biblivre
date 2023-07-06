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

import biblivre.core.utils.Constants;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.marc4j.MarcException;
import org.marc4j.MarcPermissiveStreamReader;
import org.marc4j.MarcReader;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Leader;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarcUtils {

    private static final Logger logger = LoggerFactory.getLogger(MarcUtils.class);

    public static Record iso2709ToRecord(String iso2709) {
        return MarcUtils.iso2709ToRecord(iso2709.getBytes(Constants.DEFAULT_CHARSET));
    }

    public static Record iso2709ToRecord(byte[] iso2709) {
        Record record = null;

        try {
            ByteArrayInputStream is = new ByteArrayInputStream(iso2709);
            MarcReader reader = new MarcPermissiveStreamReader(is, true, true);

            if (reader.hasNext()) {
                record = reader.next();
            }

        } catch (MarcException me) {
            if (MarcUtils.logger.isDebugEnabled()) {
                MarcUtils.logger.error(me.getMessage(), me);
            }
        }

        return record;
    }

    public static Record marcToRecord(String marc, MaterialType materialType, RecordStatus status) {
        char splitter = MarcUtils.detectSplitter(marc);
        String unescaped = StringEscapeUtils.unescapeHtml4(marc);
        Scanner scanner;

        ByteArrayInputStream is =
                new ByteArrayInputStream(unescaped.getBytes(Constants.DEFAULT_CHARSET));

        scanner = new Scanner(is, Constants.DEFAULT_CHARSET);

        List<String> text = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            if (line.length() > 3) {
                text.add(line);
            }
        }

        scanner.close();

        String[] tags = new String[text.size()];
        String[] values = new String[text.size()];

        for (int i = 0; i < text.size(); i++) {
            String line = text.get(i).trim();
            if (line.toUpperCase().startsWith("LEADER")) {
                tags[i] = line.substring(0, 6).toUpperCase();
                values[i] = line.substring(7);
            } else {
                tags[i] = line.substring(0, 3).toUpperCase();
                values[i] = line.substring(4);
            }
        }

        Leader leader = MarcUtils.createLeader(tags, values, materialType, status);
        MarcFactory factory = MarcFactory.newInstance();
        Record record = factory.newRecord(leader);
        MarcUtils.setControlFields(record, tags, values);
        MarcUtils.setDataFields(record, tags, values, splitter);

        return record;
    }

    public static Record jsonToRecord(
            JSONObject json, MaterialType materialType, RecordStatus status) {
        if (json == null) {
            return null;
        }

        Record record = null;

        try {
            String strLeader = null;
            if (json.has("000")) {
                strLeader = json.getString("000");
            }

            MarcFactory factory = MarcFactory.newInstance();
            Leader leader = MarcUtils.createLeader(strLeader, materialType, status);
            record = factory.newRecord(leader);
            Iterator<String> dataFieldsIterator = json.keys();

            while (dataFieldsIterator.hasNext()) {
                String dataFieldTag = dataFieldsIterator.next();

                try {
                    int dataFieldIntTag = Integer.parseInt(dataFieldTag);

                    if (dataFieldIntTag == 0) {
                        continue;
                    }

                    if (dataFieldIntTag < 10) {
                        ControlField cf =
                                factory.newControlField(dataFieldTag, json.getString(dataFieldTag));
                        record.addVariableField(cf);

                    } else {
                        JSONArray subFieldsArray = json.getJSONArray(dataFieldTag);

                        for (int i = 0; i < subFieldsArray.length(); i++) {
                            JSONObject subFieldJson = subFieldsArray.getJSONObject(i);

                            DataField df = factory.newDataField();
                            df.setTag(dataFieldTag);
                            df.setIndicator1(' ');
                            df.setIndicator2(' ');

                            Iterator<String> dfIterator = subFieldJson.keys();
                            while (dfIterator.hasNext()) {
                                String subFieldTag = dfIterator.next();

                                if (subFieldTag.equals("ind1")) {
                                    df.setIndicator1(subFieldJson.getString(subFieldTag).charAt(0));
                                } else if (subFieldTag.equals("ind2")) {
                                    df.setIndicator2(subFieldJson.getString(subFieldTag).charAt(0));
                                } else {
                                    JSONArray subFieldDataArray =
                                            subFieldJson.getJSONArray(subFieldTag);

                                    for (int j = 0; j < subFieldDataArray.length(); j++) {
                                        String subfieldData = subFieldDataArray.getString(j);

                                        Subfield sf =
                                                factory.newSubfield(
                                                        subFieldTag.charAt(0), subfieldData);
                                        df.addSubfield(sf);
                                    }
                                }
                            }

                            record.addVariableField(df);
                        }
                    }
                } catch (NumberFormatException nfe) {
                    MarcUtils.logger.error(nfe.getMessage(), nfe);
                }
            }
        } catch (JSONException je) {
            MarcUtils.logger.error(je.getMessage(), je);
        }

        return record;
    }

    public static char detectSplitter(String marc) {
        if (StringUtils.isBlank(marc)) {
            return MarcConstants.DEFAULT_SPLITTER;
        }

        return marc.lines()
                .map(String::trim)
                .filter(line -> line.length() > 7)
                .map(line -> line.charAt(6))
                .filter(c -> c == '|' || c == '$')
                .findFirst()
                .orElse(MarcConstants.DEFAULT_SPLITTER);
    }

    private static Leader createLeader(
            String[] tags, String[] values, MaterialType materialType, RecordStatus status) {
        Leader leader = null;
        for (int i = 0; i < tags.length; i++) {
            if (tags[i].equals("000") || tags[i].equals("LDR") || tags[i].equals("LEADER")) {
                leader = MarcUtils.createLeader(values[i], materialType, status);
                break;
            }
        }

        if (leader == null) {
            leader = MarcUtils.createBasicLeader(materialType, status);
        }

        return leader;
    }

    private static Leader createLeader(
            String pLeader, MaterialType materialType, RecordStatus status) {
        Leader leader = MarcFactory.newInstance().newLeader();

        if (pLeader != null && pLeader.length() == 24) {
            leader.setRecordStatus(status.getCode());

            if (materialType != null && !materialType.equals(MaterialType.ALL)) {
                leader.setTypeOfRecord(materialType.getTypeOfRecord());
                leader.setImplDefined1(materialType.getImplDefined1().toCharArray());
            } else {
                leader.setTypeOfRecord(pLeader.charAt(6));
                char $07 = pLeader.charAt(7);
                char $08 = pLeader.charAt(8) == 'a' ? 'a' : ' ';
                char[] implDef1 = {$07, $08};
                leader.setImplDefined1(implDef1);
            }

            char $09 = pLeader.charAt(9) == 'a' ? 'a' : ' ';
            leader.setCharCodingScheme($09);
            leader.setIndicatorCount(2);
            leader.setSubfieldCodeLength(2);
            leader.setImplDefined2(pLeader.substring(17, 20).toCharArray());
            leader.setEntryMap(pLeader.substring(20).toCharArray());
            leader.setRecordLength(Integer.parseInt(pLeader.substring(0, 5)));

        } else {
            leader = MarcUtils.createBasicLeader(materialType, status);
        }

        return leader;
    }

    public static Leader createBasicLeader(MaterialType materialType, RecordStatus status) {
        if (materialType == null) {
            materialType = MaterialType.ALL;
        }

        Leader leader = MarcFactory.newInstance().newLeader();
        leader.setRecordStatus(status.getCode());
        leader.setTypeOfRecord(materialType.getTypeOfRecord());
        leader.setImplDefined1(materialType.getImplDefined1().toCharArray());
        leader.setCharCodingScheme('a');
        leader.setIndicatorCount(2);
        leader.setSubfieldCodeLength(2);

        if (materialType.equals(MaterialType.AUTHORITIES)) {
            leader.setImplDefined2("n  ".toCharArray());
        } else if (materialType.equals(MaterialType.HOLDINGS)) {
            leader.setImplDefined2("un ".toCharArray());
        } else if (materialType.equals(MaterialType.VOCABULARY)) {
            leader.setImplDefined2("o  ".toCharArray());
        } else { // BIBLIO
            leader.setImplDefined2(" a ".toCharArray());
        }

        leader.setEntryMap("4500".toCharArray());
        return leader;
    }

    private static void setControlFields(Record record, String[] tags, String[] values) {
        MarcFactory factory = MarcFactory.newInstance();
        for (int i = 0; i < tags.length; i++) {
            String tag = tags[i];
            String value = values[i];

            if (StringUtils.isNumeric(tag)) {
                int iTag = Integer.parseInt(tag);

                if (iTag > 0 && iTag < 10) {
                    ControlField controlField = factory.newControlField(tag, value);
                    record.addVariableField(controlField);
                }
            }
        }
    }

    private static void setDataFields(
            Record record, String[] tags, String[] values, char splitter) {
        MarcFactory factory = MarcFactory.newInstance();

        for (int i = 0; i < tags.length; i++) {
            String tag = tags[i];
            String value = values[i];

            if (!StringUtils.isNumeric(tag) || Integer.parseInt(tag) < 10) {
                continue;
            }

            DataField dataField = extractDatafield(factory, tag, value);

            record.addVariableField(dataField);

            String dataFieldValue = value.substring(2).trim();

            SubfieldIterable iterable = new SubfieldIterable(dataFieldValue, splitter);

            for (String subField : iterable) {
                if (StringUtils.isBlank(subField)) {
                    continue;
                }

                Subfield subfield = extractSubfield(factory, subField);

                dataField.addSubfield(subfield);
            }
        }
    }

    private static DataField extractDatafield(MarcFactory factory, String tag, String value) {
        char ind1 = getIndicator1(value);
        char ind2 = getIndicator2(value);

        return factory.newDataField(tag, ind1, ind2);
    }

    private static Subfield extractSubfield(MarcFactory factory, String subField) {
        char code = subField.charAt(0);

        String subFieldValue = subField.substring(1).trim();

        return factory.newSubfield(code, subFieldValue);
    }

    private static char getIndicator2(String value) {
        return getIndicator(value, 1);
    }

    private static char getIndicator1(String value) {
        return getIndicator(value, 0);
    }

    private static char getIndicator(String value, int index) {
        return value.charAt(index) != MarcConstants.NO_INDICATOR ? value.charAt(index) : ' ';
    }

    public static void setAccessionNumber(Record holding, String accessionNumber) {
        MarcFactory factory = MarcFactory.newInstance();

        DataField field = (DataField) holding.getVariableField(MarcConstants.ACCESSION_NUMBER);

        if (field == null) {
            field = factory.newDataField(MarcConstants.ACCESSION_NUMBER, ' ', ' ');
            holding.addVariableField(field);
        }

        Subfield subfield = field.getSubfield('a');
        if (subfield == null) {
            subfield = factory.newSubfield('a');
            field.addSubfield(subfield);
        }

        subfield.setData(accessionNumber);
    }

    static class SubfieldIterable implements Iterable<String> {
        private String next;
        private final char subfieldSplitter;
        private boolean hasNext;
        private final String field;

        private int pos;

        SubfieldIterable(String field, char subfieldSplitter) {
            if (StringUtils.isBlank(field)) {
                throw new IllegalArgumentException("field can't be blank");
            }

            this.subfieldSplitter = subfieldSplitter;
            this.field = field;
            this.pos = 0;
        }

        @Override
        public Iterator<String> iterator() {
            return new Iterator<String>() {
                @Override
                public boolean hasNext() {
                    return pos < field.length();
                }

                @Override
                public String next() {
                    int start = pos;

                    while (pos < field.length() && field.charAt(pos) != subfieldSplitter) {
                        pos++;
                    }

                    return field.substring(start, pos++);
                }
            };
        }
    }
}
