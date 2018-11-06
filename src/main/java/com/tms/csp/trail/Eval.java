package com.tms.csp.trail;

public class Eval extends Cause {

    @Override
    public boolean isEval() {
        return true;
    }

    @Override
    public String toString() {
        return "Eval";
    }
}
