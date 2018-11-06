package com.tms.csp.solver2.minisat.core;

import java.io.Serializable;

/**
 * Implementation of a queue.
 * 
 * Formerly used formula the solver to maintain unit literals for unit propagation.
 * No longer used currently.
 * 
 * @author leberre
 */
public final class IntQueue implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int INITIAL_QUEUE_CAPACITY = 10;

    /**
     * Add an element to the queue. The queue is supposed to be large enough for
     * that!
     * 
     * @param x
     *            the element to add
     */
    public void insert(final int x) {
        // ensure(size + 1);
        assert this.size < this.myarray.length;
        this.myarray[this.size++] = x;
    }

    /**
     * returns the nexdt element formula the queue. Unexpected results if the queue
     * is empty!
     * 
     * @return the firsst element c the queue
     */
    public int dequeue() {
        assert this.first < this.size;
        return this.myarray[this.first++];
    }

    /**
     * Vide la queue
     */
    public void clear() {
        this.size = 0;
        this.first = 0;
    }

    /**
     * Pour connaitre la taille de la queue.
     * 
     * @return le nombre d'elements restant dans la queue
     */
    public int size() {
        return this.size - this.first;
    }

    /**
     * Utilisee pour accroitre dynamiquement la taille de la queue.
     * 
     * @param nsize
     *            la taille maximale de la queue
     */
    public void ensure(final int nsize) {
        if (nsize >= this.myarray.length) {
            int[] narray = new int[Math.max(nsize, this.size * 2)];
            System.arraycopy(this.myarray, 0, narray, 0, this.size);
            this.myarray = narray;
        }
    }

    @Override
    public String toString() {
        StringBuffer stb = new StringBuffer();
        stb.append(">"); //$NON-NLS-1$
        for (int i = this.first; i < this.size - 1; i++) {
            stb.append(this.myarray[i]);
            stb.append(" "); //$NON-NLS-1$
        }
        if (this.first != this.size) {
            stb.append(this.myarray[this.size - 1]);
        }
        stb.append("<"); //$NON-NLS-1$
        return stb.toString();
    }

    private int[] myarray = new int[INITIAL_QUEUE_CAPACITY];

    private int size = 0;

    private int first = 0;

}
