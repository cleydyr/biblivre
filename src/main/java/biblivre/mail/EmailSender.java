package biblivre.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;

public interface EmailSender {

    void sendEmail(InternetAddress to, String subject, String body) throws MessagingException;

}
