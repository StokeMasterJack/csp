package com.smartsoft.csp.fm.explanations;

public enum Cause {

    INFERENCE,
    INFERENCE_ASSERTING,
    DECISION,
    USER,
    DEFAULT;

    public boolean isInference() {
        return this == INFERENCE;
    }


}
