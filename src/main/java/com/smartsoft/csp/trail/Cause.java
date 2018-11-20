package com.smartsoft.csp.trail;

public abstract class Cause {

    public static final Decision DECISION = new Decision();
    public static final Premise PREMISE = new Premise();
    public static final Eval EVAL = new Eval();

    public boolean isInferred() {
        return this instanceof Inference;
    }


    public boolean isEval() {
        return false;
    }

    public boolean isFlip() {
        return false;
    }

    public boolean isDecision() {
        return false;
    }
}

