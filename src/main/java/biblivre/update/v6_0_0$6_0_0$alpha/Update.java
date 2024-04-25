package biblivre.update.v6_0_0$6_0_0$alpha;

import biblivre.update.UpdateService;
import biblivre.update.exception.UpdateException;
import java.sql.Connection;
import java.sql.Statement;
import org.springframework.stereotype.Component;

@Component
public class Update implements UpdateService {

    public void doUpdateScopedBySchema(Connection connection) throws UpdateException {
        fixBiblioBriefTabFormats(connection);
    }

    private void fixBiblioBriefTabFormats(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            statement.execute(
                    """
                  UPDATE biblio_brief_formats
                  SET format = REPLACE(format, '${b. }', '${b}')
                  WHERE format LIKE '%${b. }%';
                  """);
        } catch (Exception e) {
            throw new UpdateException("Error fixing biblio_brief_formats formats", e);
        }
    }
}
