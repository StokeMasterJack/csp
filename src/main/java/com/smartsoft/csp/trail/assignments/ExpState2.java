package com.smartsoft.csp.trail.assignments;


public class ExpState2 {

    private final AssignmentSupport as;

    public ExpState2(AssignmentSupport as) {
        this.as = as;
    }

    public boolean isOpen() {
        return as == null || as.isOpen();
    }

    public boolean isTrue() {
        return as != null && as.isTrue();
    }

    public boolean isFalse() {
        return as != null && as.isFalse();
    }

    public boolean isConflict() {
        return as != null && as.isConflicted();
    }

    public boolean isAssigned() {
        return isTrue() || isFalse();
    }

    @Override
    public String toString() {
        if (isOpen()) {
            return "OPEN";
        } else if (isTrue()) {
            return "TRUE";
        } else if (isFalse()) {
            return "FALSE";
        } else if (isConflict()) {
            return "ShortCircuit";
        } else {
            throw new IllegalStateException();
        }
    }


}
