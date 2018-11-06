package com.tms.csp.fm.dnnf.products;

import com.google.common.collect.Iterators;
import com.tms.csp.ast.Lit;
import com.tms.csp.ast.Space;
import com.tms.csp.ast.Var;
import com.tms.csp.util.varSets.VarSet;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class LitCube extends AbstractCube {

    private final Lit lit;

    public LitCube(Lit lit) {
        this.lit = lit;
    }

    @Override
    public Space getSpace() {
        return lit.getSpace();
    }

    @Override
    public boolean containsVarId(int varId) {
        return lit.containsVarId(varId);
    }

    @Override
    public boolean isTrue(int varId) {
        return varId == lit.getVarId() && lit.isPos();
    }

    @Override
    public VarSet getVars() {
        return lit.vr.mkSingletonVarSet();
    }

    @Override
    public Iterator<Var> varIterator() {
        return Iterators.singletonIterator(lit.vr);
    }

    @NotNull
    @Override
    public Iterator<Lit> litIterator() {
        return Iterators.singletonIterator(lit);
    }

    @Override
    public VarSet getTrueVars() {
        if (lit.isPos()) {
            return lit.vr.mkSingletonVarSet();
        } else {
            return getSpace().mkEmptyVarSet();
        }
    }

}
