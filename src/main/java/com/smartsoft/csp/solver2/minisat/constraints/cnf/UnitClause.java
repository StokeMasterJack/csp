package com.smartsoft.csp.solver2.minisat.constraints.cnf;

import com.smartsoft.csp.solver2.core.LiteralsUtils;
import com.smartsoft.csp.solver2.minisat.core.Constr;
import com.smartsoft.csp.solver2.minisat.core.ILits;
import com.smartsoft.csp.solver2.specs.IVecInt;
import com.smartsoft.csp.solver2.specs.UnitPropagationListener;

/**
 * 
 * @author daniel
 * @since 2.1
 */
public class UnitClause implements Constr {

    protected final int literal;
    protected double activity;

    public UnitClause(int value) {
        this.literal = value;
    }

    public void assertConstraint(UnitPropagationListener s) {
        s.enqueue(this.literal, this);
    }

    public void assertConstraintIfNeeded(UnitPropagationListener s) {
        assertConstraint(s);
    }

    public void calcReason(int p, IVecInt outReason) {
        if (p == ILits.UNDEFINED) {
            outReason.push(LiteralsUtils.neg(this.literal));
        }
    }

    public double getActivity() {
        return activity;
    }

    public void incActivity(double claInc) {
        // silent to prevent problems with xplain trick.
    }

    public void setActivity(double claInc) {
        activity = claInc;
    }

    public boolean locked() {
        throw new UnsupportedOperationException();
    }

    public void register() {
        throw new UnsupportedOperationException();
    }

    public void remove(UnitPropagationListener upl) {
        upl.unset(this.literal);
    }

    public void rescaleBy(double d) {
        throw new UnsupportedOperationException();
    }

    public void setLearnt() {
        throw new UnsupportedOperationException();
    }

    public boolean simplify() {
        return false;
    }

    public boolean propagate(UnitPropagationListener s, int p) {
        throw new UnsupportedOperationException();
    }

    public int get(int i) {
        if (i > 0) {
            throw new IllegalArgumentException();
        }
        return this.literal;
    }

    public boolean learnt() {
        return false;
    }

    public int size() {
        return 1;
    }

    public void forwardActivity(double claInc) {
        // silent to prevent problems with xplain trick.
    }

    @Override
    public String toString() {
        return Lits.toString(this.literal);
    }

    public boolean canBePropagatedMultipleTimes() {
        return false;
    }

    public void calcReasonOnTheFly(int p, IVecInt trail, IVecInt outReason) {
        calcReason(p, outReason);
    }
}
