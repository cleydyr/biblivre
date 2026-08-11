package biblivre.cataloging.search.intelligent;

import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.search.SearchDTO;
import biblivre.cataloging.search.SearchQueryDTO;
import biblivre.cataloging.search.SearchTermDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

public interface IntelligentSearchService {
    boolean populateSearch(SearchDTO search, boolean deleteOldResults);

    void indexRecord(RecordDTO dto);

    void deleteRecord(int recordId);

    void clearAll();

    void reindexRecords(List<RecordDTO> records);
}

@Service
@RequiredArgsConstructor
@Slf4j
class IntelligentSearchServiceImpl implements IntelligentSearchService {
    private final IntelligentSearchProperties properties;
    private final EmbeddingProvider embeddingProvider;
    private final BibliographicSearchTextBuilder searchTextBuilder;
    private final IntelligentSearchDAO intelligentSearchDAO;

    @Override
    public boolean populateSearch(SearchDTO search, boolean deleteOldResults) {
        if (!properties.isEnabled()) {
            log.warn("Intelligent search requested but biblivre.search.intelligent.enabled=false");
            return false;
        }

        SearchQueryDTO query = search.getQuery();
        String queryText = extractQueryText(query);
        if (StringUtils.isBlank(queryText)) {
            return false;
        }

        float[] queryEmbedding = embeddingProvider.embed(queryText);

        return intelligentSearchDAO.populateIntelligentSearch(
                search.getId(),
                query.getDatabase().toString(),
                query.getMaterialType(),
                queryEmbedding,
                queryText,
                embeddingProvider.modelId(),
                properties.getCandidateLimit(),
                properties.getRrfK());
    }

    @Override
    public void indexRecord(RecordDTO dto) {
        if (!properties.isEnabled() || dto == null) {
            return;
        }

        String searchText = searchTextBuilder.build(dto);
        if (StringUtils.isBlank(searchText)) {
            intelligentSearchDAO.delete(dto.getId());
            return;
        }

        String contentHash = VectorLiteral.contentHash(searchText);
        Optional<IntelligentSearchDAO.StoredSearchRow> existing =
                intelligentSearchDAO.findByRecordId(dto.getId());

        if (existing.isPresent()
                && existing.get().hasEmbedding()
                && contentHash.equals(existing.get().contentHash())
                && embeddingProvider.modelId().equals(existing.get().modelId())) {
            return;
        }

        try {
            float[] embedding = embeddingProvider.embed(searchText);
            intelligentSearchDAO.upsert(
                    dto.getId(), searchText, contentHash, embeddingProvider.modelId(), embedding);
        } catch (Exception e) {
            log.error("Failed to embed bibliographic record {}", dto.getId(), e);
            intelligentSearchDAO.upsert(
                    dto.getId(), searchText, contentHash, embeddingProvider.modelId(), null);
        }
    }

    @Override
    public void deleteRecord(int recordId) {
        intelligentSearchDAO.delete(recordId);
    }

    @Override
    public void clearAll() {
        intelligentSearchDAO.clearAll();
    }

    @Override
    public void reindexRecords(List<RecordDTO> records) {
        if (!properties.isEnabled() || records == null || records.isEmpty()) {
            return;
        }

        int batchSize = Math.max(1, properties.getEmbedding().getBatchSize());
        List<RecordDTO> pending = new ArrayList<>();
        List<String> texts = new ArrayList<>();
        List<String> hashes = new ArrayList<>();

        for (RecordDTO dto : records) {
            String searchText = searchTextBuilder.build(dto);
            if (StringUtils.isBlank(searchText)) {
                intelligentSearchDAO.delete(dto.getId());
                continue;
            }

            String contentHash = VectorLiteral.contentHash(searchText);
            Optional<IntelligentSearchDAO.StoredSearchRow> existing =
                    intelligentSearchDAO.findByRecordId(dto.getId());
            if (existing.isPresent()
                    && existing.get().hasEmbedding()
                    && contentHash.equals(existing.get().contentHash())
                    && embeddingProvider.modelId().equals(existing.get().modelId())) {
                continue;
            }

            pending.add(dto);
            texts.add(searchText);
            hashes.add(contentHash);

            if (pending.size() >= batchSize) {
                flushBatch(pending, texts, hashes);
                pending.clear();
                texts.clear();
                hashes.clear();
            }
        }

        if (!pending.isEmpty()) {
            flushBatch(pending, texts, hashes);
        }
    }

    private void flushBatch(List<RecordDTO> records, List<String> texts, List<String> hashes) {
        try {
            List<float[]> embeddings = embeddingProvider.embedBatch(texts);
            for (int i = 0; i < records.size(); i++) {
                intelligentSearchDAO.upsert(
                        records.get(i).getId(),
                        texts.get(i),
                        hashes.get(i),
                        embeddingProvider.modelId(),
                        embeddings.get(i));
            }
        } catch (Exception e) {
            log.error("Failed to batch-embed {} bibliographic records", records.size(), e);
            for (int i = 0; i < records.size(); i++) {
                intelligentSearchDAO.upsert(
                        records.get(i).getId(),
                        texts.get(i),
                        hashes.get(i),
                        embeddingProvider.modelId(),
                        null);
            }
        }
    }

    private static String extractQueryText(SearchQueryDTO query) {
        if (query == null || query.getTerms() == null || query.getTerms().isEmpty()) {
            return "";
        }
        SearchTermDTO term = query.getTerms().getFirst();
        if (term.getTerms() == null || term.getTerms().isEmpty()) {
            return "";
        }
        return String.join(" ", term.getTerms()).trim();
    }
}
