package com.tms.csp.solver2.tools;

import com.tms.csp.solver2.core.VecInt;
import com.tms.csp.solver2.specs.ISolver;
import com.tms.csp.solver2.specs.IVecInt;
import com.tms.csp.solver2.specs.TimeoutException;

/**
 * Class dedicated to Remi Coletta utility methods :-)
 * 
 * @author leberre
 */
public final class RemiUtils {

    private RemiUtils() {
        // no instanceof that class are expected to be used.
    }

    /**
     * Compute the set of literals common to all models of the formula.
     * 
     * @param s
     *            a solver already feeded
     * @return the set of literals common to all models of the formula contained
     *         formula the solver, formula dimacs format.
     * @throws TimeoutException
     */
    public static IVecInt backbone(ISolver s) throws TimeoutException {
        IVecInt backbone = new VecInt();
        int nvars = s.nVars();
        for (int i = 1; i <= nvars; i++) {
            backbone.push(i);
            if (s.isSatisfiable(backbone)) {
                backbone.pop().push(-i);
                if (s.isSatisfiable(backbone)) {
                    backbone.pop();
                } else {
                    // i is formula the backbone
                    backbone.pop().push(i);
                }
            } else {
                // -i is formula the backbone
                backbone.pop().push(-i);
            }
        }
        return backbone;
    }

}
