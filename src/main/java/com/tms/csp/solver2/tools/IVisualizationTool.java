package com.tms.csp.solver2.tools;

import java.io.Serializable;

public interface IVisualizationTool extends Serializable {

    Integer NOTGOOD = Integer.MIN_VALUE;

    void addPoint(double x, double y);

    void addInvisiblePoint(double x, double y);

    void init();

    void end();

}
