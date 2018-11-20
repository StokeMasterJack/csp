package com.smartsoft.csp.solver2.minisat.core;

/**
 * ShortCircuit based timer.
 * 
 * Used to perform a task when a conflict occurs.
 * 
 * @author daniel
 * 
 */
public interface ConflictTimer {

    void reset();

    void newConflict();
}