package com.smartsoft.csp.api;

import com.smartsoft.csp.ast.Lit;
import com.smartsoft.csp.ast.Var;
import com.smartsoft.csp.util.Bit;

public class VarState {

    private final Var var;
    private final Bit userValue;
    private final Bit computedValue;

    public VarState(Var var, Bit userValue, Bit computedValue) {
        this.var = var;
        this.userValue = userValue;
        this.computedValue = computedValue;
    }

    public Var getVar() {
        return var;
    }

    public Bit getUserValue() {
        return userValue;
    }

    public Bit getComputedValue() {
        return computedValue;
    }

    @Override
    public String toString() {
        return var + ": " + userValue + "-" + computedValue;
    }

    public Lit toLit() {
        boolean sign;
        if (computedValue.isTrue()) {
            sign = true;
        } else if (computedValue.isFalse()) {
            sign = false;
        } else {
            throw new IllegalStateException();
        }
        return var.mkLit(sign);
    }

    public boolean isTrueStuck() {
        return userValue.isTrue() && computedValue.isTrue();
    }
}
