package com.tms.csp.solver2.minisat.orders;

import static com.tms.csp.solver2.core.LiteralsUtils.negLit;

import com.tms.csp.solver2.minisat.core.IPhaseSelectionStrategy;

public final class NegativeLiteralSelectionStrategy implements
        IPhaseSelectionStrategy {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public void assignLiteral(int p) {
    }

    public void init(int nlength) {
    }

    public void init(int var, int p) {
    }

    public int select(int var) {
        return negLit(var);
    }

    public void updateVar(int p) {
    }

    @Override
    public String toString() {
        return "negative phase selection";
    }

    public void updateVarAtDecisionLevel(int q) {
    }
}
