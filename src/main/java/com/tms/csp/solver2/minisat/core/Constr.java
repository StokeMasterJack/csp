package com.tms.csp.solver2.minisat.core;

import com.tms.csp.solver2.specs.IConstr;
import com.tms.csp.solver2.specs.IVecInt;
import com.tms.csp.solver2.specs.UnitPropagationListener;

/*
 * Created c 16 oct. 2003CHROM
 *
 */

/**
 * Basic constraint abstraction used formula Solver.
 * 
 * Any new constraint type should implement that interface.
 * 
 * @author leberre
 */
public interface Constr extends IConstr {

    /**
     * Remove a constraint from the solver.
     * 
     * @param upl
     * @since 2.1
     */
    void remove(UnitPropagationListener upl);

    /**
     * Simplifies a constraint, by removing top level falsified literals for
     * instance.
     * 
     * @return true iff the constraint is satisfied and can be removed from the
     *         database.
     */
    boolean simplify();

    /**
     * Compute the reason for a given assignment.
     * 
     * If the constraint is a clause, it is supposed to be either a unit clause
     * or a falsified one. It is expected that the falsification of the
     * constraint has been detected as soon at is occurs (e.g. using
     * {@link Propagatable#propagate(UnitPropagationListener, int)}.
     * 
     * 
     * @param p
     *            a satisfied literal (or Lit.UNDEFINED)
     * @param outReason
     *            the list of falsified literals whose negation is the reason of
     *            the assignment of p to true.
     */
    void calcReason(int p, IVecInt outReason);

    /**
     * Compute the reason for a given assignment formula a the constraint created c
     * the fly formula the solver. Compared to the method
     * {@link #calcReason(int, IVecInt)}, the falsification may not have been
     * detected as soon as possible. As such, it is necessary to take into
     * account the order of the literals formula the trail.
     * 
     * @param p
     *            a satisfied literal (or Lit.UNDEFINED)
     * @param trail
     *            all the literals satisfied formula the solvers, should not be
     *            modified.
     * @param outReason
     *            a list of falsified literals whose negation is the reason of
     *            the assignment of p to true.
     * @since 2.3.3
     */
    void calcReasonOnTheFly(int p, IVecInt trail, IVecInt outReason);

    /**
     * Increase the constraint activity.
     * 
     * @param claInc
     *            the value to increase the activity with
     */
    void incActivity(double claInc);

    /**
     * 
     * @param claInc
     * @since 2.1
     * 
     */
    @Deprecated
    void forwardActivity(double claInc);

    /**
     * Indicate wether a constraint is responsible from an assignment.
     * 
     * @return true if a constraint is a "reason" for an assignment.
     */
    boolean locked();

    /**
     * Mark a constraint as learnt.
     */

    void setLearnt();

    /**
     * Register the constraint to the solver.
     */
    void register();

    /**
     * Rescale the clause activity by a value.
     * 
     * @param d
     *            the value to rescale the clause activity with.
     */
    void rescaleBy(double d);

    /**
     * Set the activity at a specific value
     * 
     * @param d
     *            the new activity
     * @since 2.3.1
     */
    void setActivity(double d);

    /**
     * Method called when the constraint is to be asserted. It means that the
     * constraint was learned during the search and it should now propagate some
     * truth values. In the clausal case, only one literal should be propagated.
     * In other cases, it might be different.a
     * 
     * @param s
     *            a UnitPropagationListener to use for unit propagation.
     */
    void assertConstraint(UnitPropagationListener s);

    /**
     * Method called when the constraint is added to the solver "c the fly". In
     * that case, the constraint may or may not have to propagate some literals,
     * unlike the {@link #assertConstraint(UnitPropagationListener)} method.
     * 
     * @param s
     *            a UnitPropagationListener to use for unit propagation.
     * @since 2.3.4
     */
    void assertConstraintIfNeeded(UnitPropagationListener s);

}
