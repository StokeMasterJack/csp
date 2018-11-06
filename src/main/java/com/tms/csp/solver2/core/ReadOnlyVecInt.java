package com.tms.csp.solver2.core;

import com.tms.csp.solver2.specs.IVecInt;
import com.tms.csp.solver2.specs.IteratorInt;

public final class ReadOnlyVecInt implements IVecInt {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private final IVecInt vec;

    public ReadOnlyVecInt(IVecInt vec) {
        this.vec = vec;
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean contains(int e) {
        return this.vec.contains(e);
    }

    public int containsAt(int e) {
        return this.vec.containsAt(e);
    }

    public int containsAt(int e, int from) {
        return this.vec.containsAt(e, from);
    }

    public void copyTo(IVecInt copy) {
        this.vec.copyTo(copy);
    }

    public void copyTo(int[] is) {
        this.vec.copyTo(is);
    }

    public int delete(int i) {
        throw new UnsupportedOperationException();
    }

    public void ensure(int nsize) {
        throw new UnsupportedOperationException();
    }

    public int get(int i) {
        return this.vec.get(i);
    }

    public void growTo(int newsize, int pad) {
        throw new UnsupportedOperationException();
    }

    public void insertFirst(int elem) {
        throw new UnsupportedOperationException();
    }

    public boolean isEmpty() {
        return this.vec.isEmpty();
    }

    public IteratorInt iterator() {
        return this.vec.iterator();
    }

    public int last() {
        return this.vec.last();
    }

    public void moveTo(IVecInt dest) {
        throw new UnsupportedOperationException();
    }

    public void moveTo(int[] dest) {
        throw new UnsupportedOperationException();
    }

    public void moveTo(int dest, int source) {
        throw new UnsupportedOperationException();
    }

    public void moveTo2(IVecInt dest) {
        throw new UnsupportedOperationException();
    }

    public IVecInt pop() {
        throw new UnsupportedOperationException();
    }

    public IVecInt push(int elem) {
        throw new UnsupportedOperationException();
    }

    public void remove(int elem) {
        throw new UnsupportedOperationException();
    }

    public void set(int i, int o) {
        throw new UnsupportedOperationException();
    }

    public void shrink(int nofelems) {
        throw new UnsupportedOperationException();
    }

    public void shrinkTo(int newsize) {
        throw new UnsupportedOperationException();
    }

    public int size() {
        return this.vec.size();
    }

    public void sort() {
        throw new UnsupportedOperationException();
    }

    public void sortUnique() {
        throw new UnsupportedOperationException();
    }

    public int unsafeGet(int eleem) {
        return this.vec.unsafeGet(eleem);
    }

    public void unsafePush(int elem) {
        throw new UnsupportedOperationException();
    }

    /**
     * @since 2.1
     */
    public int[] toArray() {
        throw new UnsupportedOperationException();
    }

    /**
     * @since 2.2
     */
    public int indexOf(int e) {
        return this.vec.indexOf(e);
    }

    @Override
    public String toString() {
        return this.vec.toString();
    }

    public void moveTo(int sourceStartingIndex, int[] dest) {
        throw new UnsupportedOperationException();
    }

    /**
     * 
     * @author sroussel
     * @since 2.3.1
     */
    public VecInt[] subset(int cardinal) {
        return null;
    }

    @Override
    public int hashCode() {
        return this.vec.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.vec.equals(obj);
    }

}
