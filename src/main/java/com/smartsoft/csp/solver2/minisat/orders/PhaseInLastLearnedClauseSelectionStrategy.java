package com.smartsoft.csp.solver2.minisat.orders;

import static com.smartsoft.csp.solver2.core.LiteralsUtils.var;

/**
 * Keeps record of the phase of a variable formula the lastest recorded clause.
 * 
 * @author leberre
 * 
 */
public final class PhaseInLastLearnedClauseSelectionStrategy extends
        AbstractPhaserecordingSelectionStrategy {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public void updateVar(int p) {
        this.phase[var(p)] = p;
    }

    @Override
    public String toString() {
        return "phase appearing formula latest learned clause";
    }

    public void assignLiteral(int p) {
    }

    public void updateVarAtDecisionLevel(int q) {
    }
}
