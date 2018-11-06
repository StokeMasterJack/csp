package com.tms.csp.solver2.minisat.constraints;

import com.tms.csp.solver2.minisat.constraints.cnf.Clauses;
import com.tms.csp.solver2.minisat.constraints.cnf.LearntWLClause;
import com.tms.csp.solver2.minisat.constraints.cnf.Lits;
import com.tms.csp.solver2.minisat.constraints.cnf.OriginalBinaryClause;
import com.tms.csp.solver2.minisat.constraints.cnf.OriginalWLClause;
import com.tms.csp.solver2.minisat.constraints.cnf.UnitClause;
import com.tms.csp.solver2.minisat.core.Constr;
import com.tms.csp.solver2.minisat.core.ILits;
import com.tms.csp.solver2.specs.ContradictionException;
import com.tms.csp.solver2.specs.IVecInt;

/**
 * @author leberre To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Generation - Code and Comments
 */
public class ClausalDataStructureWL extends AbstractDataStructureFactory {

    private static final long serialVersionUID = 1L;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.sat4j.minisat.DataStructureFactory#createClause(org.sat4j.datatype
     * .VecInt)
     */
    public Constr createClause(IVecInt literals) throws ContradictionException {
        IVecInt v = Clauses.sanityCheck(literals, getVocabulary(), this.solver);
        if (v == null) {
            // tautological clause
            return null;
        }
        if (v.size() == 1) {
            return new UnitClause(v.last());
        }
        if (v.size() == 2) {
            return OriginalBinaryClause.brandNewClause(this.solver,
                    getVocabulary(), v);
        }
        return OriginalWLClause.brandNewClause(this.solver, getVocabulary(), v);
    }

    public Constr createUnregisteredClause(IVecInt literals) {
        return new LearntWLClause(literals, getVocabulary());
    }

    @Override
    protected ILits createLits() {
        return new Lits();
    }
}
