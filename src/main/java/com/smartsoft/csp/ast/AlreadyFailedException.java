package com.smartsoft.csp.ast;

public class AlreadyFailedException extends IllegalStateException {

    public AlreadyFailedException() {
    }


    public AlreadyFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyFailedException(Throwable cause) {
        super(cause);
    }


}
