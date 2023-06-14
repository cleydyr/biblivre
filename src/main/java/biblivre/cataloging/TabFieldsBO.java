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
import biblivre.core.SchemaThreadLocal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TabFieldsBO {
    private TabFieldsDAO tabFieldsDAO;

    private static final Logger logger = LoggerFactory.getLogger(TabFieldsBO.class);

    private final Map<String, JavascriptCacheableList<FormTabDatafieldDTO>> tabFieldsCache =
            new ConcurrentHashMap<>();

    public List<BriefTabFieldFormatDTO> getBriefFormats(RecordType recordType) {
        return loadBriefFormats(recordType);
    }

    public boolean insertBriefFormat(
            RecordType recordType, BriefTabFieldFormatDTO dto, int loggedUser) {
        clearTabFieldsCache(recordType);

        return tabFieldsDAO.insertBriefFormat(dto, recordType, loggedUser);
    }

    public boolean updateBriefFormats(
            RecordType recordType, List<BriefTabFieldFormatDTO> briefFormats, int loggedUser) {
        clearTabFieldsCache(recordType);

        return tabFieldsDAO.updateBriefFormats(briefFormats, recordType, loggedUser);
    }

    public boolean updateFormTabDatafield(
            RecordType recordType,
            Map<String, FormTabDatafieldDTO> formDatafields,
            int loggedUser) {

        clearTabFieldsCache(recordType);

        return tabFieldsDAO.updateFormTabDatafield(formDatafields, recordType, loggedUser);
    }

    public boolean deleteBriefFormat(RecordType recordType, String datafield) {
        clearTabFieldsCache(recordType);

        return tabFieldsDAO.deleteBriefFormat(datafield, recordType);
    }

    public boolean deleteFormTabDatafield(RecordType recordType, String datafield) {
        clearTabFieldsCache(recordType);

        return tabFieldsDAO.deleteFormTabDatafield(datafield, recordType);
    }

    public JavascriptCacheableList<FormTabDatafieldDTO> getFormFields(String type) {
        RecordType recordType = RecordType.fromString(type);

        if (recordType == null) {
            recordType = RecordType.BIBLIO;
        }

        return getFormFields(recordType);
    }

    public JavascriptCacheableList<FormTabDatafieldDTO> getFormFields(RecordType recordType) {
        return loadFormFields(recordType);
    }

    public List<FormTabSubfieldDTO> getAutocompleteSubFields(String type) {
        RecordType recordType = RecordType.fromString(type);

        if (recordType == null) {
            recordType = RecordType.BIBLIO;
        }

        return getAutocompleteSubFields(recordType);
    }

    public List<FormTabSubfieldDTO> getAutocompleteSubFields(RecordType recordType) {
        return loadAutocompleteSubFields(recordType);
    }

    private List<BriefTabFieldFormatDTO> loadBriefFormats(RecordType recordType) {
        return tabFieldsDAO.listBriefFormats(recordType);
    }

    private JavascriptCacheableList<FormTabDatafieldDTO> loadFormFields(RecordType recordType) {
        String schema = SchemaThreadLocal.get();

        String key = getTabFieldsCacheKey(recordType, schema);

        return tabFieldsCache.computeIfAbsent(
                key,
                __ -> {
                    if (TabFieldsBO.logger.isDebugEnabled()) {
                        TabFieldsBO.logger.debug(
                                "Loading form fields from " + schema + "." + recordType);
                    }

                    List<FormTabDatafieldDTO> fields = tabFieldsDAO.listFields(recordType);

                    JavascriptCacheableList<FormTabDatafieldDTO> list;

                    if (recordType == RecordType.HOLDING) {
                        list =
                                new JavascriptCacheableList<>(
                                        "CatalogingInput.holdingFields",
                                        schema + ".cataloging." + recordType,
                                        ".form.js");
                    } else {
                        list =
                                new JavascriptCacheableList<>(
                                        "CatalogingInput.formFields",
                                        schema + ".cataloging." + recordType,
                                        ".form.js");
                    }

                    list.addAll(fields);

                    return list;
                });
    }

    private String getTabFieldsCacheKey(RecordType recordType, String schema) {
        return "%s#%s".formatted(schema, recordType.toString());
    }

    private List<FormTabSubfieldDTO> loadAutocompleteSubFields(RecordType recordType) {
        String schema = SchemaThreadLocal.get();

        if (TabFieldsBO.logger.isDebugEnabled()) {
            TabFieldsBO.logger.debug(
                    "Loading autocomplete subfields from " + schema + "." + recordType);
        }

        JavascriptCacheableList<FormTabDatafieldDTO> fields = getFormFields(recordType);

        List<FormTabSubfieldDTO> list = new ArrayList<>();

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

        return list;
    }

    private void clearTabFieldsCache(RecordType recordType) {
        String key = getTabFieldsCacheKey(recordType, SchemaThreadLocal.get());

        tabFieldsCache.remove(key);
    }

    @Autowired
    public void setTabFieldsDAO(TabFieldsDAO tabFieldsDAO) {
        this.tabFieldsDAO = tabFieldsDAO;
    }
}
