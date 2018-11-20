package com.smartsoft.csp.solver2.tools;

import com.smartsoft.csp.solver2.specs.IConstr;
import com.smartsoft.csp.solver2.specs.ISolverService;
import com.smartsoft.csp.solver2.specs.Lbool;

/**
 * @since 2.2
 */
public class ConflictDepthTracing extends SearchListenerAdapter<ISolverService> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private int counter;
    private int nVar;

    private final IVisualizationTool conflictDepthVisu;
    private final IVisualizationTool conflictDepthRestartVisu;
    private final IVisualizationTool conflictDepthCleanVisu;

    public ConflictDepthTracing(IVisualizationTool conflictDepthVisu,
            IVisualizationTool conflictDepthRestartVisu,
            IVisualizationTool conflictDepthCleanVisu) {
        this.conflictDepthVisu = conflictDepthVisu;
        this.conflictDepthRestartVisu = conflictDepthRestartVisu;
        this.conflictDepthCleanVisu = conflictDepthCleanVisu;
        this.counter = 0;
    }

    @Override
    public void conflictFound(IConstr confl, int dlevel, int trailLevel) {
        this.conflictDepthVisu.addPoint(this.counter, trailLevel);
        this.conflictDepthRestartVisu.addInvisiblePoint(this.counter,
                trailLevel);
        this.conflictDepthCleanVisu.addInvisiblePoint(this.counter, trailLevel);
        this.counter++;
    }

    @Override
    public void end(Lbool result) {
        this.conflictDepthVisu.end();
        this.conflictDepthRestartVisu.end();
        this.conflictDepthCleanVisu.end();
    }

    @Override
    public void start() {
        this.conflictDepthVisu.init();
        this.conflictDepthRestartVisu.init();
        this.conflictDepthCleanVisu.init();
        this.counter = 0;
    }

    @Override
    public void restarting() {
        this.conflictDepthRestartVisu.addPoint(this.counter, this.nVar);
        this.conflictDepthCleanVisu.addPoint(this.counter, 0);
        this.conflictDepthVisu.addInvisiblePoint(this.counter, this.nVar);
    }

    @Override
    public void init(ISolverService solverService) {
        this.nVar = solverService.nVars();
    }

    @Override
    public void cleaning() {
        this.conflictDepthRestartVisu.addPoint(this.counter, 0);
        this.conflictDepthCleanVisu.addPoint(this.counter, this.nVar);
        this.conflictDepthVisu.addInvisiblePoint(this.counter, this.nVar);
    }
}
