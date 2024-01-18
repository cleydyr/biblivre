package biblivre.mail;

import biblivre.mail.config.MailConfiguration;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailSenderService implements EmailSender {

    @Override
    public void sendEmail(InternetAddress to, String subject, String body)
            throws MessagingException {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(MailConfiguration.class);

        JavaMailSender javaMailSender = context.getBean(JavaMailSender.class);

        MimeMessage message = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);

        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setText(body);

        javaMailSender.send(mimeMessageHelper.getMimeMessage());
    }
}
