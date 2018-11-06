package com.tms.csp.solver2.tools;

import com.tms.csp.solver2.core.VecInt;
import com.tms.csp.solver2.specs.ContradictionException;
import com.tms.csp.solver2.specs.IOptimizationProblem;
import com.tms.csp.solver2.specs.ISolver;
import com.tms.csp.solver2.specs.IVecInt;
import com.tms.csp.solver2.specs.TimeoutException;

public class OptToSatAdapter extends SolverDecorator<ISolver> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    IOptimizationProblem problem;

    boolean optimalValueForced = false;
    private final IVecInt assumps = new VecInt();

    private long begin;

    private final SolutionFoundListener sfl;

    public OptToSatAdapter(IOptimizationProblem problem) {
        this(problem, SolutionFoundListener.VOID);
    }

    public OptToSatAdapter(IOptimizationProblem problem,
            SolutionFoundListener sfl) {
        super((ISolver) problem);
        this.problem = problem;
        this.sfl = sfl;
    }

    @Override
    public void reset() {
        super.reset();
        this.optimalValueForced = false;
    }

    @Override
    public boolean isSatisfiable() throws TimeoutException {
        return isSatisfiable(VecInt.EMPTY);
    }

    @Override
    public boolean isSatisfiable(boolean global) throws TimeoutException {
        return isSatisfiable();
    }

    @Override
    public boolean isSatisfiable(IVecInt myAssumps, boolean global)
            throws TimeoutException {
        return isSatisfiable(myAssumps);
    }

    @Override
    public boolean isSatisfiable(IVecInt myAssumps) throws TimeoutException {
        this.assumps.clear();
        myAssumps.copyTo(this.assumps);
        this.begin = System.currentTimeMillis();
        if (this.problem.hasNoObjectiveFunction()) {
            return this.problem.isSatisfiable(myAssumps);
        }
        boolean satisfiable = false;
        try {
            while (this.problem.admitABetterSolution(myAssumps)) {
                satisfiable = true;
                sfl.onSolutionFound(this.problem.model());
                this.problem.discardCurrentSolution();
                if (isVerbose()) {
                    System.out.println(getLogPrefix()
                            + "Current objective function value: "
                            + this.problem.getObjectiveValue() + "("
                            + (System.currentTimeMillis() - this.begin)
                            / 1000.0 + "s)");
                }
            }
            sfl.onUnsatTermination();
        } catch (TimeoutException e) {
            if (isVerbose()) {
                System.out.println(getLogPrefix() + "Solver timed out after "
                        + (System.currentTimeMillis() - this.begin) / 1000.0
                        + "s)");
            }
            if (!satisfiable) {
                throw e;
            }
        } catch (ContradictionException ce) {
            sfl.onUnsatTermination();
        }
        return satisfiable;
    }

    @Override
    public int[] model() {
        return this.problem.model();
    }

    @Override
    public boolean model(int var) {
        return this.problem.model(var);
    }

    @Override
    public int[] modelWithInternalVariables() {
        return decorated().modelWithInternalVariables();
    }

    @Override
    public int[] findModel() throws TimeoutException {
        if (isSatisfiable()) {
            return model();
        }
        return null;
    }

    @Override
    public int[] findModel(IVecInt assumps) throws TimeoutException {
        if (isSatisfiable(assumps)) {
            return model();
        }
        return null;
    }

    @Override
    public String toString(String prefix) {
        return prefix + "Optimization to SAT adapter\n"
                + super.toString(prefix);
    }

    /**
     * Allow to easily check is the solution returned by isSatisfiable is
     * optimal or not.
     * 
     * @return true is the solution found is indeed optimal.
     */
    public boolean isOptimal() {
        return this.problem.isOptimal();
    }
}
