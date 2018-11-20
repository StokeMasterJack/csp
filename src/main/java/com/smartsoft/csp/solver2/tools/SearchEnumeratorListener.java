package com.smartsoft.csp.solver2.tools;

import com.smartsoft.csp.solver2.specs.ISolverService;
import com.smartsoft.csp.solver2.specs.RandomAccessModel;
import com.smartsoft.csp.solver2.specs.Lbool;

/**
 * That class allows to iterate over the models from the inside: conflicts are
 * created to ask the solver to backtrack.
 * 
 * @author leberre
 * 
 */
public class SearchEnumeratorListener extends
        SearchListenerAdapter<ISolverService> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private ISolverService solverService;

    private int nbsolutions = 0;

    private final SolutionFoundListener sfl;

    public SearchEnumeratorListener(SolutionFoundListener sfl) {
        this.sfl = sfl;
    }

    @Override
    public void init(ISolverService solverService) {
        this.solverService = solverService;
    }

    @Override
    public void solutionFound(int[] model, RandomAccessModel lazyModel) {
        int[] clause = new int[model.length];
        for (int i = 0; i < model.length; i++) {
            clause[i] = -model[i];
        }
        this.solverService.addClauseOnTheFly(clause);
        this.nbsolutions++;
        sfl.onSolutionFound(model);
    }

    @Override
    public void end(Lbool result) {
        assert result != Lbool.TRUE;
    }

    public int getNumberOfSolutionFound() {
        return this.nbsolutions;
    }
}
