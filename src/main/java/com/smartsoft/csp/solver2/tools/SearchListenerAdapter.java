package com.smartsoft.csp.solver2.tools;

import com.smartsoft.csp.solver2.specs.IConstr;
import com.smartsoft.csp.solver2.specs.ISolverService;
import com.smartsoft.csp.solver2.specs.Lbool;
import com.smartsoft.csp.solver2.specs.RandomAccessModel;
import com.smartsoft.csp.solver2.specs.SearchListener;

public abstract class SearchListenerAdapter<S extends ISolverService>
        implements SearchListener<S> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public void init(S solverService) {
    }

    public void assuming(int p) {
    }

    public void propagating(int p, IConstr reason) {
    }

    public void backtracking(int p) {
    }

    public void adding(int p) {
    }

    public void learn(IConstr c) {
    }

    public void learnUnit(int p) {
    }

    public void delete(int[] clause) {
    }

    public void conflictFound(IConstr confl, int dlevel, int trailLevel) {
    }

    public void conflictFound(int p) {
    }

    public void solutionFound(int[] model, RandomAccessModel lazyModel) {
    }

    public void beginLoop() {
    }

    public void start() {
    }

    public void end(Lbool result) {
    }

    public void restarting() {
    }

    public void backjump(int backjumpLevel) {
    }

    public void cleaning() {
    }

}
