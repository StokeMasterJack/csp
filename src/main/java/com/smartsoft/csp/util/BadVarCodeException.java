package com.smartsoft.csp.util;

public class BadVarCodeException extends IllegalArgumentException {

    private BadVarCodeException() {
    }

    public BadVarCodeException(String varCode) {
        super(varCode);
    }

    public String getBadVarCode() {
        return getMessage();
    }
}
