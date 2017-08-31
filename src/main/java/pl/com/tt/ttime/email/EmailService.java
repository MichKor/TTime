package pl.com.tt.ttime.email;

import pl.com.tt.ttime.model.User;

public interface EmailService {
    void sendMessage(User user, String subject, String text);
}
