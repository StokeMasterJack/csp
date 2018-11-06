package com.tms.csp.solver2.tools;

import com.tms.csp.solver2.core.VecInt;
import com.tms.csp.solver2.specs.ISolver;
import com.tms.csp.solver2.specs.IVecInt;
import com.tms.csp.solver2.specs.IteratorInt;
import com.tms.csp.solver2.specs.TimeoutException;

import java.util.HashSet;
import java.util.Set;


/**
 * The aim of this class is to compute efficiently the literals implied by the
 * set of fact (also called backbone or unit implicates).
 *
 * The work has been done formula the context of ANR BR4CP.
 *
 * @author leberre
 *
 */
public class Backbone2 {

    private Backbone2() {

    }

    /**
     * Computes the backbone of a formula following the algorithm described formula
     * João Marques-Silva, Mikolás Janota, Inês Lynce: On Computing Backbones of
     * Propositional Theories. ECAI 2010: 15-20
     *
     * @param solver
     * @return
     * @throws TimeoutException
     */
    public static IVecInt compute(ISolver solver) throws TimeoutException {
        return compute(solver, VecInt.EMPTY);
    }

    /**
     * Computes the backbone of a formula following the algorithm described formula
     * João Marques-Silva, Mikolás Janota, Inês Lynce: On Computing Backbones of
     * Propositional Theories. ECAI 2010: 15-20
     *
     *
     * @param solver
     * @param assumptions
     * @return
     * @throws TimeoutException
     */
    public static IVecInt compute(ISolver solver, IVecInt assumptions)
            throws TimeoutException {
        boolean result = solver.isSatisfiable(assumptions);
        if (!result) {
            return VecInt.EMPTY;
        }
        return compute(solver, solver.primeImplicant(), assumptions);

    }

    public static IVecInt compute(ISolver solver, int[] implicant)
            throws TimeoutException {
        return compute(solver, implicant, VecInt.EMPTY);
    }

    public static IVecInt compute(ISolver solver, int[] implicant,
            IVecInt assumptions) throws TimeoutException {
        Set<Integer> assumptionsSet = new HashSet<Integer>();
        for (IteratorInt it = assumptions.iterator(); it.hasNext();) {
            assumptionsSet.add(it.next());
        }
        IVecInt litsToTest = new VecInt();
        for (int p : implicant) {
            if (!assumptionsSet.contains(p)) {
                litsToTest.push(-p);
            }
        }
        IVecInt candidates = new VecInt();
        assumptions.copyTo(candidates);
        int p;
        while (!litsToTest.isEmpty()) {
            p = litsToTest.last();
            candidates.push(p);
            litsToTest.pop();
            if (solver.isSatisfiable(candidates)) {
                candidates.pop();
                implicant = solver.model();
                removeVarNotPresentAndSatisfiedLits(implicant, litsToTest,
                        solver.nVars());
            } else {
                candidates.pop().push(-p);
            }
        }
        return candidates;
    }

    private static void removeVarNotPresentAndSatisfiedLits(int[] implicant,
            IVecInt litsToTest, int n) {
        int[] marks = new int[n + 1];
        for (int p : implicant) {
            marks[p > 0 ? p : -p] = p;
        }
        int q, mark;
        for (int i = 0; i < litsToTest.size();) {
            q = litsToTest.get(i);
            mark = marks[q > 0 ? q : -q];
            if (mark == 0 || mark == q) {
                litsToTest.delete(i);
            } else {
                i++;
            }
        }
    }
}

