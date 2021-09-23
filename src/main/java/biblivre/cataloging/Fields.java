/*******************************************************************************
 * Este arquivo é parte do Biblivre5.
 *
 * Biblivre5 é um software livre; você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da Licença Pública Geral GNU como
 * publicada pela Fundação do Software Livre (FSF); na versão 3 da
 * Licença, ou (caso queira) qualquer versão posterior.
 *
 * Este programa é distribuído na esperança de que possa ser útil,
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
package biblivre.cataloging;

import biblivre.cataloging.enums.AutocompleteType;
import biblivre.cataloging.enums.RecordType;
import biblivre.core.JavascriptCacheableList;
import biblivre.core.PreparedStatementUtil;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.StaticBO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Fields extends StaticBO {

    private static Logger logger = LoggerFactory.getLogger(Fields.class);

    private static HashMap<Pair<String, RecordType>, List<BriefTabFieldFormatDTO>>
            briefTabFieldFormats;

    private static HashMap<Pair<String, RecordType>, JavascriptCacheableList<FormTabDatafieldDTO>>
            formTabFields;

    private static HashMap<Pair<String, RecordType>, List<FormTabSubfieldDTO>>
            autocompleteSubfields;

    private Fields() {}

    static {
        Fields.resetAll();
    }

    public static void resetAll() {
        Fields.briefTabFieldFormats = new HashMap<>();

        Fields.formTabFields =
                new HashMap<
                        Pair<String, RecordType>, JavascriptCacheableList<FormTabDatafieldDTO>>();

        Fields.autocompleteSubfields = new HashMap<>();
    }

    public static void reset(RecordType recordType) {
        String schema = SchemaThreadLocal.get();

        Pair<String, RecordType> pair = Pair.of(schema, recordType);

        Fields.briefTabFieldFormats.remove(pair);

        Fields.formTabFields.remove(pair);

        Fields.autocompleteSubfields.remove(pair);
    }

    public static List<BriefTabFieldFormatDTO> getBriefFormats(RecordType recordType) {
        String schema = SchemaThreadLocal.get();

        Pair<String, RecordType> pair = Pair.of(schema, recordType);

        List<BriefTabFieldFormatDTO> list = Fields.briefTabFieldFormats.get(pair);

        if (list == null) {
            list = Fields.loadBriefFormats(recordType);
        }

        return list;
    }

    public static boolean insertBriefFormat(
            RecordType recordType, BriefTabFieldFormatDTO dto, int loggedUser) {

        TabFieldsDAO dao = TabFieldsDAOImpl.getInstance();

        boolean result = dao.insertBriefFormat(dto, recordType, loggedUser);

        String schema = SchemaThreadLocal.get();

        if (result) {
            Pair<String, RecordType> pair = Pair.of(schema, recordType);
            Fields.briefTabFieldFormats.remove(pair);
        }

        return result;
    }

    public static boolean updateBriefFormats(
            RecordType recordType, List<BriefTabFieldFormatDTO> briefFormats, int loggedUser) {

        TabFieldsDAO dao = TabFieldsDAOImpl.getInstance();

        boolean result = dao.updateBriefFormats(briefFormats, recordType, loggedUser);

        if (result) {
            String schema = SchemaThreadLocal.get();

            Pair<String, RecordType> pair = Pair.of(schema, recordType);

            Fields.briefTabFieldFormats.remove(pair);
        }

        return result;
    }

    public static boolean updateFormTabDatafield(
            RecordType recordType,
            HashMap<String, FormTabDatafieldDTO> formDatafields,
            int loggedUser) {

        TabFieldsDAO dao = TabFieldsDAOImpl.getInstance();

        boolean result = dao.updateFormTabDatafield(formDatafields, recordType, loggedUser);

        if (result) {
            String schema = SchemaThreadLocal.get();

            Pair<String, RecordType> pair = Pair.of(schema, recordType);

            Fields.formTabFields.remove(pair);
        }

        return result;
    }

    public static boolean deleteBriefFormat(RecordType recordType, String datafield) {

        TabFieldsDAO dao = TabFieldsDAOImpl.getInstance();

        boolean result = dao.deleteBriefFormat(datafield, recordType);

        if (result) {
            String schema = SchemaThreadLocal.get();

            Pair<String, RecordType> pair = Pair.of(schema, recordType);

            Fields.briefTabFieldFormats.remove(pair);
        }

        return result;
    }

    public static boolean deleteFormTabDatafield(RecordType recordType, String datafield) {

        TabFieldsDAO dao = TabFieldsDAOImpl.getInstance();
        boolean result = dao.deleteFormTabDatafield(datafield, recordType);

        if (result) {
            String schema = SchemaThreadLocal.get();
            Pair<String, RecordType> pair = Pair.of(schema, recordType);
            Fields.formTabFields.remove(pair);
        }

        return result;
    }

    public static JavascriptCacheableList<FormTabDatafieldDTO> getFormFields(String type) {
        RecordType recordType = RecordType.fromString(type);

        if (recordType == null) {
            recordType = RecordType.BIBLIO;
        }

        return Fields.getFormFields(recordType);
    }

    public static JavascriptCacheableList<FormTabDatafieldDTO> getFormFields(
            RecordType recordType) {
        String schema = SchemaThreadLocal.get();

        Pair<String, RecordType> pair = Pair.of(schema, recordType);

        JavascriptCacheableList<FormTabDatafieldDTO> list = Fields.formTabFields.get(pair);

        if (list == null) {
            list = Fields.loadFormFields(recordType);
        }

        return list;
    }

    public static List<FormTabSubfieldDTO> getAutocompleteSubFields(String type) {
        RecordType recordType = RecordType.fromString(type);

        if (recordType == null) {
            recordType = RecordType.BIBLIO;
        }

        return Fields.getAutocompleteSubFields(recordType);
    }

    public static List<FormTabSubfieldDTO> getAutocompleteSubFields(RecordType recordType) {
        String schema = SchemaThreadLocal.get();

        Pair<String, RecordType> pair = Pair.of(schema, recordType);

        List<FormTabSubfieldDTO> list = Fields.autocompleteSubfields.get(pair);

        if (list == null) {
            list = Fields.loadAutocompleteSubFields(recordType);
        }

        return list;
    }

    public static void addBriefFormat(
            Connection con,
            RecordType recordType,
            String datafield,
            String format,
            Integer sortOrder)
            throws SQLException {

        _deleteFromBriefFormat(datafield, recordType, con);

        _insertIntoBriefFormat(datafield, format, sortOrder, recordType, con);
    }

    public static void updateBriefFormat(
            Connection con, RecordType recordType, String datafield, String format)
            throws SQLException {

        StringBuilder sql =
                new StringBuilder(3)
                        .append("UPDATE ")
                        .append(recordType)
                        .append("_brief_formats SET format = ? WHERE datafield = ?;");

        try (PreparedStatement pst = con.prepareStatement(sql.toString())) {
            PreparedStatementUtil.setAllParameters(pst, format, datafield);

            pst.execute();
        }
    }

    private static void _deleteFromBriefFormat(
            String datafield, RecordType recordType, Connection con) throws SQLException {

        StringBuilder deleteFromBriefFormatsSQLTemplate =
                new StringBuilder(3)
                        .append("DELETE FROM ")
                        .append(recordType)
                        .append("_brief_formats WHERE datafield = ?;");

        PreparedStatement deleteFromBriefFormat =
                con.prepareStatement(deleteFromBriefFormatsSQLTemplate.toString());

        PreparedStatementUtil.setAllParameters(deleteFromBriefFormat, datafield);

        deleteFromBriefFormat.execute();
    }

    private static void _insertIntoBriefFormat(
            String datafield,
            String format,
            Integer sortOrder,
            RecordType recordType,
            Connection con)
            throws SQLException {

        StringBuilder insertIntoBriefFormatsSQLTemplate =
                new StringBuilder(3)
                        .append("INSERT INTO ")
                        .append(recordType)
                        .append("_brief_formats (datafield, format, sort_order) VALUES (?, ?, ?);");

        try (PreparedStatement insertIntoBriefFormat =
                con.prepareStatement(insertIntoBriefFormatsSQLTemplate.toString())) {

            PreparedStatementUtil.setAllParameters(
                    insertIntoBriefFormat, datafield, format, sortOrder);

            insertIntoBriefFormat.execute();
        }
    }

    private static synchronized List<BriefTabFieldFormatDTO> loadBriefFormats(
            RecordType recordType) {
        String schema = SchemaThreadLocal.get();

        Pair<String, RecordType> pair = Pair.of(schema, recordType);

        List<BriefTabFieldFormatDTO> list = Fields.briefTabFieldFormats.get(pair);

        // Checking again for thread safety.
        if (list != null) {
            return list;
        }

        if (Fields.logger.isDebugEnabled()) {
            Fields.logger.debug("Loading brief formats from " + schema + "." + recordType);
        }

        TabFieldsDAO dao = TabFieldsDAOImpl.getInstance();

        list = dao.listBriefFormats(recordType);

        Fields.briefTabFieldFormats.put(pair, list);

        return list;
    }

    private static synchronized JavascriptCacheableList<FormTabDatafieldDTO> loadFormFields(
            RecordType recordType) {
        String schema = SchemaThreadLocal.get();

        Pair<String, RecordType> pair = Pair.of(schema, recordType);

        JavascriptCacheableList<FormTabDatafieldDTO> list = Fields.formTabFields.get(pair);

        // Checking again for thread safety.
        if (list != null) {
            return list;
        }

        if (Fields.logger.isDebugEnabled()) {
            Fields.logger.debug("Loading form fields from " + schema + "." + recordType);
        }

        TabFieldsDAO dao = TabFieldsDAOImpl.getInstance();

        List<FormTabDatafieldDTO> fields = dao.listFields(recordType);

        if (recordType == RecordType.HOLDING) {
            list =
                    new JavascriptCacheableList<>(
                            "CatalogingInput.holdingFields",
                            schema + ".cataloging." + recordType.toString(),
                            ".form.js");
        } else {
            list =
                    new JavascriptCacheableList<>(
                            "CatalogingInput.formFields",
                            schema + ".cataloging." + recordType.toString(),
                            ".form.js");
        }

        list.addAll(fields);

        Fields.formTabFields.put(pair, list);

        return list;
    }

    private static synchronized List<FormTabSubfieldDTO> loadAutocompleteSubFields(
            RecordType recordType) {
        String schema = SchemaThreadLocal.get();

        Pair<String, RecordType> pair = Pair.of(schema, recordType);

        List<FormTabSubfieldDTO> list = Fields.autocompleteSubfields.get(pair);

        // Checking again for thread safety.
        if (list != null) {
            return list;
        }

        if (Fields.logger.isDebugEnabled()) {
            Fields.logger.debug("Loading autocomplete subfields from " + schema + "." + recordType);
        }

        JavascriptCacheableList<FormTabDatafieldDTO> fields = Fields.getFormFields(recordType);

        list = new ArrayList<>();

        if (fields == null) {
            return list;
        }

        for (FormTabDatafieldDTO datafield : fields) {
            for (FormTabSubfieldDTO subfield : datafield.getSubfields()) {
                AutocompleteType type = subfield.getAutocompleteType();

                if ((type == AutocompleteType.PREVIOUS_VALUES)
                        || (type == AutocompleteType.FIXED_TABLE_WITH_PREVIOUS_VALUES)) {
                    list.add(subfield);
                } else if (recordType == RecordType.AUTHORITIES) {
                    if ("100,110,111".contains(datafield.getDatafield())) {
                        if (subfield.getSubfield().equals("a")) {
                            list.add(subfield);
                        }
                    }
                } else if (recordType == RecordType.VOCABULARY) {
                    if ("150".contains(datafield.getDatafield())) {
                        if (subfield.getSubfield().equals("a")) {
                            list.add(subfield);
                        }
                    }
                }
            }
        }

        Fields.autocompleteSubfields.put(pair, list);

        return list;
    }
}
