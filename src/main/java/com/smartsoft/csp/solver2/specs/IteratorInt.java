package com.smartsoft.csp.solver2.specs;

/**
 * Iterator interface to avoid boxing int into Integer.
 * 
 * @author daniel
 * 
 */
public interface IteratorInt {

    boolean hasNext();

    int next();
}
