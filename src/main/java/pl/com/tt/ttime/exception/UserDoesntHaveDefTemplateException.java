package pl.com.tt.ttime.exception;

public class UserDoesntHaveDefTemplateException extends Exception {
    public UserDoesntHaveDefTemplateException(String message) {
        super(message);
    }

    public UserDoesntHaveDefTemplateException(String message, Throwable cause) {
        super(message, cause);
    }
}
