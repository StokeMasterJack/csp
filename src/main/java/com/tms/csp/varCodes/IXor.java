package com.tms.csp.varCodes;

import com.google.common.collect.ImmutableSet;
import com.tms.csp.ast.Prefix;

public class IXor {

    private final String prefix;
    private final Prefix prefix2;
    private final ImmutableSet<VarCode> childVars;

    public IXor(String prefix, ImmutableSet<VarCode> childVars) {
        this.prefix = prefix;
        this.childVars = childVars;
        this.prefix2 = Prefix.get(prefix);
    }

    public String getPrefix() {
        return prefix;
    }

    public ImmutableSet<VarCode> getChildVars() {
        return childVars;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (VarCode childVar : childVars) {
            String localName = childVar.getLocalName();
            sb.append(localName);
            sb.append(' ');
        }
        String args = sb.toString().trim();
        return "xor[" + prefix + "] (" + args + ")";
    }

    public boolean isYr() {
        return prefix2 != null && prefix2.isYr();
    }

    public Prefix getPrefix2() {
        return prefix2;
    }

    public boolean isMdl() {
        return prefix2 != null && prefix2.isMdl();
    }

    public boolean isXCol() {
        return prefix2 != null && prefix2.isXCol();
    }
}
