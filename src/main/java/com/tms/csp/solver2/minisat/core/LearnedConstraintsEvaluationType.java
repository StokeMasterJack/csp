package com.tms.csp.solver2.minisat.core;

/**
 * List the available strategies to evaluate learned clauses.
 * 
 * @author leberre
 * 
 */
public enum LearnedConstraintsEvaluationType {
    /**
     * The clauses are evaluated according to their activity during conflict
     * analysis.
     */
    ACTIVITY,

    /**
     * The clauses are evaluated using Glucose's Literal Block Distance.
     */
    LBD,

    /**
     * LBD updated also when the unit propagation is performed c the unit
     * clauses.
     */
    LBD2;
}
