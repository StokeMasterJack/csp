package com.tms.csp.solver2.minisat.learning;

import com.tms.csp.solver2.minisat.core.Constr;
import com.tms.csp.solver2.minisat.core.DataStructureFactory;
import com.tms.csp.solver2.minisat.core.IOrder;
import com.tms.csp.solver2.minisat.core.Solver;

/**
 * Learn clauses with a great number of active variables.
 * 
 * @author leberre
 */
public final class ActiveLearning<D extends DataStructureFactory> extends
        LimitedLearning<D> {

    private static final long serialVersionUID = 1L;

    private double percent;

    private IOrder order;

    private int maxpercent;

    public ActiveLearning() {
        this(0.95);
    }

    public ActiveLearning(double d) {
        this.percent = d;
    }

    public void setOrder(IOrder order) {
        this.order = order;
    }

    @Override
    public void setSolver(Solver<D> s) {
        super.setSolver(s);
        this.order = s.getOrder();
    }

    public void setActivityPercent(double d) {
        this.percent = d;
    }

    public double getActivityPercent() {
        return this.percent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.sat4j.minisat.LimitedLearning#learningCondition(org.sat4j.minisat
     * .Constr)
     */
    @Override
    protected boolean learningCondition(Constr clause) {
        int nbactivevars = 0;
        for (int i = 0; i < clause.size(); i++) {
            if (this.order.varActivity(clause.get(i)) > 1) {
                nbactivevars++;
            }
        }
        return nbactivevars > clause.size() * this.percent;
    }

    @Override
    public String toString() {
        return "Limit learning to clauses containing active literals (" + this.percent * 100 + "%)"; //$NON-NLS-1$
    }

    public void setLimit(int percent) {
        this.maxpercent = percent;
    }

    public int getLimit() {
        return this.maxpercent;
    }
}
