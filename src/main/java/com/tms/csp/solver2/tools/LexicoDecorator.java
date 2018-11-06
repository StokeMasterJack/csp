package com.tms.csp.solver2.tools;

import java.util.ArrayList;
import java.util.List;

import com.tms.csp.solver2.core.VecInt;
import com.tms.csp.solver2.specs.ContradictionException;
import com.tms.csp.solver2.specs.IConstr;
import com.tms.csp.solver2.specs.IOptimizationProblem;
import com.tms.csp.solver2.specs.ISolver;
import com.tms.csp.solver2.specs.IVecInt;
import com.tms.csp.solver2.specs.IteratorInt;
import com.tms.csp.solver2.specs.TimeoutException;

public class LexicoDecorator<T extends ISolver> extends SolverDecorator<T>
        implements IOptimizationProblem {

    protected final List<IVecInt> criteria = new ArrayList<IVecInt>();

    protected int currentCriterion = 0;

    protected IConstr prevConstr;

    private Number currentValue = -1;

    protected int[] prevfullmodel;
    protected int[] prevmodelwithinternalvars;
    protected boolean[] prevboolmodel;

    protected boolean isSolutionOptimal;

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public LexicoDecorator(T solver) {
        super(solver);
    }

    public void addCriterion(IVecInt literals) {
        IVecInt copy = new VecInt(literals.size());
        literals.copyTo(copy);
        this.criteria.add(copy);
    }

    public boolean admitABetterSolution() throws TimeoutException {
        return admitABetterSolution(VecInt.EMPTY);
    }

    public boolean admitABetterSolution(IVecInt assumps)
            throws TimeoutException {
        this.isSolutionOptimal = false;
        if (decorated().isSatisfiable(assumps, true)) {
            this.prevboolmodel = new boolean[nVars()];
            for (int i = 0; i < nVars(); i++) {
                this.prevboolmodel[i] = decorated().model(i + 1);
            }
            this.prevfullmodel = decorated().model();
            this.prevmodelwithinternalvars = decorated()
                    .modelWithInternalVariables();
            calculateObjective();
            return true;
        }
        return manageUnsatCase();
    }

    protected boolean manageUnsatCase() {
        if (this.prevfullmodel == null) {
            // the problem is UNSAT
            return false;
        }
        // an optimal solution has been found
        // for one criteria
        if (this.currentCriterion < numberOfCriteria() - 1) {
            if (this.prevConstr != null) {
                super.removeConstr(this.prevConstr);
                this.prevConstr = null;
            }
            try {
                fixCriterionValue();
            } catch (ContradictionException e) {
                throw new IllegalStateException(e);
            }
            if (isVerbose()) {
                System.out.println(getLogPrefix()
                        + "Found optimal criterion number "
                        + (this.currentCriterion + 1));
            }
            this.currentCriterion++;
            calculateObjective();
            return true;
        }
        if (isVerbose()) {
            System.out.println(getLogPrefix()
                    + "Found optimal solution for the last criterion ");
        }
        this.isSolutionOptimal = true;
        if (this.prevConstr != null) {
            super.removeConstr(this.prevConstr);
            this.prevConstr = null;
        }
        return false;
    }

    public int numberOfCriteria() {
        return this.criteria.size();
    }

    protected void fixCriterionValue() throws ContradictionException {
        super.addExactly(this.criteria.get(this.currentCriterion),
                this.currentValue.intValue());
    }

    @Override
    public int[] model() {
        return this.prevfullmodel;
    }

    @Override
    public boolean model(int var) {
        return this.prevboolmodel[var - 1];
    }

    @Override
    public int[] modelWithInternalVariables() {
        return this.prevmodelwithinternalvars;
    }

    public boolean hasNoObjectiveFunction() {
        return false;
    }

    public boolean nonOptimalMeansSatisfiable() {
        return true;
    }

    public Number calculateObjective() {
        this.currentValue = evaluate();
        return this.currentValue;
    }

    public Number getObjectiveValue() {
        return this.currentValue;
    }

    public Number getObjectiveValue(int criterion) {
        return evaluate(criterion);
    }

    public void forceObjectiveValueTo(Number forcedValue)
            throws ContradictionException {
        throw new UnsupportedOperationException();
    }

    public void discard() throws ContradictionException {
        discardCurrentSolution();

    }

    public void discardCurrentSolution() throws ContradictionException {
        if (this.prevConstr != null) {
            super.removeSubsumedConstr(this.prevConstr);
        }
        try {
            this.prevConstr = discardSolutionsForOptimizing();
        } catch (ContradictionException c) {
            this.prevConstr = null;
            if (!manageUnsatCase()) {
                throw c;
            }
        }

    }

    protected IConstr discardSolutionsForOptimizing()
            throws ContradictionException {
        return super.addAtMost(this.criteria.get(this.currentCriterion),
                this.currentValue.intValue() - 1);
    }

    protected Number evaluate() {
        return evaluate(this.currentCriterion);
    }

    protected Number evaluate(int criterion) {
        int value = 0;
        int lit;
        for (IteratorInt it = this.criteria.get(this.currentCriterion)
                .iterator(); it.hasNext();) {
            lit = it.next();
            if (lit > 0 && this.prevboolmodel[lit - 1] || lit < 0
                    && !this.prevboolmodel[-lit - 1]) {
                value++;
            }
        }
        return value;
    }

    public boolean isOptimal() {
        return this.isSolutionOptimal;
    }

    public void setTimeoutForFindingBetterSolution(int seconds) {
        throw new UnsupportedOperationException("No implemented yet");
    }

}
