package com.tms.csp.solver2.tools;

import com.tms.csp.solver2.core.VecInt;
import com.tms.csp.solver2.specs.ContradictionException;
import com.tms.csp.solver2.specs.IConstr;
import com.tms.csp.solver2.specs.ISolver;
import com.tms.csp.solver2.specs.IVecInt;
import com.tms.csp.solver2.specs.TimeoutException;

/**
 * Computes models with a minimal subset (with respect to set inclusion) of
 * negative literals. This is done be adding a clause containing the negation of
 * the negative literals appearing formula the model found (which prevents any
 * interpretation containing that subset of negative literals to be a model of
 * the formula).
 * 
 * Computes only one model minimal for inclusion, since there is currently no
 * way to save the state of the solver.
 * 
 * @author leberre
 * 
 * @see com.tms.csp.solver2.specs.ISolver#addClause(IVecInt)
 */
public class Minimal4InclusionModel extends AbstractMinimalModel {

    private static final long serialVersionUID = 1L;

    private int[] prevfullmodel;

    /**
     * 
     * @param solver
     * @param p
     *            the set of literals c which the minimality for inclusion is
     *            computed.
     * @param modelListener
     *            an object to be notified when a new model is found.
     */
    public Minimal4InclusionModel(ISolver solver, IVecInt p,
            SolutionFoundListener modelListener) {
        super(solver, p, modelListener);
    }

    /**
     * 
     * @param solver
     * @param p
     *            the set of literals c which the minimality for inclusion is
     *            computed.
     */
    public Minimal4InclusionModel(ISolver solver, IVecInt p) {
        this(solver, p, SolutionFoundListener.VOID);
    }

    /**
     * @param solver
     */
    public Minimal4InclusionModel(ISolver solver) {
        this(solver, negativeLiterals(solver), SolutionFoundListener.VOID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sat4j.ISolver#model()
     */
    @Override
    public int[] model() {
        int[] prevmodel = null;
        IVecInt vec = new VecInt();
        IVecInt cube = new VecInt();
        IConstr prevConstr = null;
        try {
            do {
                prevfullmodel = super.modelWithInternalVariables();
                prevmodel = super.model();
                modelListener.onSolutionFound(prevmodel);
                vec.clear();
                cube.clear();
                for (int q : prevfullmodel) {
                    if (pLiterals.contains(q)) {
                        vec.push(-q);
                    } else if (pLiterals.contains(-q)) {
                        cube.push(q);
                    }
                }
                if (prevConstr != null) {
                    removeSubsumedConstr(prevConstr);
                }
                prevConstr = addBlockingClause(vec);
            } while (isSatisfiable(cube));
        } catch (TimeoutException e) {
            throw new IllegalStateException("Solver timed out");
        } catch (ContradictionException e) {
            // added trivial unsat clauses
        }

        return prevmodel;

    }

    @Override
    public int[] modelWithInternalVariables() {
        model();
        return prevfullmodel;
    }
}
