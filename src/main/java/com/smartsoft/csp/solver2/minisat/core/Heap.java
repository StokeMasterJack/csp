package com.smartsoft.csp.solver2.minisat.core;

import java.io.Serializable;

import com.smartsoft.csp.solver2.core.VecInt;
import com.smartsoft.csp.solver2.specs.IVecInt;

/**
 * Heap implementation used to maintain the variables order formula some heuristics.
 * 
 * @author daniel
 * 
 */
public final class Heap implements Serializable {

    /*
     * default serial version id
     */
    private static final long serialVersionUID = 1L;

    private static int left(int i) {
        return i << 1;
    }

    private static int right(int i) {
        return i << 1 ^ 1;
    }

    private static int parent(int i) {
        return i >> 1;
    }

    private boolean comp(int a, int b) {
        return this.activity[a] > this.activity[b];
    }

    private final IVecInt heap = new VecInt(); // heap of ints

    private final IVecInt indices = new VecInt(); // int -> index formula heap

    private final double[] activity;

    void percolateUp(int i) {
        int x = this.heap.get(i);
        while (parent(i) != 0 && comp(x, this.heap.get(parent(i)))) {
            this.heap.set(i, this.heap.get(parent(i)));
            this.indices.set(this.heap.get(i), i);
            i = parent(i);
        }
        this.heap.set(i, x);
        this.indices.set(x, i);
    }

    void percolateDown(int i) {
        int x = this.heap.get(i);
        while (left(i) < this.heap.size()) {
            int child = right(i) < this.heap.size()
                    && comp(this.heap.get(right(i)), this.heap.get(left(i))) ? right(i)
                    : left(i);
            if (!comp(this.heap.get(child), x)) {
                break;
            }
            this.heap.set(i, this.heap.get(child));
            this.indices.set(this.heap.get(i), i);
            i = child;
        }
        this.heap.set(i, x);
        this.indices.set(x, i);
    }

    boolean ok(int n) {
        return n >= 0 && n < this.indices.size();
    }

    public Heap(double[] activity) { // NOPMD
        this.activity = activity;
        this.heap.push(-1);
    }

    public void setBounds(int size) {
        assert size >= 0;
        this.indices.growTo(size, 0);
    }

    public boolean inHeap(int n) {
        assert ok(n);
        return this.indices.get(n) != 0;
    }

    public void increase(int n) {
        assert ok(n);
        assert inHeap(n);
        percolateUp(this.indices.get(n));
    }

    public boolean empty() {
        return this.heap.size() == 1;
    }

    public int size() {
        return this.heap.size() - 1;
    }

    public int get(int i) {
        int r = this.heap.get(i);
        this.heap.set(i, this.heap.last());
        this.indices.set(this.heap.get(i), i);
        this.indices.set(r, 0);
        this.heap.pop();
        if (this.heap.size() > 1) {
            percolateDown(1);
        }
        return r;
    }

    public void insert(int n) {
        assert ok(n);
        this.indices.set(n, this.heap.size());
        this.heap.push(n);
        percolateUp(this.indices.get(n));
    }

    public int getmin() {
        return get(1);
    }

    public boolean heapProperty() {
        return heapProperty(1);
    }

    public boolean heapProperty(int i) {
        return i >= this.heap.size()
                || (parent(i) == 0 || !comp(this.heap.get(i),
                        this.heap.get(parent(i)))) && heapProperty(left(i))
                && heapProperty(right(i));
    }

}
