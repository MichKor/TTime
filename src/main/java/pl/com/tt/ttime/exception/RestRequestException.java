package pl.com.tt.ttime.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

public class RestRequestException extends Exception {
    private HttpStatusCodeException cause;

    public RestRequestException(String message, HttpStatusCodeException cause) {
        super(message, cause);
        this.cause = cause;
    }

    public HttpStatus getStatusCode() {
        return cause.getStatusCode();
    }
}
