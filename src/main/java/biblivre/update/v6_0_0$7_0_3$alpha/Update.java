package biblivre.update.v6_0_0$7_0_3$alpha;

import biblivre.core.translations.TranslationBO;
import biblivre.update.UpdateService;
import biblivre.update.exception.UpdateException;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.Statement;

@Component
public class Update implements UpdateService {

    private TranslationBO translationBO;

    public void doUpdate(Connection connection) throws UpdateException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(
                    """
                            DELETE FROM translations WHERE key = 'menu.administration_custom_reports';
                            """);
        } catch (Exception e) {
            throw new UpdateException("Error deleting translations", e);
        }
    }
}
