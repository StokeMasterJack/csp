package com.smartsoft.csp.varSet;

import com.google.common.collect.ImmutableSet;
import com.smartsoft.csp.util.ints.IntIterator;

import java.util.Iterator;

public class ConcatIntIterator implements IntIterator {

    private final Iterator<IntIterator> its;
    private IntIterator nextIt;
    private int nextInt;

    public ConcatIntIterator(Iterator<IntIterator> its) {
        this.its = its;
        computeNext();
    }

    public ConcatIntIterator(IntIterator it1, IntIterator it2) {
        this(its2(it1, it2));
        computeNext();
    }

    public static Iterator<IntIterator> its2(IntIterator it1, IntIterator it2) {
        ImmutableSet<IntIterator> s = ImmutableSet.of(it1, it2);
        return s.iterator();
    }

    @Override
    public boolean hasNext() {
        return nextInt != -1;
    }

    @Override
    public int next() {
        assert hasNext();
        int tmp = nextInt;
        computeNext();
        return tmp;
    }

    private void computeNext() {
        if (nextIt == null || !nextIt.hasNext()) {
            if (its.hasNext()) {
                nextIt = its.next();
            } else {
                nextIt = null;
            }
        }

        if (nextIt == null) {
            nextInt = -1;
            return;
        }

        nextInt = nextIt.next();


    }
}
