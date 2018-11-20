package com.smartsoft.csp.util.varSets;

public class NSets {

    private final int varCount;

    private final VarSet[] nSets;

    public NSets(int varCount) {
        this.varCount = varCount;
        nSets = new VarSet[100];
    }
}
