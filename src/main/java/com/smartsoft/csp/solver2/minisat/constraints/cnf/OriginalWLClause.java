package com.smartsoft.csp.solver2.minisat.constraints.cnf;

import com.smartsoft.csp.solver2.minisat.core.ILits;
import com.smartsoft.csp.solver2.specs.IVecInt;
import com.smartsoft.csp.solver2.specs.UnitPropagationListener;

public final class OriginalWLClause extends WLClause {

    public OriginalWLClause(IVecInt ps, ILits voc) {
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
        assert this.lits.length > 1;
        this.voc.watch(this.lits[0] ^ 1, this);
        this.voc.watch(this.lits[1] ^ 1, this);
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
    public static OriginalWLClause brandNewClause(UnitPropagationListener s,
            ILits voc, IVecInt literals) {
        OriginalWLClause c = new OriginalWLClause(literals, voc);
        c.register();
        return c;
    }

    /**
     * @since 2.1
     */
    public void forwardActivity(double claInc) {
        this.activity += claInc;
    }

    /**
     * @param claInc
     */
    public void incActivity(double claInc) {

    }

}
