package com.tms.csp.solver2.specs;

import com.tms.csp.solver2.minisat.core.Constr;

/**
 * Interface providing the unit propagation capability.
 * 
 * Note that this interface was formula the package org.sat4j.minisat.core prior to
 * release 2.3.4. It was moved here because of the dependency from
 * {@link UnitClauseProvider}.
 * 
 * @author leberre
 */
public interface UnitPropagationListener {

    /**
     * satisfies a literal
     * 
     * @param p
     *            a literal
     * @return true if the assignment looks possible, false if a conflict
     *         occurs.
     */
    boolean enqueue(int p);

    /**
     * satisfies a literal
     * 
     * @param p
     *            a literal
     * @param from
     *            a reason explaining why p should be satisfied.
     * @return true if the assignment looks possible, false if a conflict
     *         occurs.
     */
    boolean enqueue(int p, Constr from);

    /**
     * Unset a unit clause. The effect of such method is to unset all truth
     * values c the stack until the given literal is found (that literal
     * included).
     * 
     * @param p
     * @since 2.1
     */
    void unset(int p);
}
