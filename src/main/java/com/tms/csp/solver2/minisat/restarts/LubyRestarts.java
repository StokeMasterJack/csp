package com.tms.csp.solver2.minisat.restarts;

import com.tms.csp.solver2.minisat.core.Constr;
import com.tms.csp.solver2.minisat.core.RestartStrategy;
import com.tms.csp.solver2.minisat.core.SearchParams;
import com.tms.csp.solver2.minisat.core.SolverStats;

public final class LubyRestarts implements RestartStrategy {

    public static final int DEFAULT_LUBY_FACTOR = 32;

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    // 21-06-2012 back from SAT 2012
    // computing luby values the way presented by Donald Knuth formula his invited
    // talk at the SAT 2012 conference
    // u1
    private int un = 1;
    // v1
    private int vn = 1;

    /**
     * returns the current value of the luby sequence.
     * 
     * @return the current value of the luby sequence.
     */
    public int luby() {
        return this.vn;
    }

    /**
     * Computes and return the next value of the luby sequence. That method has
     * a side effect of the value returned by luby(). luby()!=nextLuby() but
     * nextLuby()==luby().
     * 
     * @return the new current value of the luby sequence.
     * @see #luby()
     */
    public int nextLuby() {
        if ((this.un & -this.un) == this.vn) {
            this.un = this.un + 1;
            this.vn = 1;
        } else {
            this.vn = this.vn << 1;
        }
        return this.vn;
    }

    private int factor;

    private int bound;
    private int conflictcount;

    public LubyRestarts() {
        this(DEFAULT_LUBY_FACTOR); // uses TiniSAT default
    }

    /**
     * @param factor
     *            the factor used for the Luby series.
     * @since 2.1
     */
    public LubyRestarts(int factor) {
        setFactor(factor);
    }

    public void setFactor(int factor) {
        this.factor = factor;
    }

    public int getFactor() {
        return this.factor;
    }

    public void init(SearchParams params, SolverStats stats) {
        this.un = 1;
        this.vn = 1;
        this.bound = luby() * this.factor;
    }

    public long nextRestartNumberOfConflict() {
        return this.bound;
    }

    public void onRestart() {
        this.bound = nextLuby() * this.factor;
        this.conflictcount = 0;
    }

    @Override
    public String toString() {
        return "luby style (SATZ_rand, TiniSAT) restarts strategy with factor "
                + this.factor;
    }

    public boolean shouldRestart() {
        return this.conflictcount >= this.bound;
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
