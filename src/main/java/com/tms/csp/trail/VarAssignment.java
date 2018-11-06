package com.tms.csp.trail;

import com.tms.csp.ast.PosComplexMultiVar;
import com.tms.csp.ast.Var;

import static com.google.common.base.Preconditions.checkArgument;

public class VarAssignment {

    private VarAssignment localPrevious;
    private VarAssignment globalPrevious; //overall

    private Var var;
    private boolean value;
    private PosComplexMultiVar cause;

    /**
     * First - Var Assignment (open to assigned)
     */
    public VarAssignment(VarAssignment globalPrevious, Var var, boolean newValue, PosComplexMultiVar newCause) {
        checkArgument(this.globalPrevious == null || !this.globalPrevious.isConflict());

        this.globalPrevious = globalPrevious;
        this.localPrevious = null;

        this.var = var;
        this.value = newValue;
        this.cause = newCause;
    }

    /**
     * Non-first Var Assignment
     */
    public VarAssignment(VarAssignment globalPrevious, VarAssignment localPrevious, boolean newValue, PosComplexMultiVar newCause) {
        checkArgument(this.globalPrevious == null || !this.globalPrevious.isConflict());
        checkArgument(this.localPrevious == null || !this.localPrevious.isConflict());

        this.globalPrevious = globalPrevious;
        this.localPrevious = null;

        this.var = localPrevious.getVar();
        this.value = newValue;
        this.cause = newCause;
    }

    public boolean isAssigned() {
        return !isConflict();
    }

    public boolean isAssignedValue(boolean assignedValue) {
        if (assignedValue) {
            return isTrue();
        } else {
            return isFalse();
        }
    }

    public boolean isTrue() {
        return this.value && (localPrevious == null || localPrevious.value == this.value);
    }

    public boolean isFalse() {
        return !this.value && (localPrevious == null || localPrevious.value == this.value);
    }

    public boolean isFirst() {
        return localPrevious == null;
    }

    public boolean isDup() {
        return localPrevious != null && localPrevious.value == this.value;
    }

    public boolean isConflict() {
        return localPrevious != null && localPrevious.value != this.value;
    }

    public Var getVar() {
        return var;
    }

    public boolean isValue() {
        return value;
    }

    public PosComplexMultiVar getCause() {
        return cause;
    }

    public VarAssignment getLocalPrevious() {
        return localPrevious;
    }

    public VarAssignment getGlobalPrevious() {
        return globalPrevious;
    }

    /**
     * Will never return VarState.OPEN
     */
    public VarState getState() {
        if (isTrue()) {
            return VarState.TRUE;
        } else if (isFalse()) {
            return VarState.FALSE;
        } else if (isConflict()) {
            return VarState.CONFLICTED;
        } else {
            throw new IllegalStateException();
        }
    }

    public boolean isLocalFirst() {
        return localPrevious == null;
    }

    public boolean isGlobalFirst() {
        return globalPrevious == null;
    }

}
