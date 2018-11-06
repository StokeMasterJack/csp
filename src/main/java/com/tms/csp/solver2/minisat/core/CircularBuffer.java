package com.tms.csp.solver2.minisat.core;

import java.io.Serializable;

/**
 * Create a circular buffer of a given capacity allowing to compute efficiently
 * the mean of the values storied.
 * 
 * @author leberre
 * 
 */
public class CircularBuffer implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final int[] values;
    private int index = 0;
    private long sum = 0;
    private boolean full = false;

    public CircularBuffer(int capacity) {
        this.values = new int[capacity];
    }

    public void push(int value) {
        if (!this.full) {
            this.values[this.index++] = value;
            this.sum += value;
            if (this.index == this.values.length) {
                this.full = true;
                this.index = -1;
            }
            return;
        }
        this.index++;
        if (this.index == this.values.length) {
            this.index = 0;
        }
        // buffer full, overwrite
        this.sum -= this.values[this.index];
        this.values[this.index] = value;
        this.sum += value;
    }

    public long average() {
        if (this.full) {
            return this.sum / this.values.length;
        }
        if (this.index == 0) {
            return 0;
        }
        return this.sum / this.index;
    }

    public void clear() {
        this.index = 0;
        this.full = false;
        this.sum = 0;
    }

    public boolean isFull() {
        return this.full;
    }
}
