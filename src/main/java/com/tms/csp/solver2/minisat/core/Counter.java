package com.tms.csp.solver2.minisat.core;

/**
 * @since 2.1
 */
public class Counter {
    private int value;

    public Counter() {
        this(1);
    }

    public Counter(int initialValue) {
        this.value = initialValue;
    }

    public void inc() {
        this.value++;
    }

    /**
     * @since 2.1
     */
    public void dec() {
        this.value--;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    /**
     * 
     * @return the value of the parseCounter.
     * @since 2.3.1
     */
    public int getValue() {
        return this.value;
    }
}
