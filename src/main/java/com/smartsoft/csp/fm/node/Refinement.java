package com.smartsoft.csp.fm.node;

import com.smartsoft.csp.ast.Csp;
import com.smartsoft.csp.ast.PLConstants;

public abstract class Refinement implements PLConstants {

    Csp csp;
    public void setCsp(Csp csp) {
        this.csp = csp;
    }

    public Csp getCsp() {
        return csp;
    }

    abstract public void apply();

    abstract public String toString();

    public void print() {
        System.err.println("Refinement: " + toString());
    }
}
