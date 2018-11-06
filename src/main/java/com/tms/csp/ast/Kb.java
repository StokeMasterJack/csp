package com.tms.csp.ast;

import com.tms.csp.ast.formula.Formula;

public class Kb extends And {

    private final CubeExp simple;
    private final Formula complex;

    public Kb(Space space, int expId, CubeExp simple, Formula complex) {
        super(space, expId, createArgs(simple, complex));
        this.simple = simple;
        this.complex = complex;
    }

    private static Exp[] createArgs(CubeExp simple, Formula complex) {
        Exp[] a = new Exp[2];
        a[0] = simple;
        a[1] = complex;
        return a;
    }
}
