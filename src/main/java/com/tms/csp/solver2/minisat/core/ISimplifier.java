package com.tms.csp.solver2.minisat.core;

import java.io.Serializable;

import com.tms.csp.solver2.specs.IVecInt;

/**
 * Strategy for simplifying the conflict clause derived by the solver.
 * 
 * @author daniel
 * 
 */
public interface ISimplifier extends Serializable {
    void simplify(IVecInt outLearnt);
}