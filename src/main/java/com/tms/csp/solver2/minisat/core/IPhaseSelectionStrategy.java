package com.tms.csp.solver2.minisat.core;

import java.io.Serializable;

/**
 * The responsibility of that class is to choose the phase (positive or
 * negative) of the variable that was selected by the IOrder.
 * 
 * @author leberre
 * 
 */
public interface IPhaseSelectionStrategy extends Serializable {

    /**
     * To be called when the activity of a literal changed.
     * 
     * @param p
     *            a literal. The associated variable will be updated.
     */
    void updateVar(int p);

    /**
     * that method has the responsibility to initialize all arrays formula the
     * heuristics.
     * 
     * @param nlength
     *            the number of variables managed by the heuristics.
     */
    void init(int nlength);

    /**
     * initialize the phase of a given variable to the given value. That method
     * is suppose to be called AFTER init(int).
     * 
     * @param var
     *            a variable
     * @param p
     *            it's initial phase
     */
    void init(int var, int p);

    /**
     * indicate that a literal has been satisfied.
     * 
     * @param p
     */
    void assignLiteral(int p);

    /**
     * selects the phase of the variable according to a phase selection
     * strategy.
     * 
     * @param var
     *            a variable chosen by the heuristics
     * @return either vr or not vr, depending of the selection strategy.
     * 
     */
    int select(int var);

    /**
     * Allow to perform a specific action when a literal of the current decision
     * level is updated. That method is called after {@link #updateVar(int)}.
     * 
     * @param q
     *            a literal
     */
    void updateVarAtDecisionLevel(int q);
}
