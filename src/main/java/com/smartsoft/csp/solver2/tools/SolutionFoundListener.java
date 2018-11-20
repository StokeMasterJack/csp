package com.smartsoft.csp.solver2.tools;

import com.smartsoft.csp.solver2.specs.IVecInt;

/**
 * Allows the end user to react when a new solution is found. This is typically
 * the case when doing some upper bound optimization, or iterating c the
 * models, or computing all MUses.
 * 
 * @author leberre
 * @since 2.3.3
 * 
 */
public interface SolutionFoundListener {

    SolutionFoundListener VOID = new SolutionFoundListener() {

        public void onSolutionFound(int[] model) {
            // do nothing
        }

        public void onSolutionFound(IVecInt solution) {
            // do nothing
        }

        public void onUnsatTermination() {
            // do nothing
        }
    };

    /**
     * Callback method called when a new solution is found. While a solution
     * will often be a model, it might also be the case that the solution is
     * something else (MUS, group MUS, etc).
     * 
     * @param solution
     *            a set of Dimacs literals.
     */
    void onSolutionFound(int[] solution);

    /**
     * Callback method called when a new solution is found. While a solution
     * will often be a model, it might also be the case that the solution is
     * something else (MUS, group MUS, etc).
     * 
     * @param solution
     *            a set of Dimacs literals.
     */
    void onSolutionFound(IVecInt solution);

    /**
     * Callback method called when the search is finished (either unsat problem
     * or no more solutions found)
     */
    void onUnsatTermination();
}
