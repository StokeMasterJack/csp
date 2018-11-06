package com.tms.csp.solver2.minisat.orders;

import static com.tms.csp.solver2.core.LiteralsUtils.posLit;

import com.tms.csp.solver2.minisat.core.IPhaseSelectionStrategy;

public final class PositiveLiteralSelectionStrategy implements
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
        return posLit(var);
    }

    public void updateVar(int p) {
    }

    @Override
    public String toString() {
        return "positive phase selection";
    }

    public void updateVarAtDecisionLevel(int q) {
    }
}
