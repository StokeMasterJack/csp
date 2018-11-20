package com.smartsoft.csp.ast;

public final class ConflictingAssignmentException extends RuntimeException implements CspFailure {


    @Override
    public String getMessage() {
        return "ConflictingAssignment";
    }

    @Override
    public String toString() {
        return getSimpleName() + " " + getMessage();
    }

    public String getSimpleName() {
        return "ConflictingAssignment";
    }

}