package com.tms.csp.solver2.minisat.restarts;

import com.tms.csp.solver2.minisat.core.Constr;
import com.tms.csp.solver2.minisat.core.RestartStrategy;
import com.tms.csp.solver2.minisat.core.SearchParams;
import com.tms.csp.solver2.minisat.core.SolverStats;

public final class NoRestarts implements RestartStrategy {

    private static final long serialVersionUID = 1L;

    public void init(SearchParams params, SolverStats stats) {
    }

    public long nextRestartNumberOfConflict() {
        return Long.MAX_VALUE;
    }

    public void onRestart() {
        // do nothing
    }

    public void reset() {
        // do nothing
    }

    public void newConflict() {
        // do nothing
    }

    public boolean shouldRestart() {
        return false;
    }

    public void onBackjumpToRootLevel() {
        // do nothing
    }

    @Override
    public String toString() {
        return "NoRestarts";
    }

    public void newLearnedClause(Constr learned, int trailLevel) {
    }

}
