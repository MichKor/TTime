package pl.com.tt.ttime.error;

import org.springframework.http.HttpStatus;

public enum TeamErrors implements AbstractError {
    TEAM_NOT_FOUND(HttpStatus.NO_CONTENT),
    ADMIN_MISSING(HttpStatus.NOT_ACCEPTABLE),
    PERMISSION_ERROR(HttpStatus.FORBIDDEN),
    TEAM_LEADER_MISSING(HttpStatus.NOT_ACCEPTABLE),
    TOO_MANY_TEAMS(HttpStatus.NOT_ACCEPTABLE),
    OK(HttpStatus.OK);

    private final HttpStatus status;

    private TeamErrors(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
