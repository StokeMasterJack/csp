package com.smartsoft.csp.solver2.minisat.core;

import java.io.Serializable;

/**
 * Implementation of the strategy design pattern for allowing various learning
 * schemes.
 * 
 * @author leberre
 */
public interface LearningStrategy<D extends DataStructureFactory> extends
        Serializable {

    /**
     * hook method called just before the search begins. Useful to compute
     * metrics/parameters based c the input formula.
     * 
     */
    void init();

    void learns(Constr constr);

    void setVarActivityListener(VarActivityListener s);

    void setSolver(Solver<D> s);
}
