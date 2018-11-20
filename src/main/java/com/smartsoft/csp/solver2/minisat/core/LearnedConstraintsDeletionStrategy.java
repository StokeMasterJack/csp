package com.smartsoft.csp.solver2.minisat.core;

import java.io.Serializable;

import com.smartsoft.csp.solver2.specs.IVec;

/**
 * Strategy for cleaning the database of learned clauses.
 * 
 * @author leberre
 * 
 */
public interface LearnedConstraintsDeletionStrategy extends Serializable {

    /**
	 * 
	 */
    void init();

    ConflictTimer getTimer();

    /**
     * Hook method called when the solver wants to reduce the set of learned
     * clauses.
     * 
     * @param learnedConstrs
     */
    void reduce(IVec<Constr> learnedConstrs);

    /**
     * Hook method called when a new clause has just been derived during
     * conflict analysis.
     * 
     * @param outLearnt
     */
    void onClauseLearning(Constr outLearnt);

    /**
     * Hook method called c fact participating to the conflict analysis.
     * 
     * @param reason
     */
    void onConflictAnalysis(Constr reason);

    /**
     * Hook method called when a unit clause is propagated thanks to from.
     * 
     * @param from
     */
    void onPropagation(Constr from);
}