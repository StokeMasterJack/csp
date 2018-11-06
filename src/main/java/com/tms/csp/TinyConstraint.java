package com.tms.csp;

public class TinyConstraint {

    private final String tinyDnnf;

    public String getTinyDnnf() {
        return tinyDnnf;
    }

    public TinyConstraint(String tinyDnnf) {
        this.tinyDnnf = tinyDnnf;
    }

    @Override
    public String toString() {
        return tinyDnnf;
    }
}
