package com.tms.csp.fm.node;

import java.util.Arrays;

public class Solution {

    public int dcAssignments = -1;
    private final int[] dcOpenOutVars; //maps varIndex to bitIndex

    public Solution(int[] dcOpenOutVars) {
        this.dcOpenOutVars = dcOpenOutVars;
    }

    private int computeBitIndex(int varId) {
        int xx = Arrays.binarySearch(dcOpenOutVars, varId);
        if (xx >= 0) {
            return xx;
        } else {
            return -1;
        }
    }

    public boolean isBitSet(int varId) {
        int bitIndex = computeBitIndex(varId);
        return (dcAssignments & (1 << bitIndex)) != 0;
    }

}
