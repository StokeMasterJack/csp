package com.smartsoft.csp.solver2.minisat.constraints.cnf;

import com.smartsoft.csp.solver2.minisat.core.ILits;
import com.smartsoft.csp.solver2.specs.IVecInt;

/**
 * 
 * @author daniel
 * @since 2.1
 */
public class LearntBinaryClause extends BinaryClause {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public LearntBinaryClause(IVecInt ps, ILits voc) {
        super(ps, voc);
    }

    public void setLearnt() {
        // do nothing
    }

    public boolean learnt() {
        return true;
    }

    public void forwardActivity(double claInc) {

    }

    /**
     * @param claInc
     */
    public void incActivity(double claInc) {
        this.activity += claInc;
    }

    public void setActivity(double d) {
        this.activity = d;
    }
}
