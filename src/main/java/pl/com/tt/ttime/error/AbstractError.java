package pl.com.tt.ttime.error;

import org.springframework.http.HttpStatus;

public interface AbstractError {
    HttpStatus getStatus();
}
