package com.finbox.subscrititionservice.exception;

public class SubscriptionServiceException extends Exception {

    private static final long serialVersionUID = 1L;

    public SubscriptionServiceException(String message) {
        super(message);
    }

    public SubscriptionServiceException(String message, int code) {
        super(message);
    }

    public SubscriptionServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public SubscriptionServiceException(Throwable cause) {
        super(cause);
    }
}
