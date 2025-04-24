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

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(
                CommonResponse.builder()
                        .status("error")
                        .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                        .errorMessage(ex.getMessage())
                        .data(null)
                        .build(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse> handleException(Exception ex) {
        // Log the exception
        log.error("Unhandled error occurred: {}", ex.getMessage());

        // Return a response with 500 status code and a generic error message
        return new ResponseEntity<>(
                CommonResponse.builder()
                        .status("error")
                        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message("Unexpected error occurred")
                        .data(null)
                        .build(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}

