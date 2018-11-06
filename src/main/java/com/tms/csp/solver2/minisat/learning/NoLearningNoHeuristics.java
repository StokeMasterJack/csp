package com.tms.csp.solver2.minisat.learning;

import com.tms.csp.solver2.minisat.core.Constr;
import com.tms.csp.solver2.minisat.core.DataStructureFactory;

/**
 * Allows MiniSAT to do backjumping without learning. The literals appearing formula
 * the reason do not see their activity increased. That solution looks the best
 * for VLIW-SAT-1.0 benchmarks (1346s vs 1785s).
 * 
 * @author leberre
 */
public final class NoLearningNoHeuristics<D extends DataStructureFactory>
        extends AbstractLearning<D> {

    private static final long serialVersionUID = 1L;

    public void learns(Constr reason) {
    }

}
