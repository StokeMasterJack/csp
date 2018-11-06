package com.tms.csp.solver2.minisat.constraints.cnf;

import com.tms.csp.solver2.minisat.core.Constr;
import com.tms.csp.solver2.specs.IVecInt;
import com.tms.csp.solver2.specs.UnitPropagationListener;

/**
 * @since 2.1
 */
public class UnitClauses implements Constr {

    protected final int[] literals;

    public UnitClauses(IVecInt values) {
        this.literals = new int[values.size()];
        values.copyTo(this.literals);
    }

    public void assertConstraint(UnitPropagationListener s) {
        for (int p : this.literals) {
            s.enqueue(p, this);
        }
    }

    public void assertConstraintIfNeeded(UnitPropagationListener s) {
        assertConstraint(s);
    }

    public void calcReason(int p, IVecInt outReason) {
        throw new UnsupportedOperationException();

    }

    public double getActivity() {
        throw new UnsupportedOperationException();
    }

    public void incActivity(double claInc) {
        // silent to prevent problems with xplain trick.
    }

    public void setActivity(double claInc) {
        // do nothing
    }

    public boolean locked() {
        throw new UnsupportedOperationException();
    }

    public void register() {
        throw new UnsupportedOperationException();
    }

    public void remove(UnitPropagationListener upl) {
        for (int i = this.literals.length - 1; i >= 0; i--) {
            upl.unset(this.literals[i]);
        }
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
        throw new UnsupportedOperationException();
    }

    public boolean learnt() {
        throw new UnsupportedOperationException();
    }

    public int size() {
        throw new UnsupportedOperationException();
    }

    public void forwardActivity(double claInc) {
        // silent to prevent problems with xplain trick.
    }

    public boolean canBePropagatedMultipleTimes() {
        return false;
    }

    public void calcReasonOnTheFly(int p, IVecInt trail, IVecInt outReason) {
        calcReason(p, outReason);
    }
}
