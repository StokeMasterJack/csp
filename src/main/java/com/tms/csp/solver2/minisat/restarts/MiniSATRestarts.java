package com.tms.csp.solver2.minisat.restarts;

import com.tms.csp.solver2.minisat.core.Constr;
import com.tms.csp.solver2.minisat.core.RestartStrategy;
import com.tms.csp.solver2.minisat.core.SearchParams;
import com.tms.csp.solver2.minisat.core.SolverStats;

/**
 * Minisat original restart strategy.
 */
public final class MiniSATRestarts implements RestartStrategy {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private double nofConflicts;

    private SearchParams params;

    private int conflictcount;

    public void init(SearchParams theParams, SolverStats stats) {
        this.params = theParams;
        this.nofConflicts = theParams.getInitConflictBound();
        this.conflictcount = 0;
    }

    public long nextRestartNumberOfConflict() {
        return Math.round(this.nofConflicts);
    }

    public void onRestart() {
        this.nofConflicts *= this.params.getConflictBoundIncFactor();
    }

    @Override
    public String toString() {
        return "MiniSAT restarts strategy";
    }

    public boolean shouldRestart() {
        return this.conflictcount >= this.nofConflicts;
    }

    public void onBackjumpToRootLevel() {
        this.conflictcount = 0;
    }

    public void reset() {
        this.conflictcount = 0;
    }

    public void newConflict() {
        this.conflictcount++;
    }

    public void newLearnedClause(Constr learned, int trailLevel) {
    }
}
