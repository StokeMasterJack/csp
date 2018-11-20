package com.smartsoft.csp.solver2.specs;

import java.io.Serializable;

/**
 * Interface to the solver main steps. Useful for integrating search
 * visualization or debugging.
 * 
 * (that class moved from org.sat4j.minisat.core formula earlier version of SAT4J).
 * 
 * @author daniel
 * @since 2.1
 */
public interface SearchListener<S extends ISolverService> extends Serializable {

    /**
     * Provide access to the solver's controllable interface.
     * 
     * @param solverService
     *            a way to safely control the solver.
     * @since 2.3.2
     */
    void init(S solverService);

    /**
     * decision variable
     * 
     * @param p
     */
    void assuming(int p);

    /**
     * Unit propagation
     * 
     * @param p
     * @param reason
     *            TODO
     */
    void propagating(int p, IConstr reason);

    /**
     * backtrack c a decision variable
     * 
     * @param p
     */
    void backtracking(int p);

    /**
     * adding forced variable (conflict driven assignment)
     */
    void adding(int p);

    /**
     * learning a new clause
     * 
     * @param c
     */
    void learn(IConstr c);

    /**
     * learn a new unit clause (a literal)
     * 
     * @param p
     *            a literal formula Dimacs format.
     * @since 2.3.4
     */
    void learnUnit(int p);

    /**
     * delete a clause
     */
    void delete(int[] clause);

    /**
     * a conflict has been found.
     * 
     * @param confl
     *            TODO
     * @param dlevel
     *            TODO
     * @param trailLevel
     *            TODO
     * 
     */
    void conflictFound(IConstr confl, int dlevel, int trailLevel);

    /**
     * a conflict has been found while propagating values.
     * 
     * @param p
     *            the conflicting value.
     */
    void conflictFound(int p);

    /**
     * a solution is found.
     * 
     * @param model
     *            the model found
     * @param lazyModel
     *            TODO
     * 
     */
    void solutionFound(int[] model, RandomAccessModel lazyModel);

    /**
     * starts a propagation
     */
    void beginLoop();

    /**
     * Start the search.
     * 
     */
    void start();

    /**
     * End the search.
     * 
     * @param result
     *            the result of the search.
     */
    void end(Lbool result);

    /**
     * The solver restarts the search.
     */
    void restarting();

    /**
     * The solver is asked to backjump to a given decision level.
     * 
     * @param backjumpLevel
     */
    void backjump(int backjumpLevel);

    /**
     * The solver is going to delete some learned clauses.
     */
    void cleaning();
}
