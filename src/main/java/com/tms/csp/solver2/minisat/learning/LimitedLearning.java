package com.tms.csp.solver2.minisat.learning;

import com.tms.csp.solver2.minisat.core.Constr;
import com.tms.csp.solver2.minisat.core.DataStructureFactory;
import com.tms.csp.solver2.minisat.core.ILits;
import com.tms.csp.solver2.minisat.core.LearningStrategy;
import com.tms.csp.solver2.minisat.core.Solver;
import com.tms.csp.solver2.minisat.core.SolverStats;
import com.tms.csp.solver2.minisat.core.VarActivityListener;

/**
 * Learn only clauses which size is smaller than a percentage of the number of
 * variables.
 * 
 * @author leberre
 */
public abstract class LimitedLearning<D extends DataStructureFactory>
        implements LearningStrategy<D> {

    private static final long serialVersionUID = 1L;

    private final NoLearningButHeuristics<D> none;

    private final MiniSATLearning<D> all;

    protected ILits lits;

    private SolverStats stats;

    public LimitedLearning() {
        this.none = new NoLearningButHeuristics<D>();
        this.all = new MiniSATLearning<D>();
    }

    public void setSolver(Solver<D> s) {
        if (s != null) {
            this.lits = s.getVocabulary();
            setVarActivityListener(s);
            this.all.setDataStructureFactory(s.getDSFactory());
            this.stats = s.getStats();
        }
    }

    public void learns(Constr constr) {
        if (learningCondition(constr)) {
            this.all.learns(constr);
        } else {
            this.none.learns(constr);
            this.stats.ignoredclauses++;
        }
    }

    protected abstract boolean learningCondition(Constr constr);

    public void init() {
        this.all.init();
        this.none.init();
    }

    public void setVarActivityListener(VarActivityListener s) {
        this.none.setVarActivityListener(s);
        this.all.setVarActivityListener(s);
    }
}
