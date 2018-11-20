package com.smartsoft.csp.solver2.minisat.core;

import com.smartsoft.csp.solver2.specs.ILogAble;
import com.smartsoft.csp.solver2.specs.ISolver;
import com.smartsoft.csp.solver2.specs.UnitPropagationListener;

/**
 * Abstraction for ShortCircuit Driven Clause Learning Solver.
 * 
 * Allows to easily access the various options available to setup the solver.
 * 
 * @author daniel
 * 
 * @param <D>
 */
public interface ICDCL<D extends DataStructureFactory> extends ISolver,
        UnitPropagationListener, ActivityListener, Learner {

    /**
     * Change the internal representation of the fact. Note that the
     * heuristics must be changed prior to calling that method.
     * 
     * @param dsf
     *            the internal fact
     */
    void setDataStructureFactory(D dsf);

    /**
     * 
     * @since 2.2
     * @deprecated renamed into setLearningStrategy()
     * @see #setLearningStrategy(LearningStrategy)
     */
    @Deprecated
    void setLearner(LearningStrategy<D> learner);

    /**
     * Allow to change the learning strategy, i.e. to decide which
     * clauses/fact should be learned by the solver after conflict
     * analysis.
     * 
     * @since 2.3.3
     */
    void setLearningStrategy(LearningStrategy<D> strategy);

    void setSearchParams(SearchParams sp);

    SearchParams getSearchParams();

    SolverStats getStats();

    void setRestartStrategy(RestartStrategy restarter);

    RestartStrategy getRestartStrategy();

    /**
     * Setup the reason simplification strategy. By default, there is no reason
     * simplification. NOTE THAT REASON SIMPLIFICATION DOES NOT WORK WITH
     * SPECIFIC DATA STRUCTURE FOR HANDLING BOTH BINARY AND TERNARY CLAUSES.
     * 
     * @param simp
     *            a simplification type.
     * 
     */
    void setSimplifier(SimplificationType simp);

    /**
     * Setup the reason simplification strategy. By default, there is no reason
     * simplification. NOTE THAT REASON SIMPLIFICATION IS ONLY ALLOWED FOR WL
     * CLAUSAL data structures. USING REASON SIMPLIFICATION ON CB CLAUSES,
     * CARDINALITY CONSTRAINTS OR PB CONSTRAINTS MIGHT RESULT IN INCORRECT
     * RESULTS.
     * 
     * @param simp
     */
    void setSimplifier(ISimplifier simp);

    ISimplifier getSimplifier();

    /**
     * @param lcds
     * @since 2.1
     */
    void setLearnedConstraintsDeletionStrategy(
            LearnedConstraintsDeletionStrategy lcds);

    /**
     * 
     * @param timer
     *            when to apply fact cleanup.
     * @param evaluation
     *            the strategy used to evaluate learned clauses.
     * @since 2.3.2
     */
    void setLearnedConstraintsDeletionStrategy(ConflictTimer timer,
            LearnedConstraintsEvaluationType evaluation);

    /**
     * 
     * @param evaluation
     *            the strategy used to evaluate learned clauses.
     * @since 2.3.2
     */
    void setLearnedConstraintsDeletionStrategy(
            LearnedConstraintsEvaluationType evaluation);

    IOrder getOrder();

    void setOrder(IOrder h);

    void setNeedToReduceDB(boolean needToReduceDB);

    void setLogger(ILogAble out);

    ILogAble getLogger();
}