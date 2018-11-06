package com.tms.csp.trail;


public enum CauseTypePoop {
    DECISION, INFERRED_FROM_EVAL, INFERRED_FROM_PARENT, TRANSFORMER;


    public boolean isDecision() {
        switch (this) {
            case DECISION:
                return true;
            default:
                return false;
        }
    }

    public boolean isInference() {
        return !isDecision();
    }

    public boolean isInferredFromParent() {
        switch (this) {
            case INFERRED_FROM_PARENT:
                return true;
            default:
                return false;
        }
    }
}
