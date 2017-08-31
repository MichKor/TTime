package pl.com.tt.ttime.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.com.tt.ttime.error.ErrorInformation;

import java.io.IOException;
import java.net.URISyntaxException;

@RestControllerAdvice
public class ExceptionHandlerResponse {

    private ResponseEntity<ErrorInformation> errorResponse(String errorMeessage, HttpStatus status) {
        ErrorInformation errorMsg = new ErrorInformation(errorMeessage);
        return new ResponseEntity<ErrorInformation>(errorMsg, status);
    }

    @ExceptionHandler({IOException.class, URISyntaxException.class})
    public ResponseEntity<ErrorInformation> generateExcelXlsException(){
        return errorResponse("Server has problem. Call to tech support", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
