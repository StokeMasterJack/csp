package com.smartsoft.csp.trail.node;

import com.smartsoft.csp.ast.Exp;
import com.smartsoft.csp.ast.Space;

import java.util.ArrayList;

public abstract class AbstractNode implements Node {

    private final ArrayList<Exp> inferences = new ArrayList<Exp>();

    @Override
    public int getPhase() {
        return 1;
    }

    @Override
    public boolean isPhase1() {
        return getPhase() == 1;
    }

    @Override
    public boolean isPhase2() {
        return getPhase() == 2;
    }

    @Override
    public Space getSpace() {
        throw new UnsupportedOperationException();
//        return getLit().getSpace();
    }

    @Override
    public void recordInference(Exp inference) {
        inferences.add(inference);
    }

}
