package com.smartsoft.csp.util;

public class BadVarIdException extends IllegalArgumentException {

    private BadVarIdException() {
    }

    public BadVarIdException(int varId) {
        super(varId + "");
    }
}
