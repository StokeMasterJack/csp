package com.smartsoft.csp.solver2.minisat.restarts;

import com.smartsoft.csp.solver2.minisat.core.Constr;
import com.smartsoft.csp.solver2.minisat.core.RestartStrategy;
import com.smartsoft.csp.solver2.minisat.core.SearchParams;
import com.smartsoft.csp.solver2.minisat.core.SolverStats;

/**
 * Rapid restart strategy presented by Armin Biere during it's SAT 07 invited
 * talk.
 * 
 * @author leberre
 * 
 */
public final class ArminRestarts implements RestartStrategy {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private double inner, outer;
    private long conflicts;
    private SearchParams params;

    private long conflictcount = 0;

    public void init(SearchParams theParams, SolverStats stats) {
        this.params = theParams;
        this.inner = theParams.getInitConflictBound();
        this.outer = theParams.getInitConflictBound();
        this.conflicts = Math.round(this.inner);
    }

    public long nextRestartNumberOfConflict() {
        return this.conflicts;
    }

    public void onRestart() {
        if (this.inner >= this.outer) {
            this.outer *= this.params.getConflictBoundIncFactor();
            this.inner = this.params.getInitConflictBound();
        } else {
            this.inner *= this.params.getConflictBoundIncFactor();
        }
        this.conflicts = Math.round(this.inner);
        this.conflictcount = 0;
    }

    @Override
    public String toString() {
        return "Armin Biere (Picosat) restarts strategy";
    }

    public boolean shouldRestart() {
        return this.conflictcount >= this.conflicts;
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
