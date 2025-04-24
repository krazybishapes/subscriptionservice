package com.finbox.subscrititionservice.exception;


import com.finbox.subscrititionservice.models.response.CommonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);



    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse> handleSubscriptionException(Exception ex) {
        // Default to 500 INTERNAL_SERVER_ERROR
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = ex.getMessage();

        // Customize status based on exception type
        if (ex instanceof ResourceNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex instanceof InvalidRequestException) {
            status = HttpStatus.BAD_REQUEST;
        } else if(ex instanceof SubscriptionServiceException) {
            message = ex.getMessage();
        }else{
            message = "An unexpected error occurred";
        }

        log.error("Handled SubscriptionServiceException: {}", ex.getMessage());

        return new ResponseEntity<>(
                CommonResponse.builder()
                        .status("error")
                        .statusCode(status.value())
                        .message(message)
                        .data(null)
                        .build(),
                status
        );
    }

}

