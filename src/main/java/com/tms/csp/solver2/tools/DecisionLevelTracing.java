package com.tms.csp.solver2.tools;

import com.tms.csp.solver2.specs.IConstr;
import com.tms.csp.solver2.specs.ISolverService;
import com.tms.csp.solver2.specs.Lbool;

/**
 * @since 2.2
 */
public class DecisionLevelTracing extends SearchListenerAdapter<ISolverService> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private int counter;

    private final IVisualizationTool visuTool;

    public DecisionLevelTracing(IVisualizationTool visuTool) {
        this.visuTool = visuTool;
        visuTool.init();
        this.counter = 0;
    }

    @Override
    public void conflictFound(IConstr confl, int dlevel, int trailLevel) {
        this.counter++;
    }

    @Override
    public void end(Lbool result) {
        this.visuTool.end();
    }

    @Override
    public void start() {
        this.visuTool.init();
    }

    @Override
    public void backjump(int backjumpLevel) {
        this.visuTool.addPoint(this.counter, backjumpLevel);
    }

}
