package com.tms.csp.trail;

public class Decision extends Cause {

    @Override
    public String toString() {
        return "Decision";
    }

    @Override
    public boolean isDecision() {
        return true;
    }
}
