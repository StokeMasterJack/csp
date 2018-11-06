package com.tms.csp.solver2.minisat.orders;

import com.tms.csp.solver2.minisat.core.Heap;

public class SubsetVarOrder extends VarOrderHeap {

    private final int[] varsToTest;

    public SubsetVarOrder(int[] varsToTest) {
        this.varsToTest = new int[varsToTest.length];
        System.arraycopy(varsToTest, 0, this.varsToTest, 0, varsToTest.length);
    }

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    @Override
    public void init() {
        int nlength = this.lits.nVars() + 1;
        if (this.activity == null || this.activity.length < nlength) {
            this.activity = new double[nlength];
        }
        this.phaseStrategy.init(nlength);
        this.activity[0] = -1;
        this.heap = new Heap(this.activity);
        this.heap.setBounds(nlength);
        for (int var : this.varsToTest) {
            assert var > 0;
            assert var <= this.lits.nVars() : "" + this.lits.nVars() + "/" + var; //$NON-NLS-1$ //$NON-NLS-2$
            this.activity[var] = 0.0;
            if (this.lits.belongsToPool(var)) {
                this.heap.insert(var);
            }
        }
    }
}
