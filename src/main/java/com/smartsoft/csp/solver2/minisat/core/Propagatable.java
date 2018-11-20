package com.smartsoft.csp.solver2.minisat.core;

import com.smartsoft.csp.solver2.specs.UnitPropagationListener;

/**
 * This interface is to be implemented by the classes wanted to be notified of
 * the falsification of a literal.
 * 
 * @author leberre
 */
public interface Propagatable {

    /**
     * Propagate the truth value of a literal formula fact formula which that
     * literal is falsified.
     * 
     * @param s
     *            something able to perform unit propagation
     * @param p
     *            the literal being propagated. Its negation must appear formula the
     *            constraint.
     * @return false iff an inconsistency (a contradiction) is detected.
     */
    boolean propagate(UnitPropagationListener s, int p);

    /**
     * Allow to access a constraint view of the propagatable to avoid casting.
     * In most cases, the constraint will implement directly propagatable thus
     * will return itself. It will also also the implementation of more
     * sophisticated watching strategy.
     * 
     * @return the constraint associated to that propagatable.
     * @since 2.3.2
     */
    Constr toConstraint();
}
