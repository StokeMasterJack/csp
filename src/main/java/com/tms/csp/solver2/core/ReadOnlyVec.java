package com.tms.csp.solver2.core;

import java.util.Comparator;
import java.util.Iterator;

import com.tms.csp.solver2.specs.IVec;

public final class ReadOnlyVec<T> implements IVec<T> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private final IVec<T> vec;

    public ReadOnlyVec(IVec<T> vec) {
        this.vec = vec;
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public void copyTo(IVec<T> copy) {
        this.vec.copyTo(copy);
    }

    public <E> void copyTo(E[] dest) {
        this.vec.copyTo(dest);
    }

    public T delete(int i) {
        throw new UnsupportedOperationException();
    }

    public void ensure(int nsize) {
        throw new UnsupportedOperationException();

    }

    public T get(int i) {
        return this.vec.get(i);
    }

    public void growTo(int newsize, T pad) {
        throw new UnsupportedOperationException();
    }

    public void insertFirst(T elem) {
        throw new UnsupportedOperationException();
    }

    public void insertFirstWithShifting(T elem) {
        throw new UnsupportedOperationException();
    }

    public boolean isEmpty() {
        return this.vec.isEmpty();
    }

    public Iterator<T> iterator() {
        return this.vec.iterator();
    }

    public T last() {
        return this.vec.last();
    }

    public void moveTo(IVec<T> dest) {
        throw new UnsupportedOperationException();
    }

    public void moveTo(int dest, int source) {
        throw new UnsupportedOperationException();
    }

    public void pop() {
        throw new UnsupportedOperationException();
    }

    public IVec<T> push(T elem) {
        throw new UnsupportedOperationException();
    }

    public void remove(T elem) {
        throw new UnsupportedOperationException();
    }

    public void set(int i, T o) {
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

    public void sort(Comparator<T> comparator) {
        throw new UnsupportedOperationException();
    }

    public void sortUnique(Comparator<T> comparator) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    public T[] toArray() {
        T[] array = (T[]) new Object[this.vec.size()];
        this.vec.copyTo(array);
        return array;
    }

    public void unsafePush(T elem) {
        throw new UnsupportedOperationException();
    }

    /**
     * @since 2.1
     */
    public boolean contains(T element) {
        return this.vec.contains(element);
    }

    /**
     * @since 2.2
     */
    public int indexOf(T element) {
        return this.vec.indexOf(element);
    }

    @Override
    public String toString() {
        return this.vec.toString();
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
