package com.tms.csp.solver2.minisat.core;

import java.io.Serializable;

/**
 * Utility class to be used to return the two results of a conflict analysis.
 * 
 * @author daniel
 * 
 */
public final class Pair implements Serializable {

    private static final long serialVersionUID = 1L;

    public int backtrackLevel;
    public Constr reason;
}