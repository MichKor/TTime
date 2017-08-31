package pl.com.tt.ttime.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.com.tt.ttime.email.EmailService;
import pl.com.tt.ttime.exception.UserDoesntHaveDefTemplateException;
import pl.com.tt.ttime.model.User;
import pl.com.tt.ttime.service.UserService;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

@Component
public class DefaultTemplatesScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTemplatesScheduler.class);

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    //Mondays at 3:00 assigns a schedule for the next week
    @Scheduled(cron = "0 0 3 * * MON")
    @Async
    public void assignSchedule() {
        LocalDate date = LocalDate.now().plusWeeks(1);
        for (User u : userService.listAll()) {
            if (!u.isLoggedInBefore()) {
                continue;
            }

            try {
                if (userService.assignDefTemplateUnlessSet(u, date)) {
                    LOGGER.info("User " + u.getUsername() + " has had his/her schedule set according to his/her default template.");
                    if (u.isNotificationsEnabled()) {
                        emailService.sendMessage(u, "email.setting_schedule_def_template_title", "email.setting_schedule_def_template");
                    }
                } else {
                    LOGGER.info("User " + u.getUsername() + " has already set his/her schedule for next week.");
                }
            } catch (UserDoesntHaveDefTemplateException e) {
                LOGGER.warn("User " + u.getUsername() + " doesn't have a default template!");
                emailService.sendMessage(u, "email.no_def_template_title", "email.no_def_template");
            }
        }
    }
}
