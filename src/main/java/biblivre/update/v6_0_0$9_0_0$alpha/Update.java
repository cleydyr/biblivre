package biblivre.update.v6_0_0$9_0_0$alpha;

import biblivre.cataloging.search.intelligent.ConditionalOnIntelligentSearchEnabled;
import biblivre.update.UpdateService;
import biblivre.update.exception.UpdateException;
import java.sql.Connection;
import java.sql.Statement;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnIntelligentSearchEnabled
public class Update implements UpdateService {

    @Override
    public void doUpdate(Connection connection) {
        createVectorExtension(connection);
    }

    @Override
    public void doUpdateScopedBySchema(Connection connection) {
        createBiblioRecordSearchTable(connection);
    }

    private void createVectorExtension(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE EXTENSION IF NOT EXISTS vector WITH SCHEMA public");
        } catch (Exception e) {
            throw new UpdateException("Error creating vector extension", e);
        }
    }

    private void createBiblioRecordSearchTable(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            statement.execute(
                    """
					CREATE TABLE IF NOT EXISTS biblio_record_search (
						record_id INTEGER PRIMARY KEY REFERENCES biblio_records (id) ON DELETE CASCADE,
						search_text TEXT NOT NULL,
						tsv tsvector,
						embedding public.vector(1024),
						model_id TEXT,
						content_hash TEXT NOT NULL,
						updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
					)
					""");
            statement.execute(
                    """
					CREATE INDEX IF NOT EXISTS ix_biblio_record_search_tsv
						ON biblio_record_search USING gin (tsv)
					""");
            statement.execute(
                    """
					CREATE INDEX IF NOT EXISTS ix_biblio_record_search_embedding
						ON biblio_record_search
						USING hnsw (embedding public.vector_cosine_ops)
					""");
        } catch (Exception e) {
            throw new UpdateException("Error creating biblio_record_search table", e);
        }
    }
}
