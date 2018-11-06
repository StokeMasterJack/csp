package com.tms.csp.util;

import com.tms.csp.ast.Exp;
import com.tms.csp.ast.Var;

import static com.google.common.base.Preconditions.checkArgument;

public class SingleVarPickEvalContext implements EvalContext {

    private final Exp pic;

    public SingleVarPickEvalContext(Exp singleVarPic) {
        checkArgument(singleVarPic.isPosLit() || singleVarPic.isNegLit());
        this.pic = singleVarPic;
    }

    @Override
    public boolean isTrue(Var vr) {
        return isAssigned(vr) && pic.sign();
    }

    @Override
    public boolean isFalse(Var vr) {
        return isAssigned(vr) && !pic.sign();
    }

    @Override
    public boolean isOpen(Var vr) {
        Var picVar = pic.getVr();

        boolean eq = picVar.equals(vr);

        boolean retVal = !eq;

        return retVal;
    }

    public boolean isAssigned(Var var) {
        Var picVar = pic.getVr();
        boolean eq = picVar.equals(var);
        boolean retVal = eq;
        return retVal;
    }

    public Exp getLit() {
        return pic;
    }

    @Override
    public String toString() {
        return pic.toString();
    }
}
