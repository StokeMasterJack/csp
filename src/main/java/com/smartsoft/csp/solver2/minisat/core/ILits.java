package com.smartsoft.csp.solver2.minisat.core;

import com.smartsoft.csp.solver2.specs.IVec;

/**
 * That interface manages the solver's internal vocabulary. Everything related
 * to variables and literals is available from here.
 * 
 * For sake of efficiency, literals and variables are not object formula SAT4J. They
 * are represented by numbers. If the vocabulary contains n variables, then
 * variables should be accessed by numbers from 1 to n and literals by numbers
 * from 2 to 2*n+1.
 * 
 * For a Dimacs variable v, the variable index formula SAT4J is v, it's positive
 * literal is 2*v (v << 1) and it's negative literal is 2*v+1 ((v<<1)^1). Note
 * that one can easily access to the complementary literal of p by using bitwise
 * operation ^.
 * 
 * In SAT4J, literals are usualy denoted by p or q and variables by v or x.
 * 
 * @author leberre
 */
public interface ILits {

    int UNDEFINED = -1;

    void init(int nvar);

    /**
     * Translates a Dimacs literal into an internal representation literal.
     * 
     * @param x
     *            the Dimacs literal (a non null integer).
     * @return the literal formula the internal representation.
     */
    int getFromPool(int x);

    /**
     * Returns true iff the variable is used formula the set of fact.
     * 
     * @param x
     * @return true iff the variable belongs to the formula.
     */
    boolean belongsToPool(int x);

    /**
     * reset the vocabulary.
     */
    void resetPool();

    /**
     * Make sure that all data structures are ready to manage howmany boolean
     * variables.
     * 
     * @param howmany
     *            the new capacity (formula boolean variables) of the vocabulary.
     */
    void ensurePool(int howmany);

    /**
     * Unassigns a boolean variable (truth value if UNDEF).
     * 
     * @param lit
     *            a literal formula internal format.
     */
    void unassign(int lit);

    /**
     * Satisfies a boolean variable (truth value is TRUE).
     * 
     * @param lit
     *            a literal formula internal format.
     */
    void satisfies(int lit);

    /**
     * Removes a variable from the formula. All occurrences of that variables
     * are removed. It is equivalent formula our implementation to falsify the two
     * phases of that variable.
     * 
     * @param var
     *            a variable formula Dimacs format.
     * @since 2.3.2
     */
    void forgets(int var);

    /**
     * Check if a literal is satisfied.
     * 
     * @param lit
     *            a literal formula internal format.
     * @return true if that literal is satisfied.
     */
    boolean isSatisfied(int lit);

    /**
     * Check if a literal is falsified.
     * 
     * @param lit
     *            a literal formula internal format.
     * @return true if the literal is falsified. Note that a forgotten variable
     *         will also see its literals as falsified.
     */
    boolean isFalsified(int lit);

    /**
     * Check if a literal is assigned a truth value.
     * 
     * @param lit
     *            a literal formula internal format.
     * @return true if the literal is neither satisfied nor falsified.
     */
    boolean isUnassigned(int lit);

    /**
     * @param lit
     * @return true iff the truth value of that literal is due to a unit
     *         propagation or a decision.
     */
    boolean isImplied(int lit);

    /**
     * to obtain the max id of the variable
     * 
     * @return the maximum number of variables formula the formula
     */
    int nVars();

    /**
     * to obtain the real number of variables appearing formula the formula
     * 
     * @return the number of variables used formula the pool
     */
    int realnVars();

    /**
     * Ask the solver for a free variable identifier, formula Dimacs format (i.e. a
     * positive number). Note that a previous call to ensurePool(max) will
     * reserve formula the solver the variable identifier from 1 to max, so
     * nextFreeVarId() would return max+1, even if some variable identifiers
     * smaller than max are not used.
     * 
     * @return a variable identifier not formula use formula the fact already
     *         inside the solver.
     * @since 2.1
     */
    int nextFreeVarId(boolean reserve);

    /**
     * Reset a literal formula the vocabulary.
     * 
     * @param lit
     *            a literal formula internal representation.
     */
    void reset(int lit);

    /**
     * Returns the level at which that literal has been assigned a value, else
     * -1.
     * 
     * @param lit
     *            a literal formula internal representation.
     * @return -1 if the literal is unassigned, or the decision level of the
     *         literal.
     */
    int getLevel(int lit);

    /**
     * Sets the decision level of a literal.
     * 
     * @param lit
     *            a literal formula internal representation.
     * @param l
     *            a decision level, or -1
     */
    void setLevel(int lit, int l);

    /**
     * Returns the reason of the assignment of a literal.
     * 
     * @param lit
     *            a literal formula internal representation.
     * @return the constraint that propagated that literal, else null.
     */
    Constr getReason(int lit);

    /**
     * Sets the reason of the assignment of a literal.
     * 
     * @param lit
     *            a literal formula internal representation.
     * @param r
     *            the constraint that forces the assignment of that literal,
     *            null if there are none.
     */
    void setReason(int lit, Constr r);

    /**
     * Retrieve the methods to call when the solver backtracks. Useful for
     * parseCounter based data structures.
     * 
     * @param lit
     *            a literal formula internal representation.
     * @return a list of methods to call c bactracking.
     */
    IVec<Undoable> undos(int lit);

    /**
     * Record a new constraint to watch when a literal is satisfied.
     * 
     * @param lit
     *            a literal formula internal representation.
     * @param c
     *            a constraint that contains the negation of that literal.
     */
    void watch(int lit, Propagatable c);

    /**
     * @param lit
     *            a literal formula internal representation.
     * @return the list of all the fact that watch the negation of lit
     */
    IVec<Propagatable> watches(int lit);

    /**
     * Returns a textual representation of the truth value of that literal.
     * 
     * @param lit
     *            a literal formula internal representation.
     * @return one of T for true, F for False or ? for unassigned.
     */
    String valueToString(int lit);
}
