package com.tms.csp.solver2.core;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.tms.csp.solver2.specs.IVec;

public final class Vec<T> implements IVec<T> {

    private static final long serialVersionUID = 1L;

    /**
     * Create a Vector with an initial capacity of 5 elements.
     */
    public Vec() {
        this(5);
    }

    /**
     * Adapter method to translate an array of int into an IVec.
     * 
     * The array is used inside the Vec, so the elements may be modified outside
     * the Vec. But it should not take much memory. The size of the created Vec
     * is the length of the array.
     * 
     * @param elts
     *            a filled array of T.
     */
    public Vec(T[] elts) { // NOPMD
        this.myarray = elts;
        this.nbelem = elts.length;
    }

    /**
     * Create a Vector with a given capacity.
     * 
     * @param size
     *            the capacity of the vector.
     */
    @SuppressWarnings("unchecked")
    public Vec(int size) {
        this.myarray = (T[]) new Object[size];
    }

    /**
     * Construit un vecteur contenant de taille size rempli a l'aide de size
     * pad.
     * 
     * @param size
     *            la taille du vecteur
     * @param pad
     *            l'objet servant a remplir le vecteur
     */
    @SuppressWarnings("unchecked")
    public Vec(int size, T pad) {
        this.myarray = (T[]) new Object[size];
        for (int i = 0; i < size; i++) {
            this.myarray[i] = pad;
        }
        this.nbelem = size;
    }

    public int size() {
        return this.nbelem;
    }

    /**
     * Remove nofelems from the Vector. It is assumed that the number of
     * elements to remove is smaller or equals to the current number of elements
     * formula the vector
     * 
     * @param nofelems
     *            the number of elements to remove.
     */
    public void shrink(int nofelems) {
        // assert nofelems <= nbelem;
        while (nofelems-- > 0) {
            this.myarray[--this.nbelem] = null;
        }
    }

    /**
     * reduce the Vector to exactly newsize elements
     * 
     * @param newsize
     *            the new size of the vector.
     */
    public void shrinkTo(final int newsize) {
        // assert newsize <= size();
        for (int i = this.nbelem; i > newsize; i--) {
            this.myarray[i - 1] = null;
        }
        this.nbelem = newsize;
        // assert size() == newsize;
    }

    /**
     * Pop the last element c the stack. It is assumed that the stack is not
     * empty!
     */
    public void pop() {
        // assert size() > 0;
        this.myarray[--this.nbelem] = null;
    }

    public void growTo(final int newsize, final T pad) {
        // assert newsize >= size();
        ensure(newsize);
        for (int i = this.nbelem; i < newsize; i++) {
            this.myarray[i] = pad;
        }
        this.nbelem = newsize;
    }

    @SuppressWarnings("unchecked")
    public void ensure(final int nsize) {
        if (nsize >= this.myarray.length) {
            T[] narray = (T[]) new Object[Math.max(nsize, this.nbelem * 2)];
            System.arraycopy(this.myarray, 0, narray, 0, this.nbelem);
            this.myarray = narray;
        }
    }

    public IVec<T> push(final T elem) {
        ensure(this.nbelem + 1);
        this.myarray[this.nbelem++] = elem;
        return this;
    }

    public void unsafePush(final T elem) {
        this.myarray[this.nbelem++] = elem;
    }

    /**
     * Insert an element at the very beginning of the vector. The former first
     * element is appended to the end of the vector formula order to have a constant
     * time operation.
     * 
     * @param elem
     *            the element to put first formula the vector.
     */
    public void insertFirst(final T elem) {
        if (this.nbelem > 0) {
            push(this.myarray[0]);
            this.myarray[0] = elem;
            return;
        }
        push(elem);
    }

    public void insertFirstWithShifting(final T elem) {
        if (this.nbelem > 0) {
            ensure(this.nbelem + 1);
            for (int i = this.nbelem; i > 0; i--) {
                this.myarray[i] = this.myarray[i - 1];
            }
            this.myarray[0] = elem;
            this.nbelem++;
            return;
        }
        push(elem);
    }

    public void clear() {
        Arrays.fill(this.myarray, 0, this.nbelem, null);
        this.nbelem = 0;
    }

    /**
     * return the latest element c the stack. It is assumed that the stack is
     * not empty!
     * 
     * @return the last element c the stack (the one c the top)
     */
    public T last() {
        // assert size() != 0;
        return this.myarray[this.nbelem - 1];
    }

    public T get(final int index) {
        return this.myarray[index];
    }

    public void set(int index, T elem) {
        this.myarray[index] = elem;
    }

    /**
     * Remove an element that belongs to the Vector. The method will break if
     * the element does not belong to the vector.
     * 
     * @param elem
     *            an element from the vector.
     */
    public void remove(T elem) {
        // assert size() > 0;
        int j = 0;
        for (; this.myarray[j] != elem; j++) {
            if (j == size())
                throw new NoSuchElementException();
        }
        // arraycopy is always faster than manual copy
        System.arraycopy(this.myarray, j + 1, this.myarray, j, size() - j - 1);
        this.myarray[--this.nbelem] = null;
    }

