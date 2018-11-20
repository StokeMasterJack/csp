package com.smartsoft.csp.trail.node;

public enum NodeState {

    DIRTY,
    SOLVED,
    FAILED,
    OPEN;

    public boolean isDirty() {
        switch (this) {
            case DIRTY:
                return true;
            default:
                return false;
        }
    }

    public boolean isSolved() {
        switch (this) {
            case SOLVED:
                return true;
            default:
                return false;
        }
    }

    public boolean isFailed() {
        switch (this) {
            case FAILED:
                return true;
            default:
                return false;
        }
    }

    public boolean isStable() {
        return !isDirty();
    }

    public boolean isLeaf() {
        return isFailed() || isSolved();
    }

    public boolean isOpen() {
        switch (this) {
            case OPEN:
                return true;
            default:
                return false;
        }
    }

}
