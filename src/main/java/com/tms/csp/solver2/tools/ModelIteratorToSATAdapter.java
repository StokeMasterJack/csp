package com.tms.csp.solver2.tools;

import com.tms.csp.solver2.specs.ISolver;
import com.tms.csp.solver2.specs.IVecInt;
import com.tms.csp.solver2.specs.TimeoutException;

/**
 * This class allow to use the ModelIterator class as a solver.
 * 
 * @author lonca
 */
public class ModelIteratorToSATAdapter extends ModelIterator {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int[] lastModel = null;
    private final SolutionFoundListener sfl;

    public ModelIteratorToSATAdapter(ISolver solver, SolutionFoundListener sfl) {
        this(solver, Long.MAX_VALUE, sfl);
    }

    public ModelIteratorToSATAdapter(ISolver solver, long bound,
            SolutionFoundListener sfl) {
        super(solver, bound);
        this.sfl = sfl;
    }

    @Override
    public boolean isSatisfiable() throws TimeoutException {
        boolean isSat = false;
        while (super.isSatisfiable()) {
            isSat = true;
            lastModel = super.model();
            this.sfl.onSolutionFound(lastModel);
        }
        return isSat;
    }

    @Override
    public boolean isSatisfiable(IVecInt assumps) throws TimeoutException {
        boolean isSat = false;
        while (super.isSatisfiable(assumps)) {
            isSat = true;
            lastModel = super.model();
            this.sfl.onSolutionFound(lastModel);
        }
        return isSat;
    }

    @Override
    public int[] model() {
        return this.lastModel;
    }

}
