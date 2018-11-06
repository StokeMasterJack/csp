package com.tms.csp.solver2.tools;

import com.tms.csp.solver2.specs.ISolverService;
import com.tms.csp.solver2.specs.RandomAccessModel;

public class HeuristicsTracing extends SearchListenerAdapter<ISolverService> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private ISolverService solverService;
    private final IVisualizationTool visuTool;

    public HeuristicsTracing(IVisualizationTool visuTool) {
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

        int n = this.solverService.nVars();
        double[] heuristics = this.solverService.getVariableHeuristics();
        for (int i = 1; i <= n; i++) {
            this.visuTool.addPoint(heuristics[i], i);
        }
        this.visuTool.end();
    }

    @Override
    public void init(ISolverService solverService) {
        this.solverService = solverService;
    }
}
