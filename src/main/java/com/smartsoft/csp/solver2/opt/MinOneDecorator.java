package com.smartsoft.csp.solver2.opt;

import com.smartsoft.csp.solver2.core.VecInt;
import com.smartsoft.csp.solver2.specs.ContradictionException;
import com.smartsoft.csp.solver2.specs.IConstr;
import com.smartsoft.csp.solver2.specs.IOptimizationProblem;
import com.smartsoft.csp.solver2.specs.ISolver;
import com.smartsoft.csp.solver2.specs.IVecInt;
import com.smartsoft.csp.solver2.specs.TimeoutException;
import com.smartsoft.csp.solver2.tools.SolverDecorator;

/**
 * Computes a solution with the smallest number of satisfied literals.
 * 
 * Please make sure that newVar(howmany) is called first to setup the decorator.
 * 
 * @author leberre
 */
public final class MinOneDecorator extends SolverDecorator<ISolver> implements
        IOptimizationProblem {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private int[] prevmodel;
    private int[] prevmodelWithInternalVariables;

    private boolean isSolutionOptimal;

    public MinOneDecorator(ISolver solver) {
        super(solver);
    }

    public boolean admitABetterSolution() throws TimeoutException {
        return admitABetterSolution(VecInt.EMPTY);
    }

    /**
     * @since 2.1
     */
    public boolean admitABetterSolution(IVecInt assumps)
            throws TimeoutException {
        this.isSolutionOptimal = false;
        boolean result = isSatisfiable(assumps, true);
        if (result) {
            this.prevmodel = super.model();
            this.prevmodelWithInternalVariables = super
                    .modelWithInternalVariables();
            calculateObjectiveValue();
        } else {
            this.isSolutionOptimal = true;
        }
        return result;
    }

    public boolean hasNoObjectiveFunction() {
        return false;
    }

    public boolean nonOptimalMeansSatisfiable() {
        return true;
    }

    private int counter;

    public Number calculateObjective() {
        calculateObjectiveValue();
        return this.counter;
    }

    private void calculateObjectiveValue() {
        this.counter = 0;
        for (int p : this.prevmodel) {
            if (p > 0) {
                this.counter++;
            }
        }
    }

    private final IVecInt literals = new VecInt();

    private IConstr previousConstr;

    /**
     * @since 2.1
     */
    public void discardCurrentSolution() throws ContradictionException {
        if (this.literals.isEmpty()) {
            for (int i = 1; i <= nVars(); i++) {
                this.literals.push(i);
            }
        }
        if (this.previousConstr != null) {
            super.removeConstr(this.previousConstr);
        }
        this.previousConstr = addAtMost(this.literals, this.counter - 1);
    }

    @Override
    public int[] model() {
        // DLB findbugs ok
        return this.prevmodel;
    }

    @Override
    public int[] modelWithInternalVariables() {
        return this.prevmodelWithInternalVariables;
    }

    @Override
    public void reset() {
        this.literals.clear();
        this.previousConstr = null;
        super.reset();
    }

    /**
     * @since 2.1
     */
    public Number getObjectiveValue() {
        return this.counter;
    }

    public void discard() throws ContradictionException {
        discardCurrentSolution();
    }

    /**
     * @since 2.1
     */
    public void forceObjectiveValueTo(Number forcedValue)
            throws ContradictionException {
        try {
            addAtMost(this.literals, forcedValue.intValue());
        } catch (ContradictionException ce) {
            this.isSolutionOptimal = true;
            throw ce;
        }

    }

    public boolean isOptimal() {
        return this.isSolutionOptimal;
    }

    public void setTimeoutForFindingBetterSolution(int seconds) {
        // TODO
        throw new UnsupportedOperationException("No implemented yet");
    }
}
