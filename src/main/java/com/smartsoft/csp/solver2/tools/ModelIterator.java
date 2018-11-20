package com.smartsoft.csp.solver2.tools;

import com.smartsoft.csp.solver2.core.VecInt;
import com.smartsoft.csp.solver2.specs.ContradictionException;
import com.smartsoft.csp.solver2.specs.ISolver;
import com.smartsoft.csp.solver2.specs.IVecInt;
import com.smartsoft.csp.solver2.specs.TimeoutException;

/**
 * That class allows to iterate through all the models (implicants) of a
 * formula.
 * 
 * <pre>
 * ISolver solver = new ModelIterator(SolverFactory.OneSolver());
 * boolean unsat = true;
 * while (solver.isSatisfiable()) {
 *     unsat = false;
 *     int[] model = solver.model();
 *     // do something with model
 * }
 * if (unsat) {
 *     // UNSAT case
 * }
 * </pre>
 * 
 * It is also possible to limit the number of models returned:
 * 
 * <pre>
 * ISolver solver = new ModelIterator(SolverFactory.OneSolver(), 10);
 * </pre>
 * 
 * will return at most 10 models.
 * 
 * @author leberre
 */
public class ModelIterator extends SolverDecorator<ISolver> {

    private static final long serialVersionUID = 1L;

    private boolean trivialfalsity = false;
    private final long bound;
    private long nbModelFound = 0;

    /**
     * Create an iterator over the solutions available formula <code>solver</code>.
     * The iterator will look for one new model at each call to isSatisfiable()
     * and will discard that model at each call to model().
     * 
     * @param solver
     *            a solver containing the fact to satisfy.
     * @see #isSatisfiable()
     * @see #isSatisfiable(boolean)
     * @see #isSatisfiable(IVecInt)
     * @see #isSatisfiable(IVecInt, boolean)
     * @see #model()
     */
    public ModelIterator(ISolver solver) {
        this(solver, Long.MAX_VALUE);
    }

    /**
     * Create an iterator over a limited number of solutions available formula
     * <code>solver</code>. The iterator will look for one new model at each
     * call to isSatisfiable() and will discard that model at each call to
     * model(). At most <code>bound</code> calls to models() will be allowed
     * before the method <code>isSatisfiable()</code> returns false.
     * 
     * @param solver
     *            a solver containing the fact to satisfy.
     * @param bound
     *            the maximum number of models to return.
     * @since 2.1
     * @see #isSatisfiable()
     * @see #isSatisfiable(boolean)
     * @see #isSatisfiable(IVecInt)
     * @see #isSatisfiable(IVecInt, boolean)
     * @see #model()
     */
    public ModelIterator(ISolver solver, long bound) {
        super(solver);
        this.bound = bound;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sat4j.ISolver#model()
     */
    @Override
    public int[] model() {
        int[] last = super.model();
        this.nbModelFound++;
        IVecInt clause = new VecInt(last.length);
        for (int q : last) {
            clause.push(-q);
        }
        try {
            addBlockingClause(clause);
        } catch (ContradictionException e) {
            this.trivialfalsity = true;
        }
        return last;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sat4j.ISolver#isSatisfiable()
     */
    @Override
    public boolean isSatisfiable() throws TimeoutException {
        if (this.trivialfalsity || this.nbModelFound >= this.bound) {
            return false;
        }
        this.trivialfalsity = false;
        return super.isSatisfiable(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sat4j.ISolver#isSatisfiable(org.sat4j.datatype.VecInt)
     */
    @Override
    public boolean isSatisfiable(IVecInt assumps) throws TimeoutException {
        if (this.trivialfalsity || this.nbModelFound >= this.bound) {
            return false;
        }
        this.trivialfalsity = false;
        return super.isSatisfiable(assumps, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sat4j.ISolver#reset()
     */
    @Override
    public void reset() {
        this.trivialfalsity = false;
        this.nbModelFound = 0;
        super.reset();
    }

    @Override
    public int[] primeImplicant() {
        int[] last = super.primeImplicant();
        this.nbModelFound += Math.pow(2, nVars() - last.length);
        IVecInt clause = new VecInt(last.length);
        for (int q : last) {
            clause.push(-q);
        }
        try {
            addBlockingClause(clause);
        } catch (ContradictionException e) {
            this.trivialfalsity = true;
        }
        return last;
    }

    /**
     * To know the number of models already found.
     * 
     * @return the number of models found so far.
     * @since 2.3
     */
    public long numberOfModelsFoundSoFar() {
        return this.nbModelFound;
    }
}
