package biblivre.cataloging.search.intelligent;

import biblivre.cataloging.RecordDTO;
import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;
import biblivre.marc.MaterialType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class IntelligentSearchDAO extends AbstractDAO {

    public Optional<StoredSearchRow> findByRecordId(int recordId) {
        try (var connection = datasource.getConnection();
                PreparedStatement pst =
                        connection.prepareStatement(
                                """
								SELECT record_id, search_text, model_id, content_hash,
									embedding IS NOT NULL AS has_embedding
								FROM biblio_record_search
								WHERE record_id = ?
								""")) {
            pst.setInt(1, recordId);
            ResultSet rs = pst.executeQuery();
            if (!rs.next()) {
                return Optional.empty();
            }
            return Optional.of(
                    new StoredSearchRow(
                            rs.getInt("record_id"),
                            rs.getString("search_text"),
                            rs.getString("model_id"),
                            rs.getString("content_hash"),
                            rs.getBoolean("has_embedding")));
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    public void upsert(
            int recordId,
            String searchText,
            String contentHash,
            String modelId,
            float[] embedding) {
        try (var connection = datasource.getConnection();
                PreparedStatement pst =
                        connection.prepareStatement(
                                """
								INSERT INTO biblio_record_search
									(record_id, search_text, tsv, embedding, model_id, content_hash, updated_at)
								VALUES (
									?,
									?,
									to_tsvector('portuguese', coalesce(?, '')),
									CAST(? AS public.vector),
									?,
									?,
									NOW()
								)
								ON CONFLICT (record_id) DO UPDATE SET
									search_text = EXCLUDED.search_text,
									tsv = EXCLUDED.tsv,
									embedding = EXCLUDED.embedding,
									model_id = EXCLUDED.model_id,
									content_hash = EXCLUDED.content_hash,
									updated_at = NOW()
								""")) {
            pst.setInt(1, recordId);
            pst.setString(2, searchText);
            pst.setString(3, searchText);
            if (embedding == null) {
                pst.setNull(4, Types.OTHER);
            } else {
                pst.setString(4, VectorLiteral.toLiteral(embedding));
            }
            pst.setString(5, modelId);
            pst.setString(6, contentHash);
            pst.executeUpdate();
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    public void delete(int recordId) {
        try (var connection = datasource.getConnection();
                PreparedStatement pst =
                        connection.prepareStatement(
                                "DELETE FROM biblio_record_search WHERE record_id = ?")) {
            pst.setInt(1, recordId);
            pst.executeUpdate();
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    public void clearAll() {
        try (var connection = datasource.getConnection();
                PreparedStatement pst =
                        connection.prepareStatement("DELETE FROM biblio_record_search")) {
            pst.executeUpdate();
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    public boolean populateIntelligentSearch(
            int searchId,
            String database,
            MaterialType materialType,
            float[] queryEmbedding,
            String queryText,
            String modelId,
            int candidateLimit,
            int rrfK) {
        return withTransactionContext(
                connection -> {
                    try (PreparedStatement deletePst =
                            connection.prepareStatement(
                                    "DELETE FROM biblio_search_results WHERE search_id = ?")) {
                        deletePst.setInt(1, searchId);
                        deletePst.executeUpdate();
                    }

                    String materialFilter =
                            materialType != MaterialType.ALL ? " AND r.material = ? " : " ";

                    String sql =
                            """
							WITH vector_hits AS (
								SELECT brs.record_id,
									ROW_NUMBER() OVER (ORDER BY brs.embedding <=> CAST(? AS public.vector)) AS rank
								FROM biblio_record_search brs
								INNER JOIN biblio_records r ON r.id = brs.record_id
								WHERE brs.embedding IS NOT NULL
									AND brs.model_id = ?
									AND r.database = ?
									%s
								ORDER BY brs.embedding <=> CAST(? AS public.vector)
								LIMIT ?
							),
							text_hits AS (
								SELECT brs.record_id,
									ROW_NUMBER() OVER (
										ORDER BY ts_rank_cd(brs.tsv, plainto_tsquery('portuguese', ?)) DESC
									) AS rank
								FROM biblio_record_search brs
								INNER JOIN biblio_records r ON r.id = brs.record_id
								WHERE brs.tsv @@ plainto_tsquery('portuguese', ?)
									AND r.database = ?
									%s
								ORDER BY ts_rank_cd(brs.tsv, plainto_tsquery('portuguese', ?)) DESC
								LIMIT ?
							),
							fused AS (
								SELECT record_id,
									SUM(1.0 / (? + rank)) AS score
								FROM (
									SELECT record_id, rank FROM vector_hits
									UNION ALL
									SELECT record_id, rank FROM text_hits
								) combined
								GROUP BY record_id
							)
							INSERT INTO biblio_search_results (search_id, indexing_group_id, record_id)
							SELECT ?, 0, record_id
							FROM fused
							ORDER BY score DESC
							"""
                                    .formatted(materialFilter, materialFilter);

                    try (PreparedStatement pst = connection.prepareStatement(sql)) {
                        int index = 1;
                        String vectorLiteral = VectorLiteral.toLiteral(queryEmbedding);

                        pst.setString(index++, vectorLiteral);
                        pst.setString(index++, modelId);
                        pst.setString(index++, database);
                        if (materialType != MaterialType.ALL) {
                            pst.setString(index++, materialType.toString());
                        }
                        pst.setString(index++, vectorLiteral);
                        pst.setInt(index++, candidateLimit);

                        pst.setString(index++, queryText);
                        pst.setString(index++, queryText);
                        pst.setString(index++, database);
                        if (materialType != MaterialType.ALL) {
                            pst.setString(index++, materialType.toString());
                        }
                        pst.setString(index++, queryText);
                        pst.setInt(index++, candidateLimit);

                        pst.setInt(index++, rrfK);
                        pst.setInt(index, searchId);

                        return pst.executeUpdate() > 0;
                    }
                });
    }

    public List<RecordDTO> listRecordsNeedingEmbedding(String modelId, int limit, int offset) {
        // Not used yet — reindex walks records via RecordBO
        return List.of();
    }

    public record StoredSearchRow(
            int recordId,
            String searchText,
            String modelId,
            String contentHash,
            boolean hasEmbedding) {}
}
