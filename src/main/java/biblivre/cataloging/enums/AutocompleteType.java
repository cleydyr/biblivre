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
package biblivre.cataloging.enums;

import biblivre.cataloging.AutocompleteDTO;
import biblivre.cataloging.FormTabSubfieldDTO;
import biblivre.cataloging.RecordBO;
import biblivre.core.DTOCollection;
import biblivre.core.utils.BiblivreEnum;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.EnumUtils;

public enum AutocompleteType implements BiblivreEnum {
    DISABLED,
    PREVIOUS_VALUES,
    FIXED_TABLE,
    FIXED_TABLE_WITH_PREVIOUS_VALUES,
    BIBLIO,
    AUTHORITIES,
    VOCABULARY;

    public static AutocompleteType fromString(String str) {
        return EnumUtils.getEnum(AutocompleteType.class, str.toUpperCase(), null);
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

    public String getString() {
        return this.toString();
    }

    public List<String> getSuggestions(GetSuggestionsParameters parameterObject) {
        RecordBO recordBO = parameterObject.getRecordBO();

        String datafield = parameterObject.getDatafield();

        String subfield = parameterObject.getSubfield();

        String query = parameterObject.getQuery();

        return recordBO.phraseAutocomplete(datafield, subfield, query);
    }

    public DTOCollection<AutocompleteDTO> getAutocompletion(
            RecordBO recordBO, String query, Iterable<FormTabSubfieldDTO> autocompleteSubFields) {
        Set<AutocompleteDTO> set = new HashSet<>();

        for (FormTabSubfieldDTO formTabSubfield : autocompleteSubFields) {

            String datafield = formTabSubfield.getDatafield();

            String subfield = formTabSubfield.getSubfield();

            DTOCollection<AutocompleteDTO> additionalAutocompletions =
                    recordBO.recordAutocomplete(datafield, subfield, query);

            set.addAll(additionalAutocompletions);
        }

        DTOCollection<AutocompleteDTO> collection = new DTOCollection<>();

        collection.addAll(set);

        return collection;
    }

    public static class GetSuggestionsParameters {
        private final String schema;
        private final String query;
        private final String datafield;
        private final String subfield;
        private final RecordBO recordBO;

        private GetSuggestionsParameters(
                String schema, String query, String datafield, String subfield, RecordBO recordBO) {
            this.schema = schema;
            this.query = query;
            this.datafield = datafield;
            this.subfield = subfield;
            this.recordBO = recordBO;
        }

        public String getSchema() {
            return schema;
        }

        public String getQuery() {
            return query;
        }

        public String getDatafield() {
            return datafield;
        }

        public String getSubfield() {
            return subfield;
        }

        public RecordBO getRecordBO() {
            return recordBO;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String schema;
            private String query;
            private String datafield;
            private String subfield;
            private RecordBO recordBO;

            public Builder withQuery(String query) {
                this.query = query;

                return this;
            }

            public Builder withDatafield(String datafield) {
                this.datafield = datafield;

                return this;
            }

            public Builder withSubfield(String subfield) {
                this.subfield = subfield;

                return this;
            }

            public Builder withRecordBO(RecordBO recordBO) {
                this.recordBO = recordBO;

                return this;
            }

            public GetSuggestionsParameters build() {
                return new GetSuggestionsParameters(schema, query, datafield, subfield, recordBO);
            }
        }
    }
}
