package com.tms.csp.util.it;

import com.tms.csp.util.ints.IntIterator;
import com.tms.csp.util.varSets.Converter;
import com.tms.csp.util.varSets.VarSet;
import com.tms.csp.util.varSets.VsIterator;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class VsSet<E> extends ImSet<E> {

    private final VarSet d;
    private final Converter<E> c;

    public VsSet(VarSet d, Converter<E> c) {
        this.d = d;
        this.c = c;
    }

    @Override
    public int size() {
        return d.size();
    }

    public boolean containsE(E that) {
        int varId = c.toVarId(that);
        return containsVarId(varId);
    }

    public boolean containsVarId(int varId) {
        return d.containsVarId(varId);
    }

    @Override
    public Iterator<E> iterator() {
        return new VsIterator<E>(intIterator(), c);
    }

    public IntIterator intIterator() {
        return d.intIterator();
    }

    @Override
    public boolean containsAll(Collection<?> other) {
        if (other instanceof VsSet) {
            VsSet that = (VsSet) other;
            return d.containsAllVars(that.d);
        } else if (other instanceof VarSet) {
            VarSet that = (VarSet) other;
            return d.containsAllVars(that);
        } else {
            for (Object o : other) {
                E e = (E) o;
                int varId = c.toVarId(e);
                if (!d.containsVarId(varId)) {
                    return false;
                }
            }
            return true;
        }

    }

    @Override
    public boolean equals(Object otherSet) {
        if (otherSet instanceof VsSet) {
            VsSet that = (VsSet) otherSet;
            return this.d.equals(that.d);
        }
        if (otherSet instanceof VarSet) {
            VarSet that = (VarSet) otherSet;
            return this.d.equals(that);
        } else {
            if (otherSet instanceof Set) {
                Set that = (Set) otherSet;
                if (d.size() != that.size()) {
                    return false;
                }
                return this.containsAll(that);
            } else {
                return false;
            }
        }
    }

    @Override
    public int hashCode() {
        return d.hashCode();
    }


}
