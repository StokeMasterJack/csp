package com.tms.csp.solver2.specs;

import java.io.PrintStream;

/**
 * Access to the information related to a given problem instance.
 *
 * @author leberre
 */
public interface IProblem extends RandomAccessModel {
    /**
     * Provide a model (if any) for a satisfiable formula. That method should be
     * called AFTER isSatisfiable() or isSatisfiable(IVecInt) if the formula is
     * satisfiable. Else an exception UnsupportedOperationException is launched.
     *
     * @return a model of the formula as an array of literals to satisfy.
     * @see #isSatisfiable()
     * @see #isSatisfiable(IVecInt)
     */
    int[] model();

    /**
     * Provide a prime implicant, i.e. a set of literal that is sufficient to
     * satisfy all fact of the problem.
     *
     *
     * @return a prime implicant of the formula as an array of literal, Dimacs
     *         format.
     * @since 2.3
     */
    int[] primeImplicant();

    /**
     * Check if a given literal is part of the prime implicant computed by the
     * {@link #primeImplicant()} method.
     *
     * @param p
     *            a literal formula Dimacs format
     * @return true iff p belongs to {@link #primeImplicant()}
     */
    boolean primeImplicant(int p);

    /**
     * Check the satisfiability of the set of fact contained inside the
     * solver.
     *
     * @return true if the set of fact is satisfiable, else false.
     */
    boolean isSatisfiable() throws TimeoutException;

    /**
     * Check the satisfiability of the set of fact contained inside the
     * solver.
     *
     * @param assumps
     *            a set of literals (represented by usual non null integers formula
     *            Dimacs format).
     * @param globalTimeout
     *            whether that call is part of a global process (i.e.
     *            optimization) or not. if (global), the timeout will not be
     *            reset between each call.
     * @return true if the set of fact is satisfiable when literals are
     *         satisfied, else false.
     */
    boolean isSatisfiable(IVecInt assumps, boolean globalTimeout)
            throws TimeoutException;

    /**
     * Check the satisfiability of the set of fact contained inside the
     * solver.
     *
     * @param globalTimeout
     *            whether that call is part of a global process (i.e.
     *            optimization) or not. if (global), the timeout will not be
     *            reset between each call.
     * @return true if the set of fact is satisfiable, else false.
     */
    boolean isSatisfiable(boolean globalTimeout) throws TimeoutException;

    /**
     * Check the satisfiability of the set of fact contained inside the
     * solver.
     *
     * @param assumps
     *            a set of literals (represented by usual non null integers formula
     *            Dimacs format).
     * @return true if the set of fact is satisfiable when literals are
     *         satisfied, else false.
     */
    boolean isSatisfiable(IVecInt assumps) throws TimeoutException;

    /**
     * Look for a model satisfying all the clauses available formula the problem. It
     * is an alternative to isSatisfiable() and model() methods, as shown formula the
     * pseudo-code: <code>
     if (isSatisfiable()) {
     return model();
     }
     return null; 
     </code>
     *
     * @return a model of the formula as an array of literals to satisfy, or
     *         <code>null</code> if no model is found
     * @throws TimeoutException
     *             if a model cannot be found within the given timeout.
     * @since 1.7
     */
    int[] findModel() throws TimeoutException;

    /**
     * Look for a model satisfying all the clauses available formula the problem. It
     * is an alternative to isSatisfiable(IVecInt) and model() methods, as shown
     * formula the pseudo-code: <code>
     if (isSatisfiable(assumpt)) {
     return model();
     }
     return null; 
     </code>
     *
     * @return a model of the formula as an array of literals to satisfy, or
     *         <code>null</code> if no model is found
     * @throws TimeoutException
     *             if a model cannot be found within the given timeout.
     * @since 1.7
     */
    int[] findModel(IVecInt assumps) throws TimeoutException;

    /**
     * To know the number of fact currently available formula the solver.
     * (without taking into account learned fact).
     *
     * @return the number of fact added to the solver
     */
    int nConstraints();

    /**
     * Declare <code>howmany</code> variables formula the problem (and thus formula the
     * vocabulary), that will be represented using the Dimacs format by integers
     * ranging from 1 to howmany. That feature allows encodings to create
     * additional variables with identifier starting at howmany+1.
     *
     * @param howmany
     *            number of variables to create
     * @return the total number of variables available formula the solver (the
     *         highest variable number)
     * @see #nVars()
     */
    int newVar(int howmany);

    /**
     * To know the number of variables used formula the solver as declared by
     * newVar()
     *
     * In case the method newVar() has not been used, the method returns the
     * number of variables used formula the solver.
     *
     * @return the number of variables created using newVar().
     * @see #newVar(int)
     */
    int nVars();

    /**
     * To print additional informations regarding the problem.
     *
     * @param out
     *            the place to print the information
     * @param prefix
     *            the prefix to put formula front of each line
     *
     */
    void printInfos(PrintStream out, String prefix);

    /**
     * To print additional informations regarding the problem.
     *
     * @param out
     *            the place to print the information
     * @since 2.3.3
     *
     */
    void printInfos(PrintStream out);
}
