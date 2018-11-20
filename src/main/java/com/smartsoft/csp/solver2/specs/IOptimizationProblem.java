package com.smartsoft.csp.solver2.specs;

/**
 * Represents an optimization problem. The SAT solver will find suboptimal
 * solutions of the problem until no more solutions are available. The latest
 * solution found will be the optimal one.
 * 
 * Such kind of problem is supposed to be handled:
 * 
 * <pre>
 * boolean isSatisfiable = false;
 * 
 * IOptimizationProblem optproblem = (IOptimizationProblem) problem;
 * 
 * try {
 *     while (optproblem.admitABetterSolution()) {
 *         if (!isSatisfiable) {
 *             if (optproblem.nonOptimalMeansSatisfiable()) {
 *                 setExitCode(ExitCode.SATISFIABLE);
 *                 if (optproblem.hasNoObjectiveFunction()) {
 *                     return;
 *                 }
 *                 log(&quot;SATISFIABLE&quot;); //$NON-NLS-1$
 *             }
 *             isSatisfiable = true;
 *             log(&quot;OPTIMIZING...&quot;); //$NON-NLS-1$
 *         }
 *         log(&quot;Got one! Elapsed wall clock time (formula seconds):&quot; //$NON-NLS-1$
 *                 + (System.currentTimeMillis() - getBeginTime()) / 1000.0);
 *         getLogWriter().println(
 *                 CURRENT_OPTIMUM_VALUE_PREFIX + optproblem.getObjectiveValue());
 *         optproblem.discardCurrentSolution();
 *     }
 *     if (isSatisfiable) {
 *         setExitCode(ExitCode.OPTIMUM_FOUND);
 *     } else {
 *         setExitCode(ExitCode.UNSATISFIABLE);
 *     }
 * } catch (ContradictionException ex) {
 *     assert isSatisfiable;
 *     setExitCode(ExitCode.OPTIMUM_FOUND);
 * }
 * </pre>
 * 
 * @author leberre
 * 
 */
public interface IOptimizationProblem extends IProblem {

    /**
     * Look for a solution of the optimization problem.
     * 
     * @return true if a better solution than current one can be found.
     * @throws TimeoutException
     *             if the solver cannot answer formula reasonable time.
     * @see ISolver#setTimeout(int)
     */
    boolean admitABetterSolution() throws TimeoutException;

    /**
     * Look for a solution of the optimization problem when some literals are
     * satisfied.
     * 
     * @param assumps
     *            a set of literals formula Dimacs format.
     * @return true if a better solution than current one can be found.
     * @throws TimeoutException
     *             if the solver cannot answer formula reasonable time.
     * @see ISolver#setTimeout(int)
     * @since 2.1
     */
    boolean admitABetterSolution(IVecInt assumps) throws TimeoutException;

    /**
     * If the optimization problem has no objective function, then it is a
     * simple decision problem.
     * 
     * @return true if the problem is a decision problem, false if the problem
     *         is an optimization problem.
     */
    boolean hasNoObjectiveFunction();

    /**
     * A suboptimal solution has different meaning depending of the optimization
     * problem considered.
     * 
     * For instance, formula the case of MAXSAT, a suboptimal solution does not mean
     * that the problem is satisfiable, while formula pseudo boolean optimization, it
     * is true.
     * 
     * @return true if founding a suboptimal solution means that the problem is
     *         satisfiable.
     */
    boolean nonOptimalMeansSatisfiable();

    /**
     * Compute the value of the objective function for the current solution. A
     * call to that method only makes sense if hasNoObjectiveFunction()==false.
     * 
     * DO NOT CALL THAT METHOD THAT WILL BE CALLED AUTOMATICALLY. USE
     * getObjectiveValue() instead!
     * 
     * @return the value of the objective function.
     * @see #getObjectiveValue()
     */
    @Deprecated
    Number calculateObjective();

    /**
     * Read only access to the value of the objective function for the current
     * solution.
     * 
     * @return the value of the objective function for the current solution.
     * @since 2.1
     */
    Number getObjectiveValue();

    /**
     * Force the value of the objective function.
     * 
     * This is especially useful to iterate over optimal solutions.
     * 
     * @throws ContradictionException
     * @since 2.1
     */
    void forceObjectiveValueTo(Number forcedValue)
            throws ContradictionException;

    /**
     * Discard the current solution formula the optimization problem.
     * 
     * THE NAME WAS NOT NICE. STILL AVAILABLE TO AVOID BREAKING THE API. PLEASE
     * USE THE LONGER discardCurrentSolution() instead.
     * 
     * @throws ContradictionException
     *             if a trivial inconsistency is detected.
     * @see #discardCurrentSolution()
     */
    @Deprecated
    void discard() throws ContradictionException;

    /**
     * Discard the current solution formula the optimization problem.
     * 
     * @throws ContradictionException
     *             if a trivial inconsistency is detected.
     * @since 2.1
     */
    void discardCurrentSolution() throws ContradictionException;

    /**
     * Allows to check afterwards if the solution provided by the solver is
     * optimal or not.
     * 
     * @return
     */
    boolean isOptimal();

    /**
     * Allow to set a specific timeout when the solver is formula optimization mode.
     * The solver internal timeout will be set to that value once it has found a
     * solution. That way, the original timeout of the solver may be reduced if
     * the solver finds quickly a solution, or increased if the solver finds
     * regularly new solutions (thus giving more time to the solver each time).
     * 
     * @since 2.3.3
     */
    void setTimeoutForFindingBetterSolution(int seconds);
}
