package com.smartsoft.csp.ast;

import com.smartsoft.csp.dnnf.products.AbstractCube;
import com.smartsoft.csp.util.varSets.VarSet;

import java.util.Iterator;

public class BasicCube extends AbstractCube {

    protected final VarSet vars;
    protected final Var trueVar;

    public BasicCube(VarSet vars, Var trueVar) {
        this.vars = vars;
        this.trueVar = trueVar;
    }

    @Override
    public Space getSpace() {
        return vars.getSpace();
    }

    @Override
    public boolean containsVarId(int varId) {
        return vars.containsVarId(varId);
    }

    @Override
    public boolean isTrue(int varId) {
        return varId == trueVar.varId;
    }

    @Override
    public VarSet getVars() {
        return vars;
    }

    @Override
    public Iterator<Var> varIterator() {
        return vars.varIter();
    }

    public Var getTrueVar() {
        return trueVar;
    }

    @Override
    public boolean isFalse(int varId) {
        if (vars == null) {
            return false;
        }
        if (!vars.containsVarId(varId)) {
            return false;
        }
        return varId != trueVar.varId;
    }

    @Override
    public int getTrueVarCount() {
        return 1;
    }

    @Override
    public int getFalseVarCount() {
        return getVarCount() - 1;
    }

    @Override
    public int getVarCount() {
        return vars.size();
    }

    @Override
    public int getSize() {
        return vars.size();
    }
}
