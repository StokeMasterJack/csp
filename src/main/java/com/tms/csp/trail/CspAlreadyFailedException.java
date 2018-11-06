package com.tms.csp.trail;

public class CspAlreadyFailedException extends IllegalStateException {


    public CspAlreadyFailedException(Object xx ) {
    }

    public Object getAssignment() {
        return null;
    }

    @Override
    public String getMessage() {
        return null;
    }
}
