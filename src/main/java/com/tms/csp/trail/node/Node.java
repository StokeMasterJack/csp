package com.tms.csp.trail.node;

import com.tms.csp.ast.Exp;
import com.tms.csp.ast.Space;
import com.tms.csp.trail.levels.Level;

public interface Node {

    Level getLevel();

    Exp getLit();

    Exp getExp();

    boolean sign();

    int getPhase();

    boolean isPhase1();

    boolean isPhase2();

    Node getParent();

    Space getSpace();

    boolean isAbsoluteRootNode();

    boolean isLevelRootNode();

    void recordInference(Exp inference);
}
