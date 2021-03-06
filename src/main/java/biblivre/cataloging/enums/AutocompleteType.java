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
import biblivre.cataloging.Fields;
import biblivre.cataloging.FormTabSubfieldDTO;
import biblivre.cataloging.RecordBO;
import biblivre.core.DTOCollection;
import biblivre.core.utils.BiblivreEnum;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public enum AutocompleteType implements BiblivreEnum {
    DISABLED,
    PREVIOUS_VALUES,
    FIXED_TABLE,
    FIXED_TABLE_WITH_PREVIOUS_VALUES,
    BIBLIO,
    AUTHORITIES,
    VOCABULARY;

    public static AutocompleteType fromString(String str) {
        if (StringUtils.isBlank(str)) {
            return AutocompleteType.DISABLED;
        }

        str = str.toLowerCase();

        for (AutocompleteType type : AutocompleteType.values()) {
            if (str.equals(type.name().toLowerCase())) {
                return type;
            }
        }

        return AutocompleteType.DISABLED;
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

    public String getString() {
        return this.toString();
    }

    public List<String> getSuggestions(GetSuggestionsParameters parameterObject) {
        String schema = parameterObject.getSchema();

        RecordType recordType = parameterObject.getRecordType();

        RecordBO bo = RecordBO.getInstance(schema, recordType);

        String datafield = parameterObject.getDatafield();

        String subfield = parameterObject.getSubfield();

        String query = parameterObject.getQuery();

        return bo.phraseAutocomplete(datafield, subfield, query);
    }

    public DTOCollection<AutocompleteDTO> getAutocompletion(String schema, String query) {
        RecordType recordType = RecordType.fromString(this.toString());

        RecordBO bo = RecordBO.getInstance(schema, recordType);

        Set<AutocompleteDTO> set = new HashSet<>();

        for (FormTabSubfieldDTO formTabSubfield :
                Fields.getAutocompleteSubFields(schema, recordType)) {

            String datafield = formTabSubfield.getDatafield();

            String subfield = formTabSubfield.getSubfield();

            DTOCollection<AutocompleteDTO> additionalAutocompletions =
                    bo.recordAutocomplete(datafield, subfield, query);

            set.addAll(additionalAutocompletions);
        }

        DTOCollection<AutocompleteDTO> collection = new DTOCollection<>();

        collection.addAll(set);

        return collection;
    }

    public static class GetSuggestionsParameters {
        private String schema;
        private String query;
        private String datafield;
        private String subfield;
        private RecordType recordType;

        private GetSuggestionsParameters(
                String schema,
                String query,
                String datafield,
                String subfield,
                RecordType recordType) {
            this.schema = schema;
            this.query = query;
            this.datafield = datafield;
            this.subfield = subfield;
            this.recordType = recordType;
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

        public RecordType getRecordType() {
            return recordType;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String schema;
            private String query;
            private String datafield;
            private String subfield;
            private RecordType recordType;

            public Builder withSchema(String schema) {
                this.schema = schema;

                return this;
            }

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

            public Builder withRecordType(RecordType recordType) {
                this.recordType = recordType;

                return this;
            }

            public GetSuggestionsParameters build() {
                return new GetSuggestionsParameters(schema, query, datafield, subfield, recordType);
            }
        }
    }
}
