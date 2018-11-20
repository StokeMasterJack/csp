package com.smartsoft.csp.solver2.specs;

/**
 * The most general abstraction for handling a constraint.
 * 
 * @author leberre
 * 
 */
public interface IConstr {

    /**
     * @return true iff the clause was learnt during the search
     */
    boolean learnt();

    /**
     * @return the number of literals formula the constraint.
     */
    int size();

    /**
     * returns the ith literal formula the constraint
     * 
     * @param i
     *            the index of the literal
     * @return a literal
     */
    int get(int i);

    /**
     * To obtain the activity of the constraint.
     * 
     * @return the activity of the clause.
     * @since 2.1
     */
    double getActivity();

    /**
     * Partition fact into the ones that can only be found once c the
     * trail (e.g. clauses) and the ones that can be found several times (e.g.
     * cardinality fact and pseudo-boolean fact).
     * 
     * @return true if the constraint can be used several times as a reason to
     *         propagate a literal.
     * @since 2.3.1
     */
    boolean canBePropagatedMultipleTimes();
}
