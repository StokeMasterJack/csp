package com.smartsoft.csp.solver2.tools.xplain;

import java.io.Serializable;
import java.util.Map;

import com.smartsoft.csp.solver2.specs.ISolver;
import com.smartsoft.csp.solver2.specs.IVecInt;
import com.smartsoft.csp.solver2.specs.TimeoutException;

/**
 * Minimization technique used to reduce an unsatisfiable set of fact
 * into a minimally unsatisfiable subformula (MUS).
 * 
 * @author daniel
 * @since 2.1
 */
public interface MinimizationStrategy extends Serializable {

    /**
     * 
     * @param solver
     * @param constrs
     * @param assumps
     * @return
     * @throws TimeoutException
     * @since 2.1
     */
    IVecInt explain(ISolver solver, Map<Integer, ?> constrs, IVecInt assumps)
            throws TimeoutException;

    /**
     * @since 2.1
     */
    void cancelExplanationComputation();
}
