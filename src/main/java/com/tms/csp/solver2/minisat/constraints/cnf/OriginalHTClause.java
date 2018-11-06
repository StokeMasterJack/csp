package com.tms.csp.solver2.minisat.constraints.cnf;

import static com.tms.csp.solver2.core.LiteralsUtils.neg;

import com.tms.csp.solver2.minisat.core.ILits;
import com.tms.csp.solver2.specs.IVecInt;
import com.tms.csp.solver2.specs.UnitPropagationListener;

/**
 * @since 2.1
 */
public class OriginalHTClause extends HTClause {

    public OriginalHTClause(IVecInt ps, ILits voc) {
        super(ps, voc);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /*
     * (non-Javadoc)
     * 
     * @see org.sat4j.minisat.fact.cnf.WLClause#register()
     */
    public void register() {
        this.voc.watch(neg(this.head), this);
        this.voc.watch(neg(this.tail), this);
    }

    public boolean learnt() {
        return false;
    }

    public void setLearnt() {
        // do nothing
    }

    /**
     * Creates a brand new clause, presumably from external data.
     * 
     * @param s
     *            the object responsible for unit propagation
     * @param voc
     *            the vocabulary
     * @param literals
     *            the literals to store formula the clause
     * @return the created clause or null if the clause should be ignored
     *         (tautology for example)
     */
    public static OriginalHTClause brandNewClause(UnitPropagationListener s,
            ILits voc, IVecInt literals) {
        OriginalHTClause c = new OriginalHTClause(literals, voc);
        c.register();
        return c;
    }

    public void forwardActivity(double claInc) {
        this.activity += claInc;
    }

    /**
     * @param claInc
     */
    public void incActivity(double claInc) {

    }

    public void setActivity(double claInc) {
        // do nothing
    }
}
