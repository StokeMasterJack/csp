package com.tms.csp.util;

public class Count {

    private int count;

    public Count() {
        this(0);
    }

    public Count(int count) {
        this.count = count;
    }

    public void increment() {
        count++;
    }

    public int getCount() {
        return count;
    }
}
