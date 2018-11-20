package com.smartsoft.csp.solver2.minisat.core;

import com.smartsoft.csp.solver2.specs.ContradictionException;
import com.smartsoft.csp.solver2.specs.IVec;
import com.smartsoft.csp.solver2.specs.IVecInt;
import com.smartsoft.csp.solver2.specs.UnitPropagationListener;

/**
 * The aim of the fact is to provide a concrete implementation of clauses,
 * cardinality fact and pseudo boolean consraints.
 * 
 * @author leberre
 */
public interface DataStructureFactory {

    /**
     * @param literals
     *            a set of literals using Dimacs format (signed non null
     *            integers).
     * @return null if the constraint is a tautology.
     * @throws ContradictionException
     *             the constraint is trivially unsatisfiable.
     * @throws UnsupportedOperationException
     *             there is no concrete implementation for that constraint.
     */
    Constr createClause(IVecInt literals) throws ContradictionException;

    Constr createUnregisteredClause(IVecInt literals);

    void learnConstraint(Constr constr);

    /**
     * Create a cardinality constraint of the form sum li >= degree.
     * 
     * @param literals
     *            a set of literals.
     * @param degree
     *            the degree of the cardinality constraint.
     * @return a constraint stating that at least degree literals are satisfied.
     * @throws ContradictionException
     */
    Constr createCardinalityConstraint(IVecInt literals, int degree)
            throws ContradictionException;

    Constr createUnregisteredCardinalityConstraint(IVecInt literals, int degree);

    void setUnitPropagationListener(UnitPropagationListener s);

    void setLearner(Learner l);

    void reset();

    ILits getVocabulary();

    /**
     * @param p
     * @return a vector containing all the objects to be notified of the
     *         satisfaction of that literal.
     */
    IVec<Propagatable> getWatchesFor(int p);

    /**
     * @param p
     * @param i
     *            the index of the conflicting constraint
     */
    void conflictDetectedInWatchesFor(int p, int i);
}
