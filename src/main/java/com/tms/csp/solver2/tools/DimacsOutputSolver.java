package com.tms.csp.solver2.tools;

import com.tms.csp.solver2.specs.ContradictionException;
import com.tms.csp.solver2.specs.IConstr;
import com.tms.csp.solver2.specs.IVecInt;
import com.tms.csp.solver2.specs.IteratorInt;

import java.io.PrintStream;

/**
 * Solver used to display formula a writer the CNF instance formula Dimacs format.
 *
 * That solver is useful to produce CNF files to be used by third party solvers.
 *
 * @author leberre
 *
 */
public class DimacsOutputSolver extends AbstractOutputSolver {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private transient PrintStream out;

    public DimacsOutputSolver() {
        this(System.out);
    }

    public DimacsOutputSolver(PrintStream pw) {
        this.out = pw;
    }


    public int newVar() {
        return 0;
    }

    @Override
    public int newVar(int howmany) {
        this.out.print("p cnf " + howmany);
        this.nbvars = howmany;
        return 0;
    }

    public void setExpectedNumberOfClauses(int nb) {
        this.out.println(" " + nb);
        this.nbclauses = nb;
        this.fixedNbClauses = true;
    }

    public IConstr addClause(IVecInt literals) throws ContradictionException {
        if (this.firstConstr) {
            if (!this.fixedNbClauses) {
                this.out.println(" XXXXXX");
            }
            this.firstConstr = false;
        }
        for (IteratorInt iterator = literals.iterator(); iterator.hasNext(); ) {
            this.out.print(iterator.next() + " ");
        }
        this.out.println("0");
        return null;
    }

    public IConstr addAtMost(IVecInt literals, int degree)
            throws ContradictionException {
        if (degree > 1) {
            throw new UnsupportedOperationException(
                    "Not a clausal problem! degree " + degree);
        }
        assert degree == 1;
        if (this.firstConstr) {
            if (!this.fixedNbClauses) {
                this.out.println("XXXXXX");
            }
            this.firstConstr = false;
        }
        for (int i = 0; i <= literals.size(); i++) {
            for (int j = i + 1; j < literals.size(); j++) {
                this.out.println("" + -literals.get(i) + " " + -literals.get(j)
                        + " 0");
            }
        }
        return null;
    }

    public IConstr addAtLeast(IVecInt literals, int degree)
            throws ContradictionException {
        if (degree > 1) {
            throw new UnsupportedOperationException(
                    "Not a clausal problem! degree " + degree);
        }
        assert degree == 1;
        return addClause(literals);
    }

    public IConstr addExactly(IVecInt literals, int n)
            throws ContradictionException {
        if (n > 1) {
            throw new UnsupportedOperationException(
                    "Not a clausal problem! degree " + n);
        }
        assert n == 1;
        addAtMost(literals, n);
        addAtLeast(literals, n);
        return null;
    }

    public void reset() {
        this.fixedNbClauses = false;
        this.firstConstr = true;

    }

    public String toString(String prefix) {
        return "Dimacs output solver";
    }

    @Override
    public int nConstraints() {
        return this.nbclauses;
    }

    @Override
    public int nVars() {
        return this.nbvars;
    }

    /**
     * @since 2.1
     */
    public int nextFreeVarId(boolean reserve) {
        if (reserve) {
            return ++this.nbvars;
        }
        return this.nbvars + 1;
    }

    /**
     * @since 2.3.1
     */
    public int[] modelWithInternalVariables() {
        throw new UnsupportedOperationException();
    }

    /**
     * @since 2.3.1
     */
    public int realNumberOfVariables() {
        return this.nbvars;
    }

    /**
     * @since 2.3.1
     */
    public void registerLiteral(int p) {
        throw new UnsupportedOperationException();
    }

    /**
     * @since 2.3.2
     */
    public boolean primeImplicant(int p) {
        throw new UnsupportedOperationException();
    }

    /**
     * @since 2.3.3
     */
    public void printStat(PrintStream out) {
        throw new UnsupportedOperationException();

    }

    /**
     * @since 2.3.3
     */
    public void printInfos(PrintStream out) {
        throw new UnsupportedOperationException();

    }
}
