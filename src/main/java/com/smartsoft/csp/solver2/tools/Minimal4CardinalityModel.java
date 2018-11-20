package com.smartsoft.csp.solver2.tools;

import com.smartsoft.csp.solver2.core.VecInt;
import com.smartsoft.csp.solver2.specs.ContradictionException;
import com.smartsoft.csp.solver2.specs.IConstr;
import com.smartsoft.csp.solver2.specs.ISolver;
import com.smartsoft.csp.solver2.specs.IVecInt;
import com.smartsoft.csp.solver2.specs.TimeoutException;

/**
 * Computes models with a minimal number (with respect to cardinality) of
 * negative literals. This is done be adding a constraint c the number of
 * negative literals each time a model if found (the number of negative literals
 * occuring formula the model minus one).
 * 
 * @author leberre
 * @see com.smartsoft.csp.solver2.specs.ISolver#addAtMost(IVecInt, int)
 */
public class Minimal4CardinalityModel extends AbstractMinimalModel {

    private static final long serialVersionUID = 1L;

    private int[] prevfullmodel;

    /**
     * @param solver
     */
    public Minimal4CardinalityModel(ISolver solver) {
        super(solver);
    }

    public Minimal4CardinalityModel(ISolver solver, IVecInt p,
            SolutionFoundListener modelListener) {
        super(solver, p, modelListener);
    }

    public Minimal4CardinalityModel(ISolver solver, IVecInt p) {
        super(solver, p);
    }

    public Minimal4CardinalityModel(ISolver solver,
            SolutionFoundListener modelListener) {
        super(solver, modelListener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sat4j.ISolver#model()
     */
    @Override
    public int[] model() {
        int[] prevmodel = null;
        IConstr lastOne = null;
        IVecInt literals = new VecInt(pLiterals.size());
        for (int p : pLiterals) {
            literals.push(p);
        }
        try {
            do {
                prevfullmodel = super.modelWithInternalVariables();
                prevmodel = super.model();
                int counter = 0;
                for (int q : prevfullmodel) {
                    if (pLiterals.contains(q)) {
                        counter++;
                    }
                }
                lastOne = addAtMost(literals, counter - 1);
            } while (isSatisfiable());
        } catch (TimeoutException e) {
            throw new IllegalStateException("Solver timed out"); //$NON-NLS-1$
        } catch (ContradictionException e) {
            // added trivial unsat clauses
        }
        if (lastOne != null) {
            removeConstr(lastOne);
        }
        return prevmodel;
    }

    @Override
    public int[] modelWithInternalVariables() {
        model();
        return prevfullmodel;
    }
}
