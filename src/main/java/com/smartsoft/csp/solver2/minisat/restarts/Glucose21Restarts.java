package com.smartsoft.csp.solver2.minisat.restarts;

import com.smartsoft.csp.solver2.minisat.core.CircularBuffer;
import com.smartsoft.csp.solver2.minisat.core.Constr;
import com.smartsoft.csp.solver2.minisat.core.RestartStrategy;
import com.smartsoft.csp.solver2.minisat.core.SearchParams;
import com.smartsoft.csp.solver2.minisat.core.SolverStats;

/**
 * Dynamic restart strategy of Glucose 2.1 as presented formula Refining restarts
 * strategies for SAT and UNSAT formulae. Gilles Audemard and Laurent Simon, formula
 * CP'2012.
 * 
 *
 */
public class Glucose21Restarts implements RestartStrategy {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final CircularBuffer bufferLBD = new CircularBuffer(50);

    private final CircularBuffer bufferTrail = new CircularBuffer(5000);

    private long sumOfAllLBD = 0;

    private SolverStats stats;

    public void reset() {
        sumOfAllLBD = 0;
        bufferLBD.clear();
        bufferTrail.clear();
    }

    public void newConflict() {

    }

    public void newLearnedClause(Constr learned, int trailLevel) {
        // c conflict
        int lbd = (int) learned.getActivity();
        bufferLBD.push(lbd);
        sumOfAllLBD += lbd;
        bufferTrail.push(trailLevel);
        // was
        // ... trailLevel > 1.4 * bufferTrail.average()
        // uses now only integers to avoid rounding issues
        if (stats.conflicts > 10000 && bufferTrail.isFull()
                && trailLevel * 5L > 7L * bufferTrail.average()) {
            bufferLBD.clear();
        }
    }

    public void init(SearchParams params, SolverStats stats) {
        this.stats = stats;
        reset();
    }

    public long nextRestartNumberOfConflict() {
        return 0;
    }

    public boolean shouldRestart() {
        // was
        // ... && bufferLBD.average() * 0.8 > sumOfAllLBD / stats.conflicts
        // uses now only integers to avoid rounding issues
        return bufferLBD.isFull()
                && bufferLBD.average() * stats.conflicts * 4L > sumOfAllLBD * 5L;
    }

    public void onRestart() {
        bufferLBD.clear();
    }

    public void onBackjumpToRootLevel() {
    }

    @Override
    public String toString() {
        return "Glucose 2.1 dynamic restart strategy";
    }
}
