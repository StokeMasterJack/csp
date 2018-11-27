package com.smartsoft.csp.varSets;

import com.google.common.collect.UnmodifiableIterator;
import com.smartsoft.csp.util.ints.IntIterator;

public class VsIterator<E> extends UnmodifiableIterator<E> {

    private final IntIterator it;
    private final Converter<E> c;

    public VsIterator(IntIterator it, Converter<E> c) {
        this.it = it;
        this.c = c;
    }

    public VsIterator(VarSet varSet, Converter<E> c) {
        this.it = varSet.intIterator();
        this.c = c;
    }

    @Override
    public boolean hasNext() {
        return it.hasNext();
    }

    @Override
    public E next() {
        int varId = it.next();
        return c.toE(varId);
    }

}
