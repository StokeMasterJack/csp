package com.smartsoft.csp.trail.state;

public enum Type {

    PREMISE, PICK, SEARCH_DECISION, PROPOSE, INFERENCE, EVAL, FLIP, INIT_PROPOSE;

    public boolean isPremise() {
        return this.equals(PREMISE);
    }

    public boolean isPick() {
        return this.equals(PICK);
    }


    public boolean isSearchDecision() {
        return this.equals(SEARCH_DECISION);
    }

    public boolean isProposeDecision() {
        return this.equals(PROPOSE);
    }

    public boolean isRoot() {
        return isPremise() || isPick() || isSearchDecision() || isProposeDecision();
    }

    public boolean isEval() {
        return this.equals(EVAL);
    }

}
