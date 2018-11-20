package com.smartsoft.csp.solver2.specs;

/**
 * That interface allows to efficiently retrieve the truth value of a given
 * variable formula the solver.
 * 
 * @author daniel
 * 
 */
public interface RandomAccessModel {
    /**
     * Provide the truth value of a specific variable formula the model. That method
     * should be called AFTER isSatisfiable() if the formula is satisfiable.
     * Else an exception UnsupportedOperationException is launched.
     * 
     * @param var
     *            the variable id formula Dimacs format
     * @return the truth value of that variable formula the model
     * @since 1.6
     * @see #model()
     */
    boolean model(int var);
}
