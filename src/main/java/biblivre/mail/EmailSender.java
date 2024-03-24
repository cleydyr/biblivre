package biblivre.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;

public interface EmailSender {

    /**
     * Sends an email to the specified addresses, with the specified subject and body. It also
     * allows to specify a reply-to address, a cc address and a bcc address.
     */
    void sendEmail(
            InternetAddress[] to,
            String subject,
            String body,
            InternetAddress replyTo,
            InternetAddress[] cc,
            InternetAddress[] bcc)
            throws MessagingException;
}
