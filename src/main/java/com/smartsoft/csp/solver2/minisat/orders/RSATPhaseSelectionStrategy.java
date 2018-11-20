package com.smartsoft.csp.solver2.minisat.orders;

import static com.smartsoft.csp.solver2.core.LiteralsUtils.var;

/**
 * Keeps track of the phase of the latest assignment.
 * 
 * @author leberre
 * 
 */
public final class RSATPhaseSelectionStrategy extends
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
        return "lightweight component caching from RSAT";
    }

    public void updateVar(int p) {
    }

    public void updateVarAtDecisionLevel(int p) {
    }
}
