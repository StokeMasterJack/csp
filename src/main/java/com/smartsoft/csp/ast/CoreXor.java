package com.smartsoft.csp.ast;

import com.smartsoft.csp.util.varSets.VarSet;

public class CoreXor implements IXor {

    private final String prefix;
    private final VarSet vars;

    public CoreXor(String prefix, VarSet vars) {
        this.prefix = prefix;
        this.vars = vars;
    }

    public boolean containsVar(int varId) {
        return vars.containsVarId(varId);
    }

    public boolean containsVar(Var var) {
        return containsVar(var.getVarId());
    }

    public Prefix getPrefix2() {
        return Prefix.get(prefix);
    }

    public String getPrefix() {
        return prefix;
    }

    public VarSet getVars() {
        return vars;
    }

    public boolean isMdl() {
        return Prefix.isMdl(prefix);
    }

    public boolean isICol() {
        return Prefix.isICol(prefix);
    }

    public boolean isXCol() {
        return Prefix.isXCol(prefix);
    }

    public boolean isYr() {
        return Prefix.isYr(prefix);
    }

    public int getLevel() {
        if (isMdl()) return 1;
        if (isICol()) return 2;
        if (isXCol()) return 3;
        if (isYr()) return 4;
        return 5;
    }

    @Override
    public String toString() {
        return prefix;
    }
}
