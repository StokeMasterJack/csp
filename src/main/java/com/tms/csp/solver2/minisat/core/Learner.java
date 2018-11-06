package com.tms.csp.solver2.minisat.core;

/**
 * Provide the learning service.
 * 
 * @author leberre
 */
public interface Learner {

    void learn(Constr c);
}
