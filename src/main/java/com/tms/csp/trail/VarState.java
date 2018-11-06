package com.tms.csp.trail;

public enum VarState {

    TRUE, FALSE, CONFLICTED, OPEN;

    public boolean isTrue() {
        switch (this) {
            case TRUE:
                return true;
            default:
                return false;
        }
    }

    public boolean isFalse() {
        switch (this) {
            case FALSE:
                return true;
            default:
                return false;
        }
    }

    public boolean isAssigned() {
        switch (this) {
            case TRUE:
                return true;
            case FALSE:
                return true;
            default:
                return false;
        }
    }

    public boolean isConflicted() {
        switch (this) {
            case CONFLICTED:
                return true;
            default:
                return false;
        }
    }

    public boolean isOpen() {
        switch (this) {
            case OPEN:
                return true;
            default:
                return false;
        }
    }

    public static VarState fromBool(boolean newValue) {
        if (newValue) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

    public VarState assign(boolean newValue) throws AlreadyConflictedException {
        if (isConflicted()) {
            throw new IllegalStateException();
        } else if (isOpen()) {
            return VarState.fromBool(newValue);
        } else {
            boolean currentValue = this.toBool();
            if (currentValue == newValue) {
                return this;
            } else {
                return CONFLICTED;
            }
        }
    }

    public boolean toBool() {
        switch (this) {
            case TRUE:
                return true;
            case FALSE:
                return false;
            default:
                throw new IllegalStateException();
        }
    }
}
