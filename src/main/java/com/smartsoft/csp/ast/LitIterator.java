package com.smartsoft.csp.ast;

import com.smartsoft.csp.fm.dnnf.products.Cubes;
import com.smartsoft.csp.fm.dnnf.products.VarPredicate;
import com.smartsoft.csp.util.varSets.VarSet;

import java.util.Iterator;

public final class LitIterator implements Iterator<Lit> {

    private final Iterator<Var> varIterator;
    private final VarPredicate predicate;

    public LitIterator(Iterator<Var> varIterator, VarPredicate predicate) {
        this.predicate = predicate;
        this.varIterator = varIterator;
    }

    public LitIterator(Iterator<Var> varIterator, boolean sign) {
        this.predicate = Cubes.constantVarPredicate(sign);
        this.varIterator = varIterator;
    }

    public LitIterator(Iterator<Var> varIterator, Var trueVar) {
        this.predicate = Cubes.simpleVarPredicate(trueVar);
        this.varIterator = varIterator;
    }

    public LitIterator(Iterator<Var> varIterator, VarSet trueVars) {
        this.predicate = Cubes.simpleVarPredicate(trueVars);
        this.varIterator = varIterator;
    }

    @Override
    public boolean hasNext() {
        return varIterator.hasNext();
    }

    @Override
    public Lit next() {
        Var var = varIterator.next();
        boolean sign = predicate.isTrue(var);
        return var.lit(sign);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
