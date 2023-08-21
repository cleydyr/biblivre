package biblivre.mail;

import biblivre.TestBiblivreApplication;
import jakarta.mail.internet.InternetAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.datasource.url=jdbc:tc:postgresql:12:///biblivre4"},
        classes = TestBiblivreApplication.class)
@Testcontainers
public class EmailSenderTest {
    @Autowired private EmailSender emailSender;

//    @Test
    public void testEmailIsSent() throws Exception {
        assertDoesNotThrow(() -> emailSender.sendEmail(new InternetAddress("email@email.com"), "Lorem ipsum", "Lorem ipsum"));
    }

}
