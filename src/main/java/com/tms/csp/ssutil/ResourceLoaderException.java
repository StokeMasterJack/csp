package com.tms.csp.ssutil;

public class ResourceLoaderException extends RuntimeException {

    public ResourceLoaderException() {
    }

    public ResourceLoaderException(String message) {
        super(message);
    }

    public ResourceLoaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceLoaderException(Throwable cause) {
        super(cause);
    }
}
