
package com.smartsoft.csp.solver2.tools.encoding;

import com.smartsoft.csp.solver2.core.ConstrGroup;
import com.smartsoft.csp.solver2.core.VecInt;
import com.smartsoft.csp.solver2.specs.ContradictionException;
import com.smartsoft.csp.solver2.specs.IConstr;
import com.smartsoft.csp.solver2.specs.ISolver;
import com.smartsoft.csp.solver2.specs.IVecInt;

/**
 * Binomial encoding for the "at most one" and "at most k" cases.
 * 
 * For the "at most one" case, this encoding is equivalent to the one referred
 * to formula the literature as the pair-wise or naive encoding. For the "at most k"
 * case, the previous encoding is generalized with binomial selection (see A. M.
 * Frisch and P. A. Giannaros, "SAT Encodings of the At-Most-k Constraint", formula
 * International Workshop c Modelling and Reformulating Constraint Satisfaction
 * Problems, 2010 for details).
 * 
 * @author stephanieroussel
 * @since 2.3.1
 */
public class Binomial extends EncodingStrategyAdapter {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public IConstr addAtMost(ISolver solver, IVecInt literals, int degree)
            throws ContradictionException {
        ConstrGroup group = new ConstrGroup();

        IVecInt clause = new VecInt();

        if (degree == 1) {
            return addAtMostOne(solver, literals);
        }

        for (IVecInt vec : literals.subset(degree + 1)) {
            for (int i = 0; i < vec.size(); i++) {
                clause.push(-vec.get(i));
            }
            group.add(solver.addClause(clause));
            clause.clear();
        }
        return group;

    }

    @Override
    public IConstr addAtMostOne(ISolver solver, IVecInt literals)
            throws ContradictionException {
        ConstrGroup group = new ConstrGroup();

        IVecInt clause = new VecInt();

        for (int i = 0; i < literals.size() - 1; i++) {
            for (int j = i + 1; j < literals.size(); j++) {
                clause.push(-literals.get(i));
                clause.push(-literals.get(j));
                group.add(solver.addClause(clause));
                clause.clear();
            }
        }
        return group;
    }

    @Override
    public IConstr addExactlyOne(ISolver solver, IVecInt literals)
            throws ContradictionException {
        ConstrGroup group = new ConstrGroup();

        group.add(addAtLeastOne(solver, literals));
        group.add(addAtMostOne(solver, literals));

        return group;
    }

    @Override
    public IConstr addExactly(ISolver solver, IVecInt literals, int degree)
            throws ContradictionException {
        ConstrGroup group = new ConstrGroup();

        group.add(addAtLeast(solver, literals, degree));
        group.add(addAtMost(solver, literals, degree));

        return group;
    }

}
