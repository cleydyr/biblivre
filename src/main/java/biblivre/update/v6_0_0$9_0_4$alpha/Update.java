package biblivre.update.v6_0_0$9_0_4$alpha;

import biblivre.update.UpdateService;
import biblivre.update.exception.UpdateException;
import java.sql.Connection;
import java.sql.Statement;
import org.springframework.stereotype.Component;

/** Persist RRF relevance on biblio_search_results for intelligent-search ranking. */
@Component
public class Update implements UpdateService {

    @Override
    public void doUpdateScopedBySchema(Connection connection) {
        addRelevanceColumn(connection);
    }

    private void addRelevanceColumn(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            statement.execute(
                    """
					ALTER TABLE biblio_search_results
						ADD COLUMN IF NOT EXISTS relevance DOUBLE PRECISION
					""");
        } catch (Exception e) {
            throw new UpdateException("Error adding relevance column to biblio_search_results", e);
        }
    }
}
