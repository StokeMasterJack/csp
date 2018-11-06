package com.tms.csp.ast;

import java.util.Iterator;

public final class ExpLitIterator implements Iterator<Exp> {

    private final Iterator<Lit> litIterator;

    public ExpLitIterator(Iterator<Lit> litIterator) {
        this.litIterator = litIterator;
    }


    @Override
    public boolean hasNext() {
        return litIterator.hasNext();
    }

    @Override
    public Exp next() {
        return litIterator.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
