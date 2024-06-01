package biblivre.administration.report;

import biblivre.AbstractContainerDatabaseTest;
import biblivre.TestDatasourceConfiguration;
import biblivre.administration.reports.ReportType;
import biblivre.administration.reports.ReportsBO;
import biblivre.administration.reports.ReportsDTO;
import biblivre.circulation.user.UserDAO;
import biblivre.circulation.user.UserDTO;
import biblivre.circulation.user.UserStatus;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.file.DiskFile;
import biblivre.core.translations.TranslationBO;
import biblivre.core.translations.TranslationsMap;
import biblivre.core.utils.Constants;
import biblivre.core.utils.StringPool;
import biblivre.search.SearchException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Import({TestDatasourceConfiguration.class})
@ActiveProfiles("test")
public class JasperReportBOTest extends AbstractContainerDatabaseTest {
    @Autowired private ReportsBO reportBO;

    @Autowired private UserDAO userDAO;

    @Autowired private TranslationBO translationBO;

    @Test
    public void testAllReportsInjected() throws SearchException {
        SchemaThreadLocal.setSchema(Constants.SINGLE_SCHEMA);

        UserDTO user = newReaderUser();

        userDAO.save(user);

        ReportsDTO report = createReportForUser(user);

        TranslationsMap i18n = translationBO.get("en-US");

        DiskFile file = reportBO.generateReport(report, i18n);

        Assertions.assertNotNull(file);
    }

    private ReportsDTO createReportForUser(UserDTO user) {
        ReportsDTO report = new ReportsDTO();

        report.setType(ReportType.USER);

        report.setUserId(String.valueOf(user.getId()));

        return report;
    }

    private UserDTO newReaderUser() {
        UserDTO user = new UserDTO();

        user.setName(StringPool.BLANK);

        user.setType(1);

        user.setStatus(UserStatus.ACTIVE);

        return user;
    }
}
