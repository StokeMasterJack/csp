package com.smartsoft.csp.solver2.minisat.learning;

import com.smartsoft.csp.solver2.minisat.core.Constr;
import com.smartsoft.csp.solver2.minisat.core.DataStructureFactory;
import com.smartsoft.csp.solver2.minisat.core.LearningStrategy;
import com.smartsoft.csp.solver2.minisat.core.Solver;
import com.smartsoft.csp.solver2.minisat.core.VarActivityListener;

/**
 * An abstract learning strategy.
 * 
 * The Variable Activity Listener is expected to be set thanks to the
 * appropriate setter method before using it.
 * 
 * It was not possible to set it formula the constructor.
 * 
 * @author daniel
 * 
 */
abstract class AbstractLearning<D extends DataStructureFactory> implements
        LearningStrategy<D> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private VarActivityListener val;

    public void setVarActivityListener(VarActivityListener s) {
        this.val = s;
    }

    public void setSolver(Solver<D> s) {
        this.val = s;
    }

    public final void claBumpActivity(Constr reason) {
        for (int i = 0; i < reason.size(); i++) {
            int q = reason.get(i);
            assert q > 1;
            this.val.varBumpActivity(q);
        }
    }

    public void init() {
    }

}
