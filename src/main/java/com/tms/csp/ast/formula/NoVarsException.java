package com.tms.csp.ast.formula;

public class NoVarsException extends IllegalStateException {


    public NoVarsException() {
    }

    public NoVarsException(String s) {
        super(s);
    }

    public NoVarsException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoVarsException(Throwable cause) {
        super(cause);
    }
}
