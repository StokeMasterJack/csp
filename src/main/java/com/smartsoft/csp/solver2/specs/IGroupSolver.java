package com.smartsoft.csp.solver2.specs;

/**
 * Represents a CNF formula which clauses are grouped into levels. It was first used
 * to build a high level MUS solver for SAT 2011 competition.
 * 
 * @author leberre
 * @since 2.3.3
 */
public interface IGroupSolver extends ISolver {

    /**
     * 
     * @param literals
     *            a clause
     * @param desc
     *            the level of the clause set
     * @return c object representing that clause formula the solver.
     * @throws ContradictionException
     */
    IConstr addClause(IVecInt literals, int desc) throws ContradictionException;

}