package com.tms.csp.solver2.tools;

import com.tms.csp.solver2.specs.IConstr;
import com.tms.csp.solver2.specs.ISolverService;
import com.tms.csp.solver2.specs.Lbool;

public class ConflictLevelTracing extends SearchListenerAdapter<ISolverService> {

    /**
	 * 
	 */
    private int counter;

    private static final long serialVersionUID = 1L;

    private int nVar;
    private int maxDLevel;

    private final IVisualizationTool visuTool;
    private final IVisualizationTool restartVisuTool;
    private final IVisualizationTool cleanTool;

    public ConflictLevelTracing(IVisualizationTool visuTool,
            IVisualizationTool restartVisuTool, IVisualizationTool cleanTool) {
        this.visuTool = visuTool;
        this.restartVisuTool = restartVisuTool;
        this.cleanTool = cleanTool;

        this.counter = 1;
        this.maxDLevel = 0;
    }

    @Override
    public void conflictFound(IConstr confl, int dlevel, int trailLevel) {
        if (dlevel > this.maxDLevel) {
            this.maxDLevel = dlevel;
        }
        this.visuTool.addPoint(this.counter, dlevel);
        this.restartVisuTool.addInvisiblePoint(this.counter, this.maxDLevel);
        this.cleanTool.addInvisiblePoint(this.counter, this.maxDLevel);
        this.counter++;
    }

    @Override
    public void restarting() {
        this.restartVisuTool.addPoint(this.counter, this.maxDLevel);
        this.cleanTool.addPoint(this.counter, 0);
        this.visuTool.addInvisiblePoint(this.counter, this.nVar);
    }

    @Override
    public void end(Lbool result) {
        this.visuTool.end();
        this.cleanTool.end();
        this.restartVisuTool.end();
    }

    @Override
    public void start() {
        this.visuTool.init();
        this.restartVisuTool.init();
        this.cleanTool.init();
        this.counter = 1;
        this.maxDLevel = 0;
    }

    @Override
    public void init(ISolverService solverService) {
        this.nVar = solverService.nVars();
    }

    @Override
    public void cleaning() {
        this.restartVisuTool.addPoint(this.counter, 0);
        this.cleanTool.addPoint(this.counter, this.maxDLevel);
        this.visuTool.addInvisiblePoint(this.counter, this.nVar);
    }
}
