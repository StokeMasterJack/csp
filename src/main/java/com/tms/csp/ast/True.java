package com.tms.csp.ast;


import com.tms.csp.util.ints.Ints;

import static com.google.common.base.Preconditions.checkState;

public class True extends Constant {

    private Exp neg;

    True(Space space, int expId) {
        super(space,expId);
    }

    @Override
    public boolean isConstantTrue() {
        return true;
    }

    @Override
    public boolean isSat() {
        return true;
    }

    @Override
    public long getSatCount() {
        System.err.println("True.getSatCount");
        return 1L;
    }

    @Override
    public int getCubeCount() {
        return 1;
    }

    @Override
    public void serialize(Ser a) {
        a.constantTrue();
    }

    final public Exp flip() {
        return getNeg();
    }

    @Override
    public boolean isPos() {
        return true;
    }

    @Override
    final public Exp getPos() {
        return this;
    }

    @Override
    final public Exp getNeg() {
        if (this.neg == null) {
            this.neg = _space.mkFalse();
        }
        return this.neg;
    }


    @Override
    public void serializeTinyDnnf(Ser a) {
        a.append(TRUE_CHAR);
    }

    @Override
    public void toXml(Ser a, int depth) {
        a.indent(depth);
        a.constantTrue();
    }

}