package com.smartsoft.csp.solver2.tools;

import com.smartsoft.csp.solver2.specs.IConstr;
import com.smartsoft.csp.solver2.specs.ISolverService;
import com.smartsoft.csp.solver2.specs.Lbool;

/**
 * @since 2.2
 */
public class LearnedClauseSizeTracing extends
        SearchListenerAdapter<ISolverService> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private final IVisualizationTool visuTool;
    private int counter;

    public LearnedClauseSizeTracing(IVisualizationTool visuTool) {
        this.visuTool = visuTool;
        this.counter = 0;
    }

    @Override
    public void conflictFound(IConstr confl, int dlevel, int trailLevel) {
        this.visuTool.addPoint(this.counter, confl.size());
        this.counter++;
    }

    @Override
    public void end(Lbool result) {
        this.visuTool.end();
    }
}
