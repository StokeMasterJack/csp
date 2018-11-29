package com.smartsoft.csp.varSet;

public class NSets {

    private final int varCount;

    private final VarSet[] nSets;

    public NSets(int varCount) {
        this.varCount = varCount;
        nSets = new VarSet[100];
    }
}
