package com.tms.csp.solver2.minisat.constraints;

import com.tms.csp.solver2.minisat.constraints.card.MaxWatchCard;
import com.tms.csp.solver2.minisat.constraints.card.MinWatchCard;
import com.tms.csp.solver2.minisat.constraints.cnf.LearntWLClause;
import com.tms.csp.solver2.minisat.core.Constr;
import com.tms.csp.solver2.specs.ContradictionException;
import com.tms.csp.solver2.specs.IVecInt;

/**
 * @author leberre To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Generation - Code and Comments
 */
public class CardinalityDataStructureYanMax extends
        AbstractCardinalityDataStructure {

    private static final long serialVersionUID = 1L;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.sat4j.minisat.DataStructureFactory#createClause(org.sat4j.datatype
     * .VecInt)
     */
    public Constr createClause(IVecInt literals) throws ContradictionException {
        return MaxWatchCard.maxWatchCardNew(this.solver, getVocabulary(),
                literals, MinWatchCard.ATLEAST, 1);
    }

    public Constr createUnregisteredClause(IVecInt literals) {
        return new LearntWLClause(literals, getVocabulary());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.sat4j.minisat.DataStructureFactory#createCardinalityConstraint(org
     * .sat4j.datatype.VecInt, int)
     */
    @Override
    public Constr createCardinalityConstraint(IVecInt literals, int degree)
            throws ContradictionException {
        return MaxWatchCard.maxWatchCardNew(this.solver, getVocabulary(),
                literals, MinWatchCard.ATLEAST, degree);
    }

    public Constr createUnregisteredCardinalityConstraint(IVecInt literals,
            int degree) {
        return new MaxWatchCard(getVocabulary(), literals,
                MinWatchCard.ATLEAST, degree);
    }

}
