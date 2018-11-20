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
public class SearchMinOneListener extends SearchListenerAdapter<ISolverService> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private ISolverService solverService;

    private final SolutionFoundListener sfl;

    public SearchMinOneListener(SolutionFoundListener sfl) {
        this.sfl = sfl;
    }

    @Override
    public void init(ISolverService solverService) {
        this.solverService = solverService;
    }

    @Override
    public void solutionFound(int[] model, RandomAccessModel lazyModel) {
        int degree = 0;
        int[] variables = new int[model.length];
        for (int i = 0; i < model.length; i++) {
            if (model[i] > 0) {
                degree++;
                variables[i] = model[i];
            } else {
                variables[i] = -model[i];
            }
        }
        System.out.println(solverService.getLogPrefix() + " #one " + degree);
        this.solverService.addAtMostOnTheFly(variables, degree - 1);
        sfl.onSolutionFound(model);
    }

    @Override
    public void end(Lbool result) {
        assert result != Lbool.TRUE;
    }
}
