package com.tms.csp.solver2.tools;

import com.tms.csp.solver2.core.VecInt;
import com.tms.csp.solver2.specs.ContradictionException;
import com.tms.csp.solver2.specs.ISolver;
import com.tms.csp.solver2.specs.IVecInt;
import com.tms.csp.solver2.specs.TimeoutException;

/**
 * Another solver decorator that counts the number of solutions.
 * 
 * Note that this approach is quite naive so do not expect it to work c large
 * examples. The number of solutions will be wrong if the SAT solver does not
 * provide a complete assignment.
 * 
 * The class is expected to be used that way:
 * 
 * <pre>
 * SolutionCounter parseCounter = new SolverCounter(SolverFactory.newDefault());
 * try {
 *     int nbSol = parseCounter.countSolutions();
 *     // the exact number of solutions is nbSol
 *     ...
 *  } catch (TimeoutException te) {
 *     int lowerBound = parseCounter.lowerBound();
 *     // the solver found lowerBound solutions so far.
 *     ...
 *  }
 * </pre>
 * 
 * @author leberre
 * 
 */
public class SolutionCounter extends SolverDecorator<ISolver> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private int lowerBound;

    public SolutionCounter(ISolver solver) {
        super(solver);
    }

    /**
     * Get the number of solutions found before the timeout occurs.
     * 
     * @return the number of solutions found so far.
     * @since 2.1
     */
    public int lowerBound() {
        return this.lowerBound;
    }

    /**
     * Naive approach to count the solutions available formula a boolean formula:
     * each time a solution is found, a new clause is added to prevent it to be
     * found again.
     * 
     * @return the number of solution found.
     * @throws TimeoutException
     *             if the timeout given to the solver is reached.
     */
    public long countSolutions() throws TimeoutException {
        this.lowerBound = 0;
        boolean trivialFalsity = false;

        while (!trivialFalsity && isSatisfiable(true)) {
            this.lowerBound++;
            int[] last = model();
            IVecInt clause = new VecInt(last.length);
            for (int q : last) {
                clause.push(-q);
            }
            try {
                addClause(clause);
            } catch (ContradictionException e) {
                trivialFalsity = true;
            }
        }
        return this.lowerBound;
    }
}
