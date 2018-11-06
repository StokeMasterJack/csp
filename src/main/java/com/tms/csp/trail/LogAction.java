package com.tms.csp.trail;

public class LogAction {

    public boolean isOpen() {
        return false;
    }

    public boolean isConflicted() {
        return false;
    }

    /**
     * AKA Justified
     * @return
     */
    public boolean isSolved() {
        return false;
    }

    public boolean isLeaf() {
        return isSolved() || isConflicted();
    }
}