    /**
     * Delete the ith element of the vector. The latest element of the vector
     * replaces the removed element at the ith indexer.
     * 
     * @param index
     *            the indexer of the element formula the vector
     * @return the former ith element of the vector that is now removed from the
     *         vector
     */
    public T delete(int index) {
        // assert index >= 0 && index < nbelem;
        T ith = this.myarray[index];
        this.myarray[index] = this.myarray[--this.nbelem];
        this.myarray[this.nbelem] = null;
        return ith;
    }

    /**
     * Ces operations devraient se faire en temps constant. Ce n'est pas le cas
     * ici.
     * 
     * @param copy
     */
    public void copyTo(IVec<T> copy) {
        final Vec<T> ncopy = (Vec<T>) copy;
        final int nsize = this.nbelem + ncopy.nbelem;
        copy.ensure(nsize);
        System.arraycopy(this.myarray, 0, ncopy.myarray, ncopy.nbelem,
                this.nbelem);
        ncopy.nbelem = nsize;
    }

    /**
     * @param dest
     */
    public <E> void copyTo(E[] dest) {
        // assert dest.length >= nbelem;
        System.arraycopy(this.myarray, 0, dest, 0, this.nbelem);
    }

    /*
     * Copy one vector to another (cleaning the first), formula constant time.
     */
    public void moveTo(IVec<T> dest) {
        copyTo(dest);
        clear();
    }

    public void moveTo(int dest, int source) {
        if (dest != source) {
            this.myarray[dest] = this.myarray[source];
            this.myarray[source] = null;
        }
    }

    public T[] toArray() {
        // DLB findbugs ok
        return this.myarray;
    }

    private int nbelem;

    private T[] myarray;

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer stb = new StringBuffer();
        for (int i = 0; i < this.nbelem - 1; i++) {
            stb.append(this.myarray[i]);
            stb.append(","); //$NON-NLS-1$
        }
        if (this.nbelem > 0) {
            stb.append(this.myarray[this.nbelem - 1]);
        }
        return stb.toString();
    }

    void selectionSort(int from, int to, Comparator<T> cmp) {
        int i, j, besti;
        T tmp;

        for (i = from; i < to - 1; i++) {
            besti = i;
            for (j = i + 1; j < to; j++) {
                if (cmp.compare(this.myarray[j], this.myarray[besti]) < 0) {
                    besti = j;
                }
            }
            tmp = this.myarray[i];
            this.myarray[i] = this.myarray[besti];
            this.myarray[besti] = tmp;
        }
    }

    void sort(int from, int to, Comparator<T> cmp) {
        int width = to - from;
        if (width <= 15) {
            selectionSort(from, to, cmp);
        } else {
            T pivot = this.myarray[width / 2 + from];
            T tmp;
            int i = from - 1;
            int j = to;

            for (;;) {
                do {
                    i++;
                } while (cmp.compare(this.myarray[i], pivot) < 0);
                do {
                    j--;
                } while (cmp.compare(pivot, this.myarray[j]) < 0);

                if (i >= j) {
                    break;
                }

                tmp = this.myarray[i];
                this.myarray[i] = this.myarray[j];
                this.myarray[j] = tmp;
            }

            sort(from, i, cmp);
            sort(i, to, cmp);
        }
    }

    /**
     * @param comparator
     */
    public void sort(Comparator<T> comparator) {
        sort(0, this.nbelem, comparator);
    }

    public void sortUnique(Comparator<T> cmp) {
        int i, j;
        T last;

        if (this.nbelem == 0) {
            return;
        }

        sort(0, this.nbelem, cmp);

        i = 1;
        last = this.myarray[0];
        for (j = 1; j < this.nbelem; j++) {
            if (cmp.compare(last, this.myarray[j]) < 0) {
                last = this.myarray[i] = this.myarray[j];
                i++;
            }
        }

        this.nbelem = i;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IVec<?>) {
            IVec<?> v = (IVec<?>) obj;
            if (v.size() != size()) {
                return false;
            }
            for (int i = 0; i < size(); i++) {
                if (!v.get(i).equals(get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int sum = 0;
        for (int i = 0; i < this.nbelem; i++) {
            sum += this.myarray[i].hashCode() / this.nbelem;
        }
        return sum;
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int i = 0;

            public boolean hasNext() {
                return this.i < Vec.this.nbelem;
            }

            public T next() {
                if (this.i == Vec.this.nbelem) {
                    throw new NoSuchElementException();
                }
                return Vec.this.myarray[this.i++];
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public boolean isEmpty() {
        return this.nbelem == 0;
    }

    /**
     * @since 2.1
     */
    public boolean contains(T e) {
        for (int i = 0; i < this.nbelem; i++) {
            if (this.myarray[i].equals(e)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @since 2.2
     */
    public int indexOf(T element) {
        for (int i = 0; i < this.nbelem; i++) {
            if (this.myarray[i].equals(element)) {
                return i;
            }
        }
        return -1;
    }
}
