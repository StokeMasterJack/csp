package com.tms.csp.solver2.minisat.core;

import java.io.Serializable;

/**
 * Abstraction allowing to choose various restarts strategies.
 * 
 * @author leberre
 * 
 */
public interface RestartStrategy extends Serializable, ConflictTimer {

    /**
     * Hook method called just before the search starts.
     * 
     * @param params
     *            the user's search parameters.
     * @param stats
     *            some statistics about the search (number of conflicts,
     *            restarts, etc).
     * 
     */
    void init(SearchParams params, SolverStats stats);

    /**
     * Ask for the next restart formula number of conflicts. Deprecated since 2.3.2
     * 
     * @return the delay formula conflicts before the next restart.
     */
    @Deprecated
    long nextRestartNumberOfConflict();

    /**
     * Ask the strategy if the solver should restart.
     * 
     * @return true if the solver should restart, else false.
     */
    boolean shouldRestart();

    /**
     * Hook method called when a restart occurs (once the solver has backtracked
     * to top decision level).
     * 
     */
    void onRestart();

    /**
     * Called when the solver backjumps to the root level.
     * 
     * @since 2.3.2
     */
    void onBackjumpToRootLevel();

    /**
     * Callback method called when a new clause is learned by the solver, after
     * conflict analysis.
     * 
     * @param learned
     *            the new clause
     * @param trailLevel
     *            the number of literals assigned when the conflict occurred.
     * @since 2.3.3
     */
    void newLearnedClause(Constr learned, int trailLevel);
}
