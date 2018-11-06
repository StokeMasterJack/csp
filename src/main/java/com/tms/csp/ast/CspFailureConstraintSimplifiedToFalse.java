package com.tms.csp.ast;

public class CspFailureConstraintSimplifiedToFalse implements CspFailure {

    private final Exp before;
    private final Exp lit;

    public CspFailureConstraintSimplifiedToFalse(Exp before, Exp lit) {
        this.before = before;
        this.lit = lit;
    }

    public Exp getBefore() {
        return before;
    }

    public Exp getLit() {
        return lit;
    }

    public String getMessage() {
        return "pic[" + lit + "] caused constraint[" + before + "] to simplify to FALSE";
    }
}
