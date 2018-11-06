package com.tms.csp.solver2.minisat.core;

/**
 * @author leberre
 */
interface ConstrActivityListener {

    /**
     * @param outclause
     */
    void claBumpActivity(Constr confl);
}
