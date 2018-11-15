package com.tms.csp.ast;

import com.tms.csp.fm.dnnf.products.Cube;
import com.tms.csp.util.varSets.VarSet;

public abstract class Constant extends Exp {

    public Constant(Space space, int expId) {
        super(space, expId);
    }

    @Override
    final public boolean isConstant() {
        return true;
    }

    @Override
    final public boolean isConstant(boolean sign) {
        return sign == sign();
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public boolean checkDnnf() {
        return true;
    }

    @Override
    final public PosOp getPosOp() {
        return PosOp.TRUE;
    }

    @Override
    final public Var getFirstVar() {
        throw new IllegalStateException();
    }

    @Override
    final public boolean isOrContainsConstant() {
        return true;
    }

    @Override
    public boolean containsVarId(int varId) {
        return false;
    }

    @Override
    final public Exp simplify() {
        return this;
    }

    @Override
    public boolean isNew() {
        return false;
    }

    @Override
    final public Exp condition(Lit lit) {
                return this;
    }

    @Override
    final public Exp condition(Cube ctx) {
                return this;
    }

    @Override
    public Exp project(VarSet outVars) {
        return this;
    }

//    @Override
//    public void print(int depth) {
//        Exp.prindent(depth, serialize());
//    }

    @Override
    public int getVarCount() {
        return 0;
    }

    @Override
    public boolean anyVarOverlap(Exp exp) {
        return false;
    }

    @Override
    public boolean isDnnf() {
        return true;
    }

    @Override
    public boolean isSmooth() {
        return true;
    }

    @Override
    public Exp toDnnf() {
        return this;
    }

    @Override
    public boolean hasFlip() {
        return true;
    }

//    @Override
//    public Set<Lit> getLits() {
//        return ImmutableSet.of();
//    }

    @Override
    public VarSet getVars() {
        return getSpace().mkEmptyVarSet();
    }

    @Override
    public Exp litMatch() {
        return this;
    }

    public Exp getSmooth() {
        return this;
    }

    @Override
    public int getArgCount() {
        return 0;
    }

    @Override
    public void notNew() {
        throw new UnsupportedOperationException();
    }
}
