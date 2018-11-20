package com.smartsoft.csp.trail;

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
