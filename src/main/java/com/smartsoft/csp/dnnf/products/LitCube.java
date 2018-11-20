package com.smartsoft.csp.dnnf.products;

import com.google.common.collect.Iterators;
import com.smartsoft.csp.ast.Lit;
import com.smartsoft.csp.ast.Space;
import com.smartsoft.csp.ast.Var;
import com.smartsoft.csp.util.varSets.VarSet;
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
        return lit.getVr().mkSingletonVarSet();
    }

    @Override
    public Iterator<Var> varIterator() {
        return Iterators.singletonIterator(lit.getVr());
    }

    @NotNull
    @Override
    public Iterator<Lit> litIterator() {
        return Iterators.singletonIterator(lit);
    }

    @Override
    public VarSet getTrueVars() {
        if (lit.isPos()) {
            return lit.getVr().mkSingletonVarSet();
        } else {
            return getSpace().mkEmptyVarSet();
        }
    }

}
