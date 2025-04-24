package com.finbox.subscrititionservice.exception;

public class SubscriptionServiceException extends Exception {

    private static final long serialVersionUID = 1L;
    int code;
    public SubscriptionServiceException(String message) {
        super(message);
    }

    public SubscriptionServiceException(String message, int code) {
        super(message);
        this.code = code;
    }

    public SubscriptionServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public SubscriptionServiceException(Throwable cause) {
        super(cause);
    }

    public int getStatusCode() {
        return this.code;
    }
}
