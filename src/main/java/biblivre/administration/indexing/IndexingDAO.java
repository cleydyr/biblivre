package biblivre.administration.indexing;

import biblivre.cataloging.AutocompleteDTO;
import biblivre.cataloging.FormTabSubfieldDTO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.enums.RecordType;
import biblivre.core.utils.TextUtils;
import biblivre.marc.MarcDataReader;
import biblivre.marc.MarcUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

public interface IndexingDAO {
    List<String> nonfillingCharactersInIndicator1 = List.of("130", "630", "730", "740");
    List<String> nonfillingCharactersInIndicator2 = List.of("240", "243", "245", "830");

    Integer countIndexed(RecordType recordType);

    void clearIndexes(RecordType recordType);

    void insertIndexes(RecordType recordType, List<IndexingDTO> indexes);

    void insertSortIndexes(RecordType recordType, List<IndexingDTO> sortIndexes);

    void insertAutocompleteIndexes(
            RecordType recordType, List<AutocompleteDTO> autocompleteIndexes);

    void reindexAutocompleteFixedTable(
            RecordType recordType, String datafield, String subfield, List<String> phrases);

    boolean deleteIndexes(RecordType recordType, RecordDTO dto);

    void reindexDatabase(RecordType recordType);

    List<String> searchExactTerms(RecordType recordType, int indexingGroupId, List<String> terms);

    default void reindex(
            RecordDTO dto,
            List<IndexingGroupDTO> indexingGroups,
            List<FormTabSubfieldDTO> autocompleteSubfields) {
        RecordType recordType = dto.getRecordType();

        synchronized (this) {
            List<IndexingDTO> indexes = new ArrayList<>();
            List<IndexingDTO> sortIndexes = new ArrayList<>();
            List<AutocompleteDTO> autocompleteIndexes = new ArrayList<>();

            populateIndexes(dto, indexingGroups, indexes, sortIndexes);
            populateAutocompleteIndexes(dto, autocompleteSubfields, autocompleteIndexes);

            deleteIndexes(recordType, dto);

            insertIndexes(recordType, indexes);
            insertSortIndexes(recordType, sortIndexes);
            insertAutocompleteIndexes(recordType, autocompleteIndexes);
        }
    }

    static void populateIndexes(
            RecordDTO dto,
            List<IndexingGroupDTO> indexingGroups,
            List<IndexingDTO> indexes,
            List<IndexingDTO> sortIndexes) {
        Record record = MarcUtils.iso2709ToRecord(dto.getIso2709());
        MarcDataReader marcDataReader = new MarcDataReader(record);

        IndexingDTO index;
        IndexingDTO sortIndex;
        String datafieldTag;
        List<DataField> datafields;
        List<Subfield> subfields;
        String phrase;
        boolean charsToIgnoreSet;

        int datafieldId = 0;

        index = new IndexingDTO();
        index.setIndexingGroupId(0);
        index.setRecordId(dto.getId());
        index.addWord(String.valueOf(dto.getId()), datafieldId);

        indexes.add(index);

        // For each indexing group
        for (IndexingGroupDTO ig : indexingGroups) {
            index = new IndexingDTO();
            index.setIndexingGroupId(ig.getId());
            index.setRecordId(dto.getId());

            if (ig.getId() != 0) {
                sortIndex = new IndexingDTO();
                sortIndex.setIndexingGroupId(ig.getId());
                sortIndex.setRecordId(dto.getId());
                charsToIgnoreSet = false;
            } else {
                sortIndex = null;
                charsToIgnoreSet = true;
            }

            // For each datafield in indexing group
            for (Pair<String, List<Character>> pair : ig.getDatafieldsArray()) {
                datafieldTag = pair.getLeft();

                // Get all datafields from record
                datafields = marcDataReader.getDataFields(datafieldTag);

                // For each one of those datafields
                for (DataField datafield : datafields) {
                    datafieldId++;

                    // For each subfield in indexing group
                    for (Character subfieldTag : pair.getRight()) {
                        // Get all the subfields from datafield
                        subfields = datafield.getSubfields(subfieldTag);

                        for (Subfield subfield : subfields) {
                            phrase = TextUtils.preparePhrase(subfield.getData());
                            index.addWords(TextUtils.prepareWords(phrase), datafieldId);
                            // index.addWord(phrase);

                            if (sortIndex != null) {
                                sortIndex.appendToPhrase(phrase);
                            }
                        }
                    }

                    // Some datafields have nonfillings characters, based on indicator 1 or 2
                    if (!charsToIgnoreSet && sortIndex.getPhraseLength() > 0) {
                        char indicator = '0';
                        if (nonfillingCharactersInIndicator1.stream()
                                .anyMatch(datafieldTag::equals)) {
                            indicator = datafield.getIndicator1();
                        } else if (nonfillingCharactersInIndicator2.stream()
                                .anyMatch(datafieldTag::equals)) {
                            indicator = datafield.getIndicator2();
                        }

                        if (indicator >= '1' && indicator <= '9') {
                            sortIndex.setIgnoreCharsCount(
                                    Integer.parseInt(Character.toString(indicator)));
                        }

                        charsToIgnoreSet = true;
                    }
                }
            }

            if (index.getCount() > 0) {
                indexes.add(index);
            }

            if (sortIndex != null) {
                sortIndexes.add(sortIndex);
            }
        }
    }

    static void populateAutocompleteIndexes(
            RecordDTO dto,
            List<FormTabSubfieldDTO> autocompleteSubfields,
            List<AutocompleteDTO> autocompleteIndexes) {
        Record record = MarcUtils.iso2709ToRecord(dto.getIso2709());

        MarcDataReader marcDataReader = new MarcDataReader(record);
        AutocompleteDTO autocomplete;
        List<DataField> datafields;
        List<Subfield> subfields;
        String phrase;

        for (FormTabSubfieldDTO autocompleteSubfield : autocompleteSubfields) {
            datafields = marcDataReader.getDataFields(autocompleteSubfield.getDatafield());

            for (DataField datafield : datafields) {
                subfields = datafield.getSubfields(autocompleteSubfield.getSubfield().charAt(0));

                for (Subfield subfield : subfields) {
                    phrase = subfield.getData();

                    if (StringUtils.isBlank(phrase)) {
                        continue;
                    }

                    autocomplete = new AutocompleteDTO();

                    autocomplete.setRecordId(dto.getId());
                    autocomplete.setDatafield(autocompleteSubfield.getDatafield());
                    autocomplete.setSubfield(autocompleteSubfield.getSubfield());
                    autocomplete.setPhrase(phrase);

                    autocompleteIndexes.add(autocomplete);
                }
            }
        }
    }
}
