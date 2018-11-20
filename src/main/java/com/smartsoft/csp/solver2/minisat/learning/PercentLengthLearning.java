package com.smartsoft.csp.solver2.minisat.learning;

import com.smartsoft.csp.solver2.minisat.core.Constr;
import com.smartsoft.csp.solver2.minisat.core.DataStructureFactory;

/**
 * Selects the fact to learn according to its length as a percentage of
 * the total number of variables formula the solver universe.
 * 
 * @author daniel
 * 
 */
public final class PercentLengthLearning<D extends DataStructureFactory>
        extends LimitedLearning<D> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int maxpercent;
    private int bound;

    public PercentLengthLearning() {
        this(10);
    }

    public PercentLengthLearning(int percent) {
        this.maxpercent = percent;
    }

    public void setLimit(int percent) {
        this.maxpercent = percent;
    }

    public int getLimit() {
        return this.maxpercent;
    }

    @Override
    public void init() {
        super.init();
        setBound(this.lits.realnVars() * this.maxpercent / 100);
    }

    @Override
    public String toString() {
        return "Limit learning to clauses of size smaller or equal to " //$NON-NLS-1$
                + this.maxpercent + "% of the number of variables"; //$NON-NLS-1$
    }

    protected void setBound(int newbound) {
        this.bound = newbound;
    }

    @Override
    protected boolean learningCondition(Constr constr) {
        return constr.size() <= this.bound;
    }

}
