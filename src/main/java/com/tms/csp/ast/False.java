package com.tms.csp.ast;


import com.google.common.collect.ImmutableSet;
import com.tms.csp.fm.dnnf.products.Cube;

import java.util.Set;

public class False extends Constant {

    private final Exp pos;

    False(Exp pos) {
        super(pos.getSpace(),FALSE_EXP_ID);
        this.pos = pos;
    }

    @Override
    public boolean isConstantFalse() {
        return true;
    }

    @Override
    public boolean isSat() {
        return false;
    }

    @Override
    public long getSatCount() {
        return 0L;
    }

    @Override
    public int getCubeCount() {
        return 0;
    }


    @Override
    public void serialize(Ser a) {
        a.constantFalse();
    }

    @Override
    final public Exp flip() {
        return pos;
    }

    @Override
    public boolean isPos() {
        return false;
    }

    @Override
    public Exp getPos() {
        return pos;
    }

    @Override
    public Exp getNeg() {
        return this;
    }

    @Override
    public void serializeTinyDnnf(Ser a) {
        a.append(FALSE_CHAR);
    }

    @Override
    public Set<Cube> getCubesSmooth() {
        return ImmutableSet.of();
    }

    @Override
    public void toXml(Ser a, int depth) {
        a.indent(depth);
        a.constantFalse();
    }

    public void prindent(Ser a, int depth) {
        a.prindent(depth, toString());
    }


    public False asFalse() {
        return  this;
    }
}