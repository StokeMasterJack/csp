package com.tms.csp.ast;

public class FailedCspException extends RuntimeException {

    public FailedCspException() {
    }

    public FailedCspException(String message) {
        super(message);
    }

    public FailedCspException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedCspException(Throwable cause) {
        super(cause);
    }
}
