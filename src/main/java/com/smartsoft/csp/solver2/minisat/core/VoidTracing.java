package com.smartsoft.csp.solver2.minisat.core;

import com.smartsoft.csp.solver2.specs.IConstr;
import com.smartsoft.csp.solver2.specs.ISolverService;
import com.smartsoft.csp.solver2.specs.Lbool;
import com.smartsoft.csp.solver2.specs.RandomAccessModel;
import com.smartsoft.csp.solver2.specs.SearchListener;

/**
 * Do-nothing search listener. Used by default by the solver when no
 * SearchListener is provided to the solver.
 * 
 * @author leberre
 * 
 */
final class VoidTracing implements SearchListener<ISolverService> {
    private static final long serialVersionUID = 1L;

    public void assuming(int p) {
    }

    public void propagating(int p, IConstr reason) {
    }

    public void backtracking(int p) {
    }

    public void adding(int p) {
    }

    public void learn(IConstr clause) {
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

    public void init(ISolverService solverService) {
    }

    public void cleaning() {
    }
}
