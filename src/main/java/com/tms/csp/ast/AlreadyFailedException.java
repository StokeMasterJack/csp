package com.tms.csp.ast;

public class AlreadyFailedException extends IllegalStateException {

    public AlreadyFailedException() {
    }

    public AlreadyFailedException(String message) {
        super(message);
    }

    public AlreadyFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyFailedException(Throwable cause) {
        super(cause);
    }


}
