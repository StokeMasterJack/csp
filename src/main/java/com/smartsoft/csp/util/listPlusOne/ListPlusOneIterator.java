package com.smartsoft.csp.util.listPlusOne;

import java.util.Iterator;

public class ListPlusOneIterator<E extends Comparable<E>> implements Iterator<E> {

    private PlusOne<E> next;

    public ListPlusOneIterator(PlusOne<E> next) {
        this.next = next;
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public E next() {
        E retVal = next.getElement();
        next = next.getParent();
        return retVal;
    }

    @Override
    public void remove() {
        throw new IllegalStateException();
    }

}