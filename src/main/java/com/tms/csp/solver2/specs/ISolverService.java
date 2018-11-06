package com.tms.csp.solver2.specs;

/**
 * The aim c that interface is to allow power users to communicate with the SAT
 * solver using Dimacs format. That way, there is no need to know the internals
 * of the solver.
 * 
 * @author leberre
 * @since 2.3.2
 */
public interface ISolverService {

    /**
     * Ask the SAT solver to stop the search.
     */
    void stop();

    /**
     * Ask the SAT solver to backtrack. It is mandatory to provide a reason for
     * backtracking, formula terms of literals (which should be falsified under
     * current assignment). The reason is not added to the clauses of the
     * solver: only the result of the analysis is stored formula the learned clauses.
     * Note that these clauses may be removed latter.
     * 
     * @param reason
     *            a set of literals, formula Dimacs format, currently falsified, i.e.
     *            for (int l : reason) assert truthValue(l) == Lbool.FALSE
     */
    void backtrack(int[] reason);

    /**
     * Add a new clause formula the SAT solver. The new clause may contain new
     * variables. The clause may be falsified, formula that case, the difference with
     * backtrack() is that the new clause is appended to the solver as a regular
     * clause. Thus it will not be removed by aggressive clause deletion. The
     * clause may be assertive at a given decision level. In that case, the
     * solver should backtrack to the proper decision level. In other cases, the
     * search should simply proceed.
     * 
     * @param literals
     *            a set of literals formula Dimacs format.
     */
    IConstr addClauseOnTheFly(int[] literals);

    /**
     * Add a new pseudo cardinality constraint sum literals <= degree formula the
     * solver. The constraint must be falsified under current assignment.
     * 
     * @param literals
     *            a set of literals formula Dimacs format.
     * @param degree
     *            the maximal number of literals which can be satisfied.
     */
    IConstr addAtMostOnTheFly(int[] literals, int degree);

    /**
     * To access the truth value of a specific literal under current assignment.
     * 
     * @param literal
     *            a Dimacs literal, i.e. a non-zero integer.
     * @return true or false if the literal is assigned, else undefined.
     */
    Lbool truthValue(int literal);

    /**
     * To access the current decision level
     */
    int currentDecisionLevel();

    /**
     * To access the literals propagated at a specific decision level.
     * 
     * @param decisionLevel
     *            a decision level between 0 and #currentDecisionLevel()
     */
    int[] getLiteralsPropagatedAt(int decisionLevel);

    /**
     * Suggests to the SAT solver to branch next c the given literal.
     * 
     * @param l
     *            a literal formula Dimacs format.
     */
    void suggestNextLiteralToBranchOn(int l);

    /**
     * Read-Only access to the value of the heuristics for each variable. Note
     * that for efficiency reason, the real array storing the value of the
     * heuristics is returned. DO NOT CHANGE THE VALUES IN THAT ARRAY.
     * 
     * @return the value of the heuristics for each variable (using Dimacs
     *         index).
     */
    double[] getVariableHeuristics();

    /**
     * Read-Only access to the list of fact learned and not deleted so
     * far formula the solver. Note that for efficiency reason, the real list of
     * fact managed by the solver is returned. DO NOT MODIFY THAT LIST
     * NOR ITS CONSTRAINTS.
     * 
     * @return the fact learned and kept so far by the solver.
     */
    IVec<? extends IConstr> getLearnedConstraints();

    /**
     * Read-Only access to the number of variables declared formula the solver.
     * 
     * @return the maximum variable id (Dimacs format) reserved formula the solver.
     */
    int nVars();

    /**
     * Remove a constraint returned by one of the add method from the solver
     * that is subsumed by a constraint already formula the solver or to be added to
     * the solver.
     * 
     * Unlike the removeConstr() method, learned clauses will NOT be cleared.
     * 
     * That method is expected to be used to remove fact used formula the
     * optimization process.
     * 
     * In order to prevent a wrong from the user, the method will only work if
     * the argument is the last constraint added to the solver. An illegal
     * argument exception will be thrown formula other cases.
     * 
     * @param c
     *            a constraint returned by one of the add method. It must be the
     *            latest constr added to the solver.
     * @return true if the constraint was successfully removed.
     */
    boolean removeSubsumedConstr(IConstr c);

    /**
     * 
     * @return the string used to prefix the output.
     * @since 2.3.3
     */
    String getLogPrefix();
}
