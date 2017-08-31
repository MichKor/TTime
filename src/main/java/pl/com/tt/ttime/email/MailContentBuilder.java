package pl.com.tt.ttime.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import pl.com.tt.ttime.model.User;

import java.util.Locale;

@Service
public class MailContentBuilder {

    private TemplateEngine templateEngine;

    @Autowired
    public MailContentBuilder(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String build(User user, String subject, String message, Locale locale) {
        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("subject", subject);
        context.setVariable("message", message);
        context.setLocale(locale);
        return templateEngine.process("mailTemplate", context);
    }

}