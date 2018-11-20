package com.smartsoft.csp.solver2.minisat.constraints;

import java.io.Serializable;

import com.smartsoft.csp.solver2.core.Vec;
import com.smartsoft.csp.solver2.minisat.core.Constr;
import com.smartsoft.csp.solver2.minisat.core.DataStructureFactory;
import com.smartsoft.csp.solver2.minisat.core.ILits;
import com.smartsoft.csp.solver2.minisat.core.Learner;
import com.smartsoft.csp.solver2.minisat.core.Propagatable;
import com.smartsoft.csp.solver2.specs.ContradictionException;
import com.smartsoft.csp.solver2.specs.IVec;
import com.smartsoft.csp.solver2.specs.IVecInt;
import com.smartsoft.csp.solver2.specs.UnitPropagationListener;

/**
 * @author leberre To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class AbstractDataStructureFactory implements
        DataStructureFactory, Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.sat4j.minisat.core.DataStructureFactory#conflictDetectedInWatchesFor
     * (int)
     */
    public void conflictDetectedInWatchesFor(int p, int i) {
        for (int j = i + 1; j < this.tmp.size(); j++) {
            this.lits.watch(p, this.tmp.get(j));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sat4j.minisat.core.DataStructureFactory#getWatchesFor(int)
     */
    public IVec<Propagatable> getWatchesFor(int p) {
        this.tmp.clear();
        this.lits.watches(p).moveTo(this.tmp);
        return this.tmp;
    }

    protected ILits lits;

    protected AbstractDataStructureFactory() {
        this.lits = createLits();
    }

    protected abstract ILits createLits();

    private final IVec<Propagatable> tmp = new Vec<Propagatable>();

    /*
     * (non-Javadoc)
     * 
     * @see org.sat4j.minisat.DataStructureFactory#createVocabulary()
     */
    public ILits getVocabulary() {
        return this.lits;
    }

    protected UnitPropagationListener solver;

    protected Learner learner;

    public void setUnitPropagationListener(UnitPropagationListener s) {
        this.solver = s;
    }

    public void setLearner(Learner learner) {
        this.learner = learner;
    }

    public void reset() {
    }

    public void learnConstraint(Constr constr) {
        this.learner.learn(constr);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.sat4j.minisat.core.DataStructureFactory#createCardinalityConstraint
     * (org.sat4j.specs.VecInt, int)
     */
    public Constr createCardinalityConstraint(IVecInt literals, int degree)
            throws ContradictionException {
        throw new UnsupportedOperationException();
    }

    public Constr createUnregisteredCardinalityConstraint(IVecInt literals,
            int degree) {
        throw new UnsupportedOperationException();
    }
}
