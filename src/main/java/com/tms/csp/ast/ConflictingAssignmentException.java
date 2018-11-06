package com.tms.csp.ast;

public final class ConflictingAssignmentException extends RuntimeException implements CspFailure {

    private final int varId;
    private final boolean failedValue;

    public ConflictingAssignmentException(int varId, boolean failedValue) {
        this.varId = varId;
        this.failedValue = failedValue;
    }

    public ConflictingAssignmentException(Lit failedAssignment) {
        this(failedAssignment.getVarId(), failedAssignment.sign());
    }

    public int getVarId() {
        return varId;
    }

    public boolean getFailedValue() {
        return failedValue;
    }

    @Override
    public String getMessage() {
        return "varId[" + varId + "]  failedValue[" + failedValue + "]";
    }

    @Override
    public String toString() {
        return getSimpleName() + " " + getMessage();
    }

    public String getSimpleName() {
        return "ConflictingAssignment";
    }

}