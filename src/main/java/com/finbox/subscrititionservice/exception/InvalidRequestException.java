package com.finbox.subscrititionservice.exception;

public class InvalidRequestException extends SubscriptionServiceException {
    public InvalidRequestException(String message) {
        super(message);
    }
}