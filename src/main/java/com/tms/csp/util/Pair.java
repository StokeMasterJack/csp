package com.tms.csp.util;

import com.google.common.collect.ImmutableSet;

import java.util.AbstractSet;
import java.util.Iterator;

/**
 * Unordered
 */
public class Pair<T> extends AbstractSet<T> {

    private final T arg1;
    private final T arg2;

    public Pair(T arg1, T arg2) {
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    public T getArg1() {
        return arg1;
    }

    public T getArg2() {
        return arg2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair pair = (Pair) o;
        if (!arg1.equals(pair.arg1)) return false;
        return arg2.equals(pair.arg2);
    }

    @Override
    public int hashCode() {
        int result = arg1.hashCode();
        result = 31 * result + arg2.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return arg1 + " " + arg2;
    }

    public ImmutableSet<T> toImmutableSet() {
        ImmutableSet<T> set = ImmutableSet.of(arg1, arg2);
        return set;
    }

    @Override
    public Iterator<T> iterator() {
        return toImmutableSet().iterator();
    }

    @Override
    public int size() {
        return 2;
    }
}
