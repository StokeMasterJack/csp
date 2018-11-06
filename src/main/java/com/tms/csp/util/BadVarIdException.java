package com.tms.csp.util;

public class BadVarIdException extends IllegalArgumentException {

    private BadVarIdException() {
    }

    public BadVarIdException(int varId) {
        super(varId + "");
    }
}
