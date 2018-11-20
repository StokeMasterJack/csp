package com.smartsoft.csp.solver2.tools;

import com.smartsoft.csp.solver2.specs.ISolverService;
import com.smartsoft.csp.solver2.specs.Lbool;

/**
 * @since 2.2
 */
public class DecisionTracing extends SearchListenerAdapter<ISolverService> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private int counter;

    private final IVisualizationTool positiveVisu;
    private final IVisualizationTool negativeVisu;
    private final IVisualizationTool restartVisu;
    private final IVisualizationTool cleanVisu;

    private int nVar;

    public DecisionTracing(IVisualizationTool positiveVisu,
            IVisualizationTool negativeVisu, IVisualizationTool restartVisu,
            IVisualizationTool cleanVisu) {
        this.positiveVisu = positiveVisu;
        this.negativeVisu = negativeVisu;
        this.restartVisu = restartVisu;
        this.cleanVisu = cleanVisu;

        this.counter = 1;
    }

    @Override
    public void assuming(int p) {
        if (p > 0) {
            this.positiveVisu.addPoint(this.counter, p);
            this.negativeVisu.addInvisiblePoint(this.counter, 0);
        } else {
            this.negativeVisu.addPoint(this.counter, -p);
            this.positiveVisu.addInvisiblePoint(this.counter, 0);
        }
        this.restartVisu.addInvisiblePoint(this.counter, 0);
        this.cleanVisu.addInvisiblePoint(this.counter, 0);
        this.counter++;
    }

    @Override
    public void restarting() {
        this.restartVisu.addPoint(this.counter, this.nVar);
        this.cleanVisu.addPoint(this.counter, 0);
        this.positiveVisu.addInvisiblePoint(this.counter, 0);
        this.negativeVisu.addInvisiblePoint(this.counter, 0);
    }

    @Override
    public void end(Lbool result) {
        this.positiveVisu.end();
        this.negativeVisu.end();
        this.restartVisu.end();
        this.cleanVisu.end();
    }

    @Override
    public void start() {
        this.counter = 1;
    }

    @Override
    public void init(ISolverService solverService) {
        this.nVar = solverService.nVars();
        this.positiveVisu.init();
        this.negativeVisu.init();
        this.restartVisu.init();
        this.cleanVisu.init();
    }

    @Override
    public void cleaning() {
        this.restartVisu.addPoint(this.counter, 0);
        this.cleanVisu.addPoint(this.counter, this.nVar);
        this.positiveVisu.addInvisiblePoint(this.counter, 0);
        this.negativeVisu.addInvisiblePoint(this.counter, 0);
    }

}
