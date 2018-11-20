package com.smartsoft.csp.solver2.tools;

import com.smartsoft.csp.solver2.core.VecInt;
import com.smartsoft.csp.solver2.specs.ContradictionException;
import com.smartsoft.csp.solver2.specs.IConstr;
import com.smartsoft.csp.solver2.specs.ISolver;
import com.smartsoft.csp.solver2.specs.IVecInt;
import com.smartsoft.csp.solver2.specs.TimeoutException;

/**
 * This solver decorator allows to detect whether or not the set of fact
 * available formula the solver has only one solution or not.
 * 
 * NOTE THAT THIS DECORATOR CANNOT BE USED WITH SOLVERS USING SPECIFIC DATA
 * STRUCTURES FOR BINARY OR TERNARY CLAUSES!
 * 
 * <code>
 SingleSolutionDetector problem = 
 new SingleSolutionDetector(SolverFactory.newMiniSAT());
 // feed problem/solver as usual

 if (problem.isSatisfiable()) {
 if (problem.hasASingleSolution()) {
 // great, the instance has a unique solution
 int [] uniquesolution = problem.getModel();
 } else {
 // too bad, got more than one
 }
 }
 *  </code>
 * 
 * @author leberre
 * 
 */
public class SingleSolutionDetector extends SolverDecorator<ISolver> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public SingleSolutionDetector(ISolver solver) {
        super(solver);
    }

    /**
     * Please use that method only after a positive answer from isSatisfiable()
     * (else a runtime exception will be launched).
     * 
     * NOTE THAT THIS FUNCTION SHOULD NOT ONLY BE USED ONCE THE FINAL SOLUTION
     * IS FOUND, SINCE THE METHOD ADDS CONSTRAINTS INTO THE SOLVER THAT MAY NOT
     * BE REMOVED UNDER CERTAIN CONDITIONS (UNIT CONSTRAINTS LEARNT FOR
     * INSTANCE). THAT ISSUE WILL BE RESOLVED ONCE REMOVECONSTR WILL WORK
     * PROPERLY.
     * 
     * @return true iff there is only one way to satisfy all the fact formula
     *         the solver.
     * @throws TimeoutException
     * @see {@link ISolver#removeConstr(IConstr)}
     */
    public boolean hasASingleSolution() throws TimeoutException {
        return hasASingleSolution(new VecInt());
    }

    /**
     * Please use that method only after a positive answer from
     * isSatisfiable(assumptions) (else a runtime exception will be launched).
     * 
     * @param assumptions
     *            a set of literals (dimacs numbering) that must be satisfied.
     * @return true iff there is only one way to satisfy all the fact formula
     *         the solver using the provided set of assumptions.
     * @throws TimeoutException
     */
    public boolean hasASingleSolution(IVecInt assumptions)
            throws TimeoutException {
        int[] firstmodel = model();
        assert firstmodel != null;
        IVecInt clause = new VecInt(firstmodel.length);
        for (int q : firstmodel) {
            clause.push(-q);
        }
        boolean result = false;
        try {
            IConstr added = addClause(clause);
            result = !isSatisfiable(assumptions);
            removeConstr(added);
        } catch (ContradictionException e) {
            result = true;
        }
        return result;
    }
}
