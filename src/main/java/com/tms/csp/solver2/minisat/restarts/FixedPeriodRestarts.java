package com.tms.csp.solver2.minisat.restarts;

import com.tms.csp.solver2.minisat.core.Constr;
import com.tms.csp.solver2.minisat.core.RestartStrategy;
import com.tms.csp.solver2.minisat.core.SearchParams;
import com.tms.csp.solver2.minisat.core.SolverStats;

public class FixedPeriodRestarts implements RestartStrategy {


    private static final long serialVersionUID = 1L;

    private long conflictCount;

    private long period;

    public void reset() {
        conflictCount = 0;
    }

    public void newConflict() {
        conflictCount++;
    }

    public void init(SearchParams params, SolverStats stats) {
        this.conflictCount = 0;
    }

    @Deprecated
    public long nextRestartNumberOfConflict() {
        return period;
    }

    public boolean shouldRestart() {
        return conflictCount >= period;
    }

    public void onRestart() {
        this.conflictCount = 0;
    }

    public void onBackjumpToRootLevel() {
    }

    public void newLearnedClause(Constr learned, int trailLevel) {
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    @Override
    public String toString() {
        return "constant restarts strategy every " + this.period + " conflicts";
    }
}
