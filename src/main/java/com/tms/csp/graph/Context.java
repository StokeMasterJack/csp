package com.tms.csp.graph;

public class Context {

    public int depth;
    public int count;

    public Context() {
    }

    public Context(int depth, int count) {
        this.depth = depth;
        this.count = count;
    }

    public Context(int depth) {
        this.depth = depth;
    }
}
