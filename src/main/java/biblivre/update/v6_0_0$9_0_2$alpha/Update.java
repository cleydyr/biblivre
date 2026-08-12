package biblivre.update.v6_0_0$9_0_2$alpha;

import biblivre.cataloging.search.intelligent.ConditionalOnIntelligentSearchEnabled;
import biblivre.update.UpdateService;
import biblivre.update.exception.UpdateException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import org.springframework.stereotype.Component;

/** Resize biblio_record_search.embedding to 1024 for bge-m3. */
@Component
@ConditionalOnIntelligentSearchEnabled
public class Update implements UpdateService {

    @Override
    public void doUpdateScopedBySchema(Connection connection) {
        resizeEmbeddingColumn(connection);
    }

    private void resizeEmbeddingColumn(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            ResultSet exists =
                    statement.executeQuery(
                            """
							SELECT 1
							FROM information_schema.tables
							WHERE table_schema = current_schema()
								AND table_name = 'biblio_record_search'
							""");
            if (!exists.next()) {
                return;
            }

            ResultSet dims =
                    statement.executeQuery(
                            """
							SELECT atttypmod
							FROM pg_attribute a
							JOIN pg_class c ON c.oid = a.attrelid
							JOIN pg_namespace n ON n.oid = c.relnamespace
							WHERE n.nspname = current_schema()
								AND c.relname = 'biblio_record_search'
								AND a.attname = 'embedding'
								AND NOT a.attisdropped
							""");
            if (!dims.next()) {
                return;
            }
            int typmod = dims.getInt(1);
            if (typmod == 1024) {
                return;
            }

            statement.execute("DROP INDEX IF EXISTS ix_biblio_record_search_embedding");
            statement.execute("TRUNCATE TABLE biblio_record_search");
            statement.execute(
                    "ALTER TABLE biblio_record_search ALTER COLUMN embedding TYPE public.vector(1024)");
            statement.execute(
                    """
					CREATE INDEX IF NOT EXISTS ix_biblio_record_search_embedding
						ON biblio_record_search
						USING hnsw (embedding public.vector_cosine_ops)
					""");
        } catch (Exception e) {
            throw new UpdateException("Error resizing biblio_record_search.embedding to 1024", e);
        }
    }
}
