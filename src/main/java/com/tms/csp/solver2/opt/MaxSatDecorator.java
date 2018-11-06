package com.tms.csp.solver2.opt;

import com.tms.csp.solver2.core.ConstrGroup;
import com.tms.csp.solver2.core.VecInt;
import com.tms.csp.solver2.specs.ContradictionException;
import com.tms.csp.solver2.specs.IConstr;
import com.tms.csp.solver2.specs.ISolver;
import com.tms.csp.solver2.specs.IVecInt;
import com.tms.csp.solver2.specs.TimeoutException;

/**
 * Computes a solution that satisfies the maximum of clauses.
 * 
 * @author daniel
 * 
 */
public final class MaxSatDecorator extends AbstractSelectorVariablesDecorator {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final boolean equivalence;

    public MaxSatDecorator(ISolver solver) {
        this(solver, false);
    }

    public MaxSatDecorator(ISolver solver, boolean equivalence) {
        super(solver);
        this.equivalence = equivalence;
    }

    @Override
    public void setExpectedNumberOfClauses(int nb) {
        super.setExpectedNumberOfClauses(nb);
        this.lits.ensure(nb);
    }

    @Override
    public IConstr addClause(IVecInt literals) throws ContradictionException {
        int newvar = nextFreeVarId(true);
        this.lits.push(newvar);
        literals.push(newvar);
        if (this.equivalence) {
            ConstrGroup constrs = new ConstrGroup();
            constrs.add(super.addClause(literals));
            IVecInt clause = new VecInt(2);
            clause.push(-newvar);
            for (int i = 0; i < literals.size() - 1; i++) {
                clause.push(-literals.get(i));
                constrs.add(super.addClause(clause));
            }
            clause.pop();
            return constrs;
        }
        return super.addClause(literals);
    }

    @Override
    public void reset() {
        this.lits.clear();
        super.reset();
        this.prevConstr = null;
    }

    public boolean hasNoObjectiveFunction() {
        return false;
    }

    public boolean nonOptimalMeansSatisfiable() {
        return false;
    }

    public Number calculateObjective() {
        calculateObjectiveValue();
        return this.counter;
    }

    private final IVecInt lits = new VecInt();

    private int counter;

    private IConstr prevConstr;

    /**
     * @since 2.1
     */
    public void discardCurrentSolution() throws ContradictionException {
        if (this.prevConstr != null) {
            super.removeSubsumedConstr(this.prevConstr);
        }
        try {
            this.prevConstr = super.addAtMost(this.lits, this.counter - 1);
        } catch (ContradictionException ce) {
            setSolutionOptimal(true);
            throw ce;
        }
    }

    @Override
    public boolean admitABetterSolution(IVecInt assumps)
            throws TimeoutException {

        boolean result = super.admitABetterSolution(assumps);
        if (!result && this.prevConstr != null) {
            super.removeConstr(this.prevConstr);
            this.prevConstr = null;
        }
        return result;
    }

    public void discard() throws ContradictionException {
        discardCurrentSolution();
    }

    /**
     * @since 2.1
     */
    public Number getObjectiveValue() {
        return this.counter;
    }

    @Override
    void calculateObjectiveValue() {
        this.counter = 0;
        for (int q : getPrevfullmodel()) {
            if (q > nVars()) {
                this.counter++;
            }
        }
    }

    /**
     * @since 2.1
     */
    public void forceObjectiveValueTo(Number forcedValue)
            throws ContradictionException {
        super.addAtMost(this.lits, forcedValue.intValue());
    }

    public void setTimeoutForFindingBetterSolution(int seconds) {
        // TODO
        throw new UnsupportedOperationException("No implemented yet");
    }
}
