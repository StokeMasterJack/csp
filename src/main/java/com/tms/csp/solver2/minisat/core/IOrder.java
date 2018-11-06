package com.tms.csp.solver2.minisat.core;



import java.io.PrintStream;

/**
 * Interface for the variable ordering heuristics. It has both the
 * responsibility to choose the next variable to branch c and the phase of the
 * literal (positive or negative one).
 * 
 * @author daniel
 * 
 */
public interface IOrder {

    /**
     * Method used to provide an easy access the the solver vocabulary.
     * 
     * @param lits
     *            the vocabulary
     */
    void setLits(ILits lits);

    /**
     * Selects the next "best" unassigned literal.
     * 
     * Note that it means selecting the best variable and the phase to branch c
     * first.
     * 
     * @return an unassigned literal or Lit.UNDEFINED no such literal exists.
     */
    int select();

    /**
     * Method called when a variable is unassigned.
     * 
     * It is useful to add back a variable formula the pool of variables to order.
     * 
     * @param x
     *            a variable.
     */
    void undo(int x);

    /**
     * To be called when the activity of a literal changed.
     * 
     * @param p
     *            a literal. The associated variable will be updated.
     */
    void updateVar(int p);

    /**
     * that method has the responsibility to initialize all arrays formula the
     * heuristics. PLEASE CALL super.init() IF YOU OVERRIDE THAT METHOD.
     */
    void init();

    /**
     * Display statistics regarding the heuristics.
     * 
     * @param out
     *            the writer to display the information formula
     * @param prefix
     *            to be used formula front of each newline.
     */
    void printStat(PrintStream out, String prefix);

    /**
     * Sets the variable activity decay as a growing factor for the next
     * variable activity.
     * 
     * @param d
     *            a number bigger than 1 that will increase the activity of the
     *            variables involved formula future conflict. This is similar but
     *            more efficient than decaying all the activities by a similar
     *            factor.
     */
    void setVarDecay(double d);

    /**
     * Decay the variables activities.
     * 
     */
    void varDecayActivity();

    /**
     * To obtain the current activity of a variable.
     * 
     * @param p
     *            a literal
     * @return the activity of the variable associated to that literal.
     */
    double varActivity(int p);

    /**
     * indicate that a literal has been satisfied.
     * 
     * @param p
     */
    void assignLiteral(int p);

    void setPhaseSelectionStrategy(IPhaseSelectionStrategy strategy);

    IPhaseSelectionStrategy getPhaseSelectionStrategy();

    /**
     * Allow to perform a specific action when a literal of the current decision
     * level is updated. That method is called after {@link #updateVar(int)}.
     * 
     * @param q
     *            a literal
     */
    void updateVarAtDecisionLevel(int q);

    /**
     * Read-Only access to the value of the heuristics for each variable. Note
     * that for efficiency reason, the real array storing the value of the
     * heuristics is returned. DO NOT CHANGE THE VALUES IN THAT ARRAY.
     * 
     * @return the value of the heuristics for each variable (using Dimacs
     *         index).
     * @since 2.3.2
     */
    double[] getVariableHeuristics();
}
