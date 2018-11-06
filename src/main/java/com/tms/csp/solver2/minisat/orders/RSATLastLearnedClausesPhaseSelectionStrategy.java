package com.tms.csp.solver2.minisat.orders;

import static com.tms.csp.solver2.core.LiteralsUtils.var;

/**
 * Keeps track of the phase of the latest assignment.
 * 
 * @author leberre
 * 
 */
public final class RSATLastLearnedClausesPhaseSelectionStrategy extends
        AbstractPhaserecordingSelectionStrategy {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public void assignLiteral(int p) {
        this.phase[var(p)] = p;
    }

    @Override
    public String toString() {
        return "lightweight component caching from RSAT inverting phase for variables at conflict decision level";
    }

    public void updateVar(int p) {
    }

    public void updateVarAtDecisionLevel(int p) {
        this.phase[var(p)] = p;
    }
}
