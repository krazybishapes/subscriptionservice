package com.finbox.subscrititionservice.exception;

public class ResourceNotFoundException extends SubscriptionServiceException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

