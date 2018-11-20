package com.smartsoft.csp.util.it;

import java.util.Iterator;

public class MappingIterator<F, T> implements Iterator<T> {

    private final Iterator<? extends F> it;
    private final java.util.function.Function<F, T> converter;

    public MappingIterator(Iterator<? extends F> it, java.util.function.Function<F, T> converter) {
        this.it = it;
        this.converter = converter;
    }

    @Override
    public boolean hasNext() {
        return it.hasNext();
    }

    @Override
    public T next() {
        F next = it.next();
        return converter.apply(next);
    }

    @Override
    public void remove() {
        it.remove();
    }
}
