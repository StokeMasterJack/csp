package com.tms.csp.solver2.minisat.core;

import java.io.Serializable;

/**
 * Perform a task when a given number of conflicts is reached.
 * 
 * @author daniel
 * 
 */
public abstract class ConflictTimerAdapter implements Serializable,
        ConflictTimer {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private int counter;

    private final int bound;

    public ConflictTimerAdapter(final int bound) {
        this.bound = bound;
        this.counter = 0;
    }

    public void reset() {
        this.counter = 0;
    }

    public void newConflict() {
        this.counter++;
        if (this.counter == this.bound) {
            run();
            this.counter = 0;
        }
    }

    public abstract void run();

    public int bound() {
        return this.bound;
    }
}