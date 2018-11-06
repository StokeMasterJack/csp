package com.tms.csp.parse;

import com.tms.csp.ast.Ser;

public class PosComplexOpTokenizer extends StringTokenTokenizer {

    protected final PosOp posComplexOp;

    public PosComplexOpTokenizer(PosOp posComplexOp, Ser a) {
        super(posComplexOp.getComplexOpToken(a));
        this.posComplexOp = posComplexOp;
    }

    @Override
    public boolean isPosComplex() {
        return true;
    }

    @Override
    public PosOp getPosComplexOp() {
        return posComplexOp;
    }

    @Override
    public boolean isDcOr() {
       return posComplexOp.isDontCare();
    }
}
