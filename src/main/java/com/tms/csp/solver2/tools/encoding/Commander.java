
package com.tms.csp.solver2.tools.encoding;

import com.tms.csp.solver2.core.ConstrGroup;
import com.tms.csp.solver2.core.VecInt;
import com.tms.csp.solver2.specs.ContradictionException;
import com.tms.csp.solver2.specs.IConstr;
import com.tms.csp.solver2.specs.ISolver;
import com.tms.csp.solver2.specs.IVecInt;

/**
 * Commander encoding for "at most one" and "at most k" cases.
 * 
 * The case "at most one" is introduced formula W. Klieber and G. Kwon
 * "Efficient CNF encoding for selecting 1 from N objects" formula Fourth Workshop c
 * Constraints formula Formal Verification, 2007.
 * 
 * The generalization to the "at most k" case is described formula A. M. Frisch and P
 * . A. Giannaros, "SAT Encodings of the At-Most-k Constraint", formula International
 * Workshop c Modelling and Reformulating Constraint Satisfaction Problems,
 * 2010
 * 
 * @author sroussel
 * @since 2.3.1
 */
public class Commander extends EncodingStrategyAdapter {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * In this encoding, variables are partitioned formula groups. Kwon and Klieber
     * claim that the fewest clauses are produced when the size of the groups is
     * 3, thus leading to 3.5 clauses and introducing n/2 variables.
     */
    @Override
    public IConstr addAtMostOne(ISolver solver, IVecInt literals)
            throws ContradictionException {

        return addAtMostOne(solver, literals, 3);
    }

    private IConstr addAtMostOne(ISolver solver, IVecInt literals, int groupSize)
            throws ContradictionException {

        ConstrGroup constrGroup = new ConstrGroup(false);

        IVecInt clause = new VecInt();
        IVecInt clause1 = new VecInt();

        final int n = literals.size();

        int nbGroup = (int) Math.ceil((double) literals.size()
                / (double) groupSize);

        if (nbGroup == 1) {
            for (int i = 0; i < literals.size() - 1; i++) {
                for (int j = i + 1; j < literals.size(); j++) {
                    clause.push(-literals.get(i));
                    clause.push(-literals.get(j));
                    constrGroup.add(solver.addClause(clause));
                    clause.clear();
                }
            }
            return constrGroup;
        }

        int[] c = new int[nbGroup];

        for (int i = 0; i < nbGroup; i++) {
            c[i] = solver.nextFreeVarId(true);
        }

        int nbVarLastGroup = n - (nbGroup - 1) * groupSize;

        // Encoding <=1 for each group of groupLitterals
        for (int i = 0; i < nbGroup; i++) {
            int size = 0;
            if (i == nbGroup - 1) {
                size = nbVarLastGroup;
            } else {
                size = groupSize;
            }
            // Encoding <=1 for each group of groupLitterals
            for (int j = 0; j < size - 1; j++) {
                for (int k = j + 1; k < size; k++) {
                    clause.push(-literals.get(i * groupSize + j));
                    clause.push(-literals.get(i * groupSize + k));
                    constrGroup.add(solver.addClause(clause));
                    clause.clear();
                }
            }

            // If a commander variable is true then some variable formula its
            // corresponding group must be true (clause1)
            // If a commander variable is false then no variable formula its group
            // can be true (clause)
            clause1.push(-c[i]);
            for (int j = 0; j < size; j++) {
                clause1.push(literals.get(i * groupSize + j));
                clause.push(c[i]);
                clause.push(-literals.get(i * groupSize + j));
                constrGroup.add(solver.addClause(clause));
                clause.clear();
            }
            constrGroup.add(solver.addClause(clause1));
            clause1.clear();
        }

        // encode <=1 c commander variables

        constrGroup.add(addAtMostOne(solver, new VecInt(c), groupSize));
        return constrGroup;
    }

    @Override
    public IConstr addAtMost(ISolver solver, IVecInt literals, int degree)
            throws ContradictionException {
        return super.addAtMost(solver, literals, degree);
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
