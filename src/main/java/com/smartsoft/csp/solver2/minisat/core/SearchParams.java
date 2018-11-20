package com.smartsoft.csp.solver2.minisat.core;

import java.io.Serializable;

/**
 * Some parameters used during the search.
 * 
 * @author daniel
 * 
 */
public class SearchParams implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Default search parameters.
     * 
     */
    public SearchParams() {
        this(0.95, 0.999, 1.5, 100);
    }

    /**
     * 
     * @param conflictBound
     *            the initial conflict bound for the first restart.
     */
    public SearchParams(int conflictBound) {
        this(0.95, 0.999, 1.5, conflictBound);
    }

    public SearchParams(double confincfactor, int conflictBound) {
        this(0.95, 0.999, confincfactor, conflictBound);
    }

    /**
     * @param d
     *            variable decay
     * @param e
     *            clause decay
     * @param f
     *            conflict bound increase factor
     * @param i
     *            initialConflictBound
     */
    public SearchParams(double d, double e, double f, int i) {
        this.varDecay = d;
        this.claDecay = e;
        this.conflictBoundIncFactor = f;
        this.initConflictBound = i;
    }

    /**
     * @return la valeur de clause decay
     */
    public double getClaDecay() {
        return this.claDecay;
    }

    /**
     * @return la valeur de vr decay
     */
    public double getVarDecay() {
        return this.varDecay;
    }

    private double claDecay;

    private double varDecay;

    private double conflictBoundIncFactor;

    private int initConflictBound;



    /**
     * @param conflictBoundIncFactor
     *            the conflictBoundIncFactor to set
     */
    public void setConflictBoundIncFactor(double conflictBoundIncFactor) {
        this.conflictBoundIncFactor = conflictBoundIncFactor;
    }

    /**
     * @param initConflictBound
     *            the initConflictBound to set
     */
    public void setInitConflictBound(int initConflictBound) {
        this.initConflictBound = initConflictBound;
    }

    /**
     * @return the conflictBoundIncFactor
     */
    public double getConflictBoundIncFactor() {
        return this.conflictBoundIncFactor;
    }

    /**
     * @return the initConflictBound
     */
    public int getInitConflictBound() {
        return this.initConflictBound;
    }

    /**
     * @param claDecay
     *            the claDecay to set
     */
    public void setClaDecay(double claDecay) {
        this.claDecay = claDecay;
    }

    /**
     * @param varDecay
     *            the varDecay to set
     */
    public void setVarDecay(double varDecay) {
        this.varDecay = varDecay;
    }
}
