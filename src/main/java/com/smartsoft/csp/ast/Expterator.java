package com.smartsoft.csp.ast;

import com.smartsoft.csp.fm.dnnf.products.Cube;

import java.util.Iterator;

public final class Expterator implements Iterator<Lit> {

    private final Cube cube;
    private final Iterator<Var> it;

    public Expterator(Cube cube) {
        this.cube = cube;
        this.it = cube.varIterator();
    }

    @Override
    public boolean hasNext() {
        return it.hasNext();
    }

    @Override
    public Lit next() {
        Var var = it.next();
        boolean sign = cube.isTrue(var);
        return var.lit(sign);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
