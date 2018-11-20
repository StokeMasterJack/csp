package com.smartsoft.csp.solver2.minisat.orders;

/**
 * @author leberre TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public final class PureOrder extends VarOrderHeap {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;

    private int period;

    private int cpt;

    public PureOrder() {
        this(20);
    }

    public PureOrder(int p) {
        setPeriod(p);
    }

    public void setPeriod(int p) {
        this.period = p;
        this.cpt = this.period;
    }

    public int getPeriod() {
        return this.period;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sat4j.minisat.core.VarOrder#select()
     */
    @Override
    public int select() {
        // wait period branching
        if (this.cpt < this.period) {
            this.cpt++;
        } else {
            // try to find a pure literal
            this.cpt = 0;
            int nblits = 2 * this.lits.nVars();
            for (int i = 2; i <= nblits; i++) {
                if (this.lits.isUnassigned(i)
                        && this.lits.watches(i).size() > 0
                        && this.lits.watches(i ^ 1).size() == 0) {
                    return i;
                }
            }
        }
        // not found: using normal order
        return super.select();
    }

    @Override
    public String toString() {
        return "tries to first branch c a single phase watched unassigned variable (pure literal if using a CB data structure) else VSIDS from MiniSAT"; //$NON-NLS-1$
    }
}
