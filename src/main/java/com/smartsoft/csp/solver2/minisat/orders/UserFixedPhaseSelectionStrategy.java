package com.smartsoft.csp.solver2.minisat.orders;

/**
 * Selection strategy where the phase selection is decided at init time and is
 * not updated during the search.
 * 
 * @author leberre
 * 
 */
public final class UserFixedPhaseSelectionStrategy extends
        AbstractPhaserecordingSelectionStrategy {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public void assignLiteral(int p) {
    }

    public void updateVar(int p) {
    }

    @Override
    public String toString() {
        return "Fixed selection strategy.";
    }

    public void updateVarAtDecisionLevel(int q) {
    }
}
