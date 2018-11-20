package com.smartsoft.csp.solver2.minisat.constraints;

import com.smartsoft.csp.solver2.minisat.constraints.card.AtLeast;
import com.smartsoft.csp.solver2.minisat.constraints.cnf.LearntWLClause;
import com.smartsoft.csp.solver2.minisat.core.Constr;
import com.smartsoft.csp.solver2.specs.ContradictionException;
import com.smartsoft.csp.solver2.specs.IVecInt;

/**
 * @author leberre To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Generation - Code and Comments
 */
public class CardinalityDataStructure extends AbstractCardinalityDataStructure {

    private static final long serialVersionUID = 1L;

    public Constr createUnregisteredClause(IVecInt literals) {
        return new LearntWLClause(literals, getVocabulary());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.sat4j.minisat.DataStructureFactory#createClause(org.sat4j.datatype
     * .VecInt)
     */
    public Constr createClause(IVecInt literals) throws ContradictionException {
        return AtLeast.atLeastNew(this.solver, getVocabulary(), literals, 1);
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
        return AtLeast.atLeastNew(this.solver, getVocabulary(), literals,
                degree);
    }

    public Constr createUnregisteredCardinalityConstraint(IVecInt literals,
            int degree) {
        return new AtLeast(getVocabulary(), literals, degree);
    }

}
