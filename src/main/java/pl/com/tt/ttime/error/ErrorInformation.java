package pl.com.tt.ttime.error;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.HttpStatus;

public class ErrorInformation {
    private AbstractError errorMsg;
    private String errorMessage;

    public ErrorInformation(AbstractError errorMsg) {
        this.errorMsg = errorMsg;
    }

    public ErrorInformation(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public AbstractError getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(AbstractError errorMsg) {
        this.errorMsg = errorMsg;
    }

    @JsonIgnore
    public HttpStatus getStatus() {
        return errorMsg.getStatus();
    }
}
