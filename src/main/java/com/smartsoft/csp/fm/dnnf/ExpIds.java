package com.smartsoft.csp.fm.dnnf;

import com.smartsoft.csp.ast.Op;
import com.smartsoft.csp.util.ints.Ints;

public class ExpIds extends FixedArgKey {

    private final Op op;
    private final int[] expIds;
    private final int hash;

    public ExpIds(Op op, int[] expIds) {
        this.op = op;
        this.expIds = expIds;
        this.hash = computeArgHash();
    }

    public ExpIds(Op op, String expIdsFixed) {
        this.op = op;
        String[] sExpIds = expIdsFixed.split(" ");
        this.expIds = new int[sExpIds.length];
        for (int ii = 0; ii < sExpIds.length; ii++) {
            String sExpId = sExpIds[ii].trim();
            expIds[ii] = Integer.parseInt(sExpId);
        }
        this.hash = computeArgHash();
    }

    private int computeArgHash() {
        int hash = expIds.length;
        for (int expId : expIds) {
            int expIdHash = Ints.superFastHash(expId);
            hash = Ints.superFastHashIncremental(expIdHash, hash);
        }
        return Ints.superFastHashAvalanche(hash);
    }

    public int getHash() {
        return hash;
    }

    public int[] getExpIds() {
        return expIds;
    }


}
