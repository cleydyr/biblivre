package biblivre.administration.report;

import biblivre.AbstractContainerDatabaseTest;
import biblivre.administration.reports.ReportType;
import biblivre.administration.reports.ReportsBO;
import biblivre.administration.reports.ReportsDTO;
import biblivre.circulation.user.UserDAO;
import biblivre.circulation.user.UserDTO;
import biblivre.circulation.user.UserStatus;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.file.DiskFile;
import biblivre.core.translations.Translations;
import biblivre.core.translations.TranslationsMap;
import biblivre.core.utils.Constants;
import biblivre.core.utils.StringPool;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
public class ReportBOTest extends AbstractContainerDatabaseTest {
    @Autowired private ReportsBO reportBO;

    @Autowired private UserDAO userDAO;

    @Test
    public void testAllReportsInjected() {
        execute(
                () -> {
                    SchemaThreadLocal.setSchema(Constants.SINGLE_SCHEMA);

                    UserDTO user = newReaderUser();

                    userDAO.save(user);

                    ReportsDTO report = createReportForUser(user);

                    TranslationsMap i18n = Translations.get("en-US");

                    DiskFile file = reportBO.generateReport(report, i18n);

                    assertNotNull(file);
                });
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
