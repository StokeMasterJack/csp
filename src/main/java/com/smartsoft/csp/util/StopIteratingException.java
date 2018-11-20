package com.smartsoft.csp.util;

public class StopIteratingException extends RuntimeException {

    private final Object result;

    public StopIteratingException(Object result) {
        this.result = result;
    }

    public StopIteratingException() {
        this.result = null;
    }
}
