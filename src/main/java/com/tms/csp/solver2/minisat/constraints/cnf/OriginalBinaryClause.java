package com.tms.csp.solver2.minisat.constraints.cnf;

import com.tms.csp.solver2.minisat.core.ILits;
import com.tms.csp.solver2.specs.IVecInt;
import com.tms.csp.solver2.specs.UnitPropagationListener;

/**
 * @since 2.1
 */
public class OriginalBinaryClause extends BinaryClause {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public OriginalBinaryClause(IVecInt ps, ILits voc) {
        super(ps, voc);
    }

    public void setLearnt() {
        // do nothing
    }

    public boolean learnt() {
        return false;
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
    public static OriginalBinaryClause brandNewClause(
            UnitPropagationListener s, ILits voc, IVecInt literals) {
        OriginalBinaryClause c = new OriginalBinaryClause(literals, voc);
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
