package com.smartsoft.csp.solver2.minisat.orders;

import static com.smartsoft.csp.solver2.core.LiteralsUtils.negLit;

import com.smartsoft.csp.solver2.minisat.core.IPhaseSelectionStrategy;

abstract class AbstractPhaserecordingSelectionStrategy implements
        IPhaseSelectionStrategy {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    protected int[] phase;

    public void init(int nlength) {
        if (this.phase == null || this.phase.length < nlength) {
            this.phase = new int[nlength];
        }
        for (int i = 1; i < nlength; i++) {
            this.phase[i] = negLit(i);
        }
    }

    public void init(int var, int p) {
        this.phase[var] = p;
    }

    public int select(int var) {
        return this.phase[var];
    }
}
