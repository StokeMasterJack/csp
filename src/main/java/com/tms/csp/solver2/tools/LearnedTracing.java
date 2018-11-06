package com.tms.csp.solver2.tools;

import com.tms.csp.solver2.specs.IConstr;
import com.tms.csp.solver2.specs.ISolverService;
import com.tms.csp.solver2.specs.IVec;
import com.tms.csp.solver2.specs.RandomAccessModel;

public class LearnedTracing extends SearchListenerAdapter<ISolverService> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private ISolverService solverService;

    private final IVisualizationTool visuTool;

    public LearnedTracing(IVisualizationTool visuTool) {
        this.visuTool = visuTool;
    }

    @Override
    public void solutionFound(int[] model, RandomAccessModel lazyModel) {
        trace();
    }

    @Override
    public void restarting() {
        trace();
    }

    private void trace() {
        this.visuTool.init();
        IVec<? extends IConstr> constrs = this.solverService
                .getLearnedConstraints();
        int n = constrs.size();
        for (int i = 0; i < n; i++) {
            this.visuTool.addPoint(i, constrs.get(i).getActivity());
        }
        this.visuTool.end();

    }

    @Override
    public void init(ISolverService solverService) {
        this.solverService = solverService;
    }

    @Override
    public void cleaning() {
        trace();
    }
}
