package pl.com.tt.ttime.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.com.tt.ttime.model.User;

import java.util.Locale;

@Service
public class EmailServiceImpl implements EmailService {

    private final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private JavaMailSender emailSender;
    private MailContentBuilder mailContentBuilder;
    private MessageSource messageSource;

    @Value("${ttimemanager.mail.address}")
    private String mailAddress;

    @Autowired
    public EmailServiceImpl(JavaMailSender emailSender, MailContentBuilder mailContentBuilder, MessageSource messageSource) {
        this.emailSender = emailSender;
        this.mailContentBuilder = mailContentBuilder;
        this.messageSource = messageSource;
    }

    @Async
    @Override
    public void sendMessage(User user, String subject, String text) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            Locale locale = user.getLocale();
            String subjectLocalized = messageSource.getMessage(subject, null, locale);
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(mailAddress);
            messageHelper.setTo(user.getEmail());
            messageHelper.setSubject(subjectLocalized);
            messageHelper.setText(mailContentBuilder.build(user, subjectLocalized, text, locale), true);
        };
        try {
            emailSender.send(messagePreparator);
        } catch (MailException e) {
            log.error(e.getMessage(), e);
        }
    }
}
