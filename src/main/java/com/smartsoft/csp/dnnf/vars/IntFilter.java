package com.smartsoft.csp.dnnf.vars;

import com.smartsoft.csp.ast.PLConstants;

public abstract class IntFilter implements PLConstants {

    abstract public boolean accept(int varId);

    public static IntFilter skip1(final int varId1) {
        return new IntFilter() {
            @Override
            public boolean accept(int varId) {
                return varId != varId1;
            }
        };
    }

    public static IntFilter skip2(final int varId1, final int varId2) {
        return new IntFilter() {
            @Override
            public boolean accept(int varId) {
                return varId != varId1 && varId != varId2;
            }
        };
    }

}