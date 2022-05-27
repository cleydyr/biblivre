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
package biblivre.administration.indexing;

import biblivre.cataloging.AutocompleteDTO;
import biblivre.cataloging.Fields;
import biblivre.cataloging.FormTabSubfieldDTO;
import biblivre.cataloging.RecordBO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.enums.RecordType;
import biblivre.core.AbstractBO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IndexingBO extends AbstractBO {
    private IndexingDAO indexingDAO;

    private volatile boolean reindexingBiblioBase = false;
    private volatile boolean reindexingAuthoritiesBase = false;
    private volatile boolean reindexingVocabularyBase = false;

    private Map<String, RecordBO> recordBOs;

    public void reindex(RecordType recordType) {
        if (this.getLockState(recordType)) {
            return;
        }

        synchronized (this) {
            this.toggleLockState(recordType, true);

            try {
                this.clearIndexes(recordType);

                List<IndexingGroupDTO> indexingGroups = IndexingGroups.getGroups(recordType);
                List<FormTabSubfieldDTO> autocompleteSubfields =
                        Fields.getAutocompleteSubFields(recordType);

                RecordBO rbo = recordBOs.get(recordType.name());

                int recordCount = rbo.count();
                int limit = 30;

                for (int offset = 0; offset < recordCount; offset += limit) {
                    // if (this.logger.isDebugEnabled()) {
                    // this.logger.debug("Reindexing offsets from " + offset + " to " + (offset +
                    // limit));
                    // }

                    List<RecordDTO> records = rbo.list(offset, limit);

                    List<IndexingDTO> indexes = new ArrayList<>();
                    List<IndexingDTO> sortIndexes = new ArrayList<>();
                    List<AutocompleteDTO> autocompleteIndexes = new ArrayList<>();

                    for (RecordDTO dto : records) {
                        IndexingDAO.populateIndexes(dto, indexingGroups, indexes, sortIndexes);
                        IndexingDAO.populateAutocompleteIndexes(
                                dto, autocompleteSubfields, autocompleteIndexes);
                    }

                    this.indexingDAO.insertIndexes(recordType, indexes);
                    this.indexingDAO.insertSortIndexes(recordType, sortIndexes);
                    this.indexingDAO.insertAutocompleteIndexes(recordType, autocompleteIndexes);
                }

                this.indexingDAO.reindexDatabase(recordType);
            } finally {
                this.toggleLockState(recordType, false);
            }
        }
    }

    public void reindexAutocompleteFixedTable(
            RecordType recordType, String datafield, String subfield, List<String> phrases) {
        this.indexingDAO.reindexAutocompleteFixedTable(recordType, datafield, subfield, phrases);
    }

    private void toggleLockState(RecordType recordType, boolean state) {
        switch (recordType) {
            case BIBLIO:
                this.reindexingBiblioBase = state;
                break;
            case AUTHORITIES:
                this.reindexingAuthoritiesBase = state;
                break;
            case VOCABULARY:
                this.reindexingVocabularyBase = state;
                break;
            default:
                break;
        }
    }

    private boolean getLockState(RecordType recordType) {
        switch (recordType) {
            case BIBLIO:
                return this.reindexingBiblioBase;
            case AUTHORITIES:
                return this.reindexingAuthoritiesBase;
            case VOCABULARY:
                return this.reindexingVocabularyBase;
            default:
                return false;
        }
    }

    public int countIndexed(RecordType recordType) {
        return this.indexingDAO.countIndexed(recordType);
    }

    public int[] getReindexProgress(RecordType recordType) {
        int progress[] = new int[2];

        RecordBO rbo = recordBOs.get(recordType.name());

        progress[0] = this.countIndexed(recordType);
        progress[1] = rbo.count();

        return progress;
    }

    public boolean isIndexOutdated() {
        Stream<RecordBO> stream = recordBOs.values().stream();

        return stream.anyMatch(this::_hasOutdatedIndexCount);
    }

    private boolean _hasOutdatedIndexCount(RecordBO recordBO) {
        return this.indexingDAO.countIndexed(recordBO.getRecordType()).equals(recordBO.count());
    }

    private void clearIndexes(RecordType recordType) {
        this.indexingDAO.clearIndexes(recordType);
    }

    public boolean deleteIndexes(RecordType recordType, RecordDTO dto) {
        return this.indexingDAO.deleteIndexes(recordType, dto);
    }

    public List<String> searchExactTerm(RecordType recordType, int indexingGroupId, String term) {
        List<String> terms = new ArrayList<>();
        terms.add(term);
        return this.indexingDAO.searchExactTerms(recordType, indexingGroupId, terms);
    }

    public List<String> searchExactTerms(
            RecordType recordType, int indexingGroupId, List<String> terms) {
        return this.indexingDAO.searchExactTerms(recordType, indexingGroupId, terms);
    }

    @Autowired
    public void setRecordBOs(Map<String, RecordBO> recordBOs) {
        this.recordBOs = recordBOs;
    }

    @Autowired
    public void setIndexingDAO(IndexingDAO indexingDAO) {
        this.indexingDAO = indexingDAO;
    }
}
