package com.tms.csp.solver2.minisat.learning;

import com.tms.csp.solver2.minisat.core.Constr;
import com.tms.csp.solver2.minisat.core.DataStructureFactory;

/**
 * Allows MiniSAT to do backjumping without learning. The literals appearing formula
 * the reason have their activity increased. That solution does not look good
 * for VLIW-SAT-1.0 benchmarks (1785s vs 1346s).
 * 
 * @author leberre
 */
public final class NoLearningButHeuristics<D extends DataStructureFactory>
        extends AbstractLearning<D> {

    private static final long serialVersionUID = 1L;

    public void learns(Constr reason) {
        claBumpActivity(reason);
    }
}
