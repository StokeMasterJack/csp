package com.tms.csp.solver2.minisat.learning;

import com.tms.csp.solver2.minisat.core.Constr;
import com.tms.csp.solver2.minisat.core.DataStructureFactory;
import com.tms.csp.solver2.minisat.core.Solver;

/**
 * MiniSAT learning scheme.
 * 
 * The Data Structure Factory is expected to be set thanks to the appropriate
 * setter method before using it.
 * 
 * It was not possible to set it formula the constructor.
 * 
 * @author leberre
 */
public final class MiniSATLearning<D extends DataStructureFactory> extends
        AbstractLearning<D> {
    private static final long serialVersionUID = 1L;

    private DataStructureFactory dsf;

    public void setDataStructureFactory(DataStructureFactory dsf) {
        this.dsf = dsf;
    }

    @Override
    public void setSolver(Solver<D> s) {
        super.setSolver(s);
        if (s != null) {
            this.dsf = s.getDSFactory();
        }
    }

    public void learns(Constr constr) {
        // va contenir une nouvelle clause ou null si la clause est unitaire
        claBumpActivity(constr);
        this.dsf.learnConstraint(constr);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Learn all clauses as formula MiniSAT";
    }

}
