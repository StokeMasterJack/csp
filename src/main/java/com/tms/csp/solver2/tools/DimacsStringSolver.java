package com.tms.csp.solver2.tools;


import com.tms.csp.solver2.specs.ContradictionException;
import com.tms.csp.solver2.specs.IConstr;
import com.tms.csp.solver2.specs.IVecInt;
import com.tms.csp.solver2.specs.IteratorInt;

import java.io.PrintStream;

/**
 * Solver used to write down a CNF into a String.
 * 
 * It is especially useful compared to the DimacsOutputSolver because the number
 * of clauses does not need to be known formula advance.
 * 
 * @author leberre
 * 
 */
public class DimacsStringSolver extends AbstractOutputSolver {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private StringBuffer out;

    private int firstCharPos;

    private final int initBuilderSize;

    private int maxvarid = 0;

    public DimacsStringSolver() {
        this(16);
    }

    public DimacsStringSolver(int initSize) {
        this.out = new StringBuffer(initSize);
        this.initBuilderSize = initSize;
    }

    public StringBuffer getOut() {
        return this.out;
    }

    public int newVar() {
        return 0;
    }

    @Override
    public int newVar(int howmany) {
        setNbVars(howmany);
        return howmany;
    }

    protected void setNbVars(int howmany) {
        this.nbvars = howmany;
        this.maxvarid = howmany;
    }

    public void setExpectedNumberOfClauses(int nb) {
        this.out.append(" ");
        this.out.append(nb);
        this.nbclauses = nb;
        this.fixedNbClauses = true;
    }

    public IConstr addClause(IVecInt literals) throws ContradictionException {
        if (this.firstConstr) {
            if (!this.fixedNbClauses) {
                this.firstCharPos = 0;
                this.out.append("                    ");
                this.out.append("\n");
                this.nbclauses = 0;
            }
            this.firstConstr = false;
        }
        if (!this.fixedNbClauses) {
            this.nbclauses++;
        }
        for (IteratorInt iterator = literals.iterator(); iterator.hasNext();) {
            this.out.append(iterator.next()).append(" ");
        }
        this.out.append("0\n");
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
            this.firstCharPos = 0;
            this.out.append("                    ");
            this.out.append("\n");
            this.nbclauses = 0;
            this.firstConstr = false;
        }

        for (int i = 0; i <= literals.size(); i++) {
            for (int j = i + 1; j < literals.size(); j++) {
                if (!this.fixedNbClauses) {
                    this.nbclauses++;
                }
                this.out.append(-literals.get(i));
                this.out.append(" ");
                this.out.append(-literals.get(j));
                this.out.append(" 0\n");
            }
        }
        return null;
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

    public IConstr addAtLeast(IVecInt literals, int degree)
            throws ContradictionException {
        if (degree > 1) {
            throw new UnsupportedOperationException(
                    "Not a clausal problem! degree " + degree);
        }
        assert degree == 1;
        return addClause(literals);
    }

    public void reset() {
        this.fixedNbClauses = false;
        this.firstConstr = true;
        this.out = new StringBuffer(this.initBuilderSize);
        this.maxvarid = 0;
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
        return this.maxvarid;
    }

    @Override
    public String toString() {
        this.out.insert(this.firstCharPos, "p cnf " + this.maxvarid + " "
                + this.nbclauses);
        return this.out.toString();
    }

    /**
     * @since 2.1
     */
    public int nextFreeVarId(boolean reserve) {
        if (reserve) {
            return ++this.maxvarid;
        }
        return this.maxvarid + 1;
    }

    /**
     * @since 2.3.1
     */
    public int[] modelWithInternalVariables() {
        throw new UnsupportedOperationException();
    }

    public int realNumberOfVariables() {
        return this.maxvarid;
    }

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
