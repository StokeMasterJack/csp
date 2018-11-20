package com.smartsoft.csp.solver2.tools;

import com.smartsoft.csp.solver2.specs.IConstr;
import com.smartsoft.csp.solver2.specs.ISolverService;
import com.smartsoft.csp.solver2.specs.Lbool;

public class SpeedTracing extends SearchListenerAdapter<ISolverService> {

    private static final long serialVersionUID = 1L;

    private final IVisualizationTool visuTool;
    private final IVisualizationTool cleanVisuTool;
    private final IVisualizationTool restartVisuTool;

    private long begin, end;
    private int counter;
    private long index;

    private double maxY;

    public SpeedTracing(IVisualizationTool visuTool,
            IVisualizationTool cleanVisuTool, IVisualizationTool restartVisuTool) {
        this.visuTool = visuTool;
        this.cleanVisuTool = cleanVisuTool;
        this.restartVisuTool = restartVisuTool;

        visuTool.init();
        cleanVisuTool.init();
        restartVisuTool.init();

        this.begin = System.currentTimeMillis();
        this.counter = 0;
        this.index = 0;
        this.maxY = 0;
    }

    @Override
    public void propagating(int p, IConstr reason) {
        this.end = System.currentTimeMillis();
        double y;
        if (this.end - this.begin >= 2000) {
            long tmp = this.end - this.begin;
            this.index += tmp;
            y = this.counter / tmp * 1000;
            if (y > this.maxY) {
                this.maxY = y;
            }
            this.visuTool.addPoint(this.index / 1000.0, y);
            this.cleanVisuTool.addPoint(this.index / 1000.0, 0);
            this.restartVisuTool.addPoint(this.index / 1000.0, 0);
            this.begin = System.currentTimeMillis();
            this.counter = 0;
        }
        this.counter++;
    }

    @Override
    public void end(Lbool result) {
        this.visuTool.end();
        this.cleanVisuTool.end();
        this.restartVisuTool.end();
    }

    @Override
    public void cleaning() {
        this.end = System.currentTimeMillis();
        long indexClean = this.index + this.end - this.begin;
        this.visuTool.addPoint(indexClean / 1000.0, this.counter
                / (this.end - this.begin) * 1000);
        this.cleanVisuTool.addPoint(indexClean / 1000.0, this.maxY);
        this.restartVisuTool.addInvisiblePoint(indexClean, 0);
    }

    @Override
    public void restarting() {
        this.end = System.currentTimeMillis();
        long indexRestart = this.index + this.end - this.begin;
        double y = this.counter / (this.end - this.begin) * 1000;
        this.visuTool.addPoint(indexRestart / 1000.0, y);
        if (y > this.maxY) {
            this.maxY = y;
        }
        this.restartVisuTool.addPoint(indexRestart / 1000.0, this.maxY);
        this.cleanVisuTool.addInvisiblePoint(indexRestart, 0);
    }

    @Override
    public void start() {
        this.visuTool.init();
        this.cleanVisuTool.init();
        this.restartVisuTool.init();

        this.begin = System.currentTimeMillis();
        this.counter = 0;
        this.index = 0;
    }
}
