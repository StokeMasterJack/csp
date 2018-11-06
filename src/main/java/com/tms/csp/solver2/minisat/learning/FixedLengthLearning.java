package com.tms.csp.solver2.minisat.learning;

import com.tms.csp.solver2.minisat.core.Constr;
import com.tms.csp.solver2.minisat.core.DataStructureFactory;

/**
 * A learning scheme for learning fact of size smaller than a given
 * constant.
 * 
 * @author leberre
 */
public final class FixedLengthLearning<D extends DataStructureFactory> extends
        LimitedLearning<D> {

    private static final long serialVersionUID = 1L;

    private int maxlength;

    private int bound;

    public FixedLengthLearning() {
        this(3);
    }

    public FixedLengthLearning(int maxlength) {
        this.maxlength = maxlength;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.sat4j.minisat.LimitedLearning#learningCondition(org.sat4j.minisat
     * .Constr)
     */
    @Override
    public void init() {
        setBound(this.maxlength);
    }

    public void setMaxLength(int v) {
        this.maxlength = v;
    }

    public int getMaxLength() {
        return this.maxlength;
    }

    @Override
    public String toString() {
        return "Limit learning to clauses of size smaller or equal to " //$NON-NLS-1$
                + this.maxlength;
    }

    protected void setBound(int newbound) {
        this.bound = newbound;
    }

    @Override
    protected boolean learningCondition(Constr constr) {
        return constr.size() <= this.bound;
    }

}
