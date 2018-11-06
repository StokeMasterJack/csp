package com.tms.csp.solver2.minisat.learning;

import com.tms.csp.solver2.minisat.constraints.cnf.WLClause;
import com.tms.csp.solver2.minisat.core.Constr;
import com.tms.csp.solver2.minisat.core.DataStructureFactory;

/**
 * The solver only records among all the fact only the clauses.
 * 
 * @author daniel
 * 
 * @param <L>
 *            a data structure for the literals.
 * @param <D>
 *            a data structure for the clauses.
 */
public final class ClauseOnlyLearning<D extends DataStructureFactory> extends
        LimitedLearning<D> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    protected boolean learningCondition(Constr constr) {
        return constr instanceof WLClause;
    }

    @Override
    public String toString() {
        return "Limit learning to clauses using watched literals only";
    }
}
