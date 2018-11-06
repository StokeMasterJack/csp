package com.tms.csp.util.it;

import java.util.Iterator;

public class ToStringIterator implements Iterator<String> {

    private final Iterator it;

    public ToStringIterator(Iterator it) {
        this.it = it;
    }

    @Override
    public boolean hasNext() {
        return it.hasNext();
    }

    @Override
    public String next() {
        Object next = it.next();
        return next + "";
    }

    @Override
    public void remove() {
        it.remove();
    }
}
