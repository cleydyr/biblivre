package biblivre.mail;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.mail.internet.InternetAddress;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@Disabled
public class EmailSenderTest {
    @Autowired private EmailSender emailSender;

    @Test
    public void testEmailIsSent() throws Exception {
        assertDoesNotThrow(
                () ->
                        emailSender.sendEmail(
                                new InternetAddress[] {new InternetAddress("email@email.com")},
                                "Test Subject",
                                "Test Body",
                                null,
                                null,
                                null));
    }
}
