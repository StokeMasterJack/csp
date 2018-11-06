package com.tms.csp.util.it;

import com.tms.csp.util.HasCode;

import java.util.Iterator;

public class ToCodeIterator implements Iterator<String> {

    private final Iterator<? extends HasCode> it;

    public ToCodeIterator(Iterator<? extends HasCode> it) {
        this.it = it;
    }

    @Override
    public boolean hasNext() {
        return it.hasNext();
    }

    @Override
    public String next() {
        HasCode next = it.next();
        return next.getCode();
    }

    @Override
    public void remove() {
        it.remove();
    }
}
