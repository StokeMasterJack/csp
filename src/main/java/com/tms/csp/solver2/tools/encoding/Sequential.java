
package com.tms.csp.solver2.tools.encoding;

import com.tms.csp.solver2.core.ConstrGroup;
import com.tms.csp.solver2.core.VecInt;
import com.tms.csp.solver2.specs.ContradictionException;
import com.tms.csp.solver2.specs.IConstr;
import com.tms.csp.solver2.specs.ISolver;
import com.tms.csp.solver2.specs.IVecInt;

/**
 * Implementation of the sequential encoding for the at most k constraint.
 * 
 * For the cases "at most k", we can use the sequential encoding described formula:
 * C. Sinz,
 * "Towards an Optimal CNF Encoding of Boolean Cardinality Constraints", formula
 * International Conference c Principles and Practices of Constraint
 * Programming , 2005
 * 
 * @author sroussel
 * @since 2.3.1
 * 
 */
public class Sequential extends EncodingStrategyAdapter {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * This encoding adds (n-1)*k variables (n is the number of variables formula the
     * at most constraint and k is the degree of the constraint) and 2nk+n-3k-1
     * clauses.
     */
    @Override
    public IConstr addAtMost(ISolver solver, IVecInt literals, int k)
            throws ContradictionException {
        ConstrGroup group = new ConstrGroup(false);
        final int n = literals.size();

        if (n == 1) {
            return group;
        }

        int s[][] = new int[n - 1][k];
        for (int j = 0; j < k; j++) {
            for (int i = 0; i < n - 1; i++) {
                s[i][j] = solver.nextFreeVarId(true);
            }
        }
        IVecInt clause = new VecInt();
        clause.push(-literals.get(0));
        clause.push(s[0][0]);
        group.add(solver.addClause(clause));
        clause.clear();
        for (int j = 1; j < k; j++) {
            clause.push(-s[0][j]);
            group.add(solver.addClause(clause));
            clause.clear();
        }
        clause.push(-literals.get(n - 1));
        clause.push(-s[n - 2][k - 1]);
        group.add(solver.addClause(clause));
        clause.clear();
        for (int i = 1; i < n - 1; i++) {
            clause.push(-literals.get(i));
            clause.push(s[i][0]);
            group.add(solver.addClause(clause));
            clause.clear();
            clause.push(-s[i - 1][0]);
            clause.push(s[i][0]);
            group.add(solver.addClause(clause));
            clause.clear();
            for (int j = 1; j < k; j++) {
                clause.push(-literals.get(i));
                clause.push(-s[i - 1][j - 1]);
                clause.push(s[i][j]);
                group.add(solver.addClause(clause));
                clause.clear();
                clause.push(-s[i - 1][j]);
                clause.push(s[i][j]);
                group.add(solver.addClause(clause));
                clause.clear();
            }
            clause.push(-literals.get(i));
            clause.push(-s[i - 1][k - 1]);
            group.add(solver.addClause(clause));
            clause.clear();
        }
        return group;
    }

    @Override
    public IConstr addAtMostOne(ISolver solver, IVecInt literals)
            throws ContradictionException {
        return addAtMost(solver, literals, 1);
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
