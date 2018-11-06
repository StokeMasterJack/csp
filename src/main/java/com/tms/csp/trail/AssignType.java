package com.tms.csp.trail;

public enum AssignType {

    NEW,
    DUP,
    CONFLICT;

    public boolean isNew() {
        return this.equals(NEW);
    }

    public boolean isDup() {
        return this.equals(DUP);
    }

    public boolean isConflict() {
        return this.equals(CONFLICT);
    }
}
