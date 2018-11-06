package com.tms.csp.util;

import com.tms.csp.ast.Var;

public abstract class AbstractEvalContext implements EvalContext {

    abstract public Tri getValue(Var var);

    @Override
    public boolean isTrue(Var vr) {
        return getValue(vr).isTrue();
    }

    @Override
    public boolean isFalse(Var vr) {
        return getValue(vr).isFalse();
    }

    @Override
    public boolean isOpen(Var vr) {
        return getValue(vr).isOpen();
    }


}
