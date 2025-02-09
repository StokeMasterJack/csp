package com.smartsoft.csp.dnnf;

public class ConflictingAssignmentException extends RuntimeException {

    private final int var;

    public ConflictingAssignmentException(int var) {
        super("varId[" + var + "]");
        this.var = var;
    }

    public int getVar() {
        return var;
    }
}

