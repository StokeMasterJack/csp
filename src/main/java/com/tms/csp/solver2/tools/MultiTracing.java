package com.tms.csp.solver2.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.tms.csp.solver2.specs.IConstr;
import com.tms.csp.solver2.specs.ISolverService;
import com.tms.csp.solver2.specs.Lbool;
import com.tms.csp.solver2.specs.RandomAccessModel;
import com.tms.csp.solver2.specs.SearchListener;

/**
 * Allow to feed the solver with several SearchListener.
 * 
 * @author leberre
 * 
 */
public class MultiTracing<T extends ISolverService> implements
        SearchListener<T> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private final Collection<SearchListener<T>> listeners = new ArrayList<SearchListener<T>>();

    public MultiTracing(SearchListener<T>... listeners) {
        this.listeners.addAll(Arrays.asList(listeners));
    }

    public MultiTracing(List<SearchListener<T>> listenersList) {
        this.listeners.addAll(listenersList);
    }

    public void assuming(int p) {
        for (SearchListener<T> sl : this.listeners) {
            sl.assuming(p);
        }

    }

    public void propagating(int p, IConstr reason) {
        for (SearchListener<T> sl : this.listeners) {
            sl.propagating(p, reason);
        }

    }

    public void backtracking(int p) {
        for (SearchListener<T> sl : this.listeners) {
            sl.backtracking(p);
        }
    }

    public void adding(int p) {
        for (SearchListener<T> sl : this.listeners) {
            sl.adding(p);
        }

    }

    public void learn(IConstr c) {
        for (SearchListener<T> sl : this.listeners) {
            sl.learn(c);
        }

    }

    public void learnUnit(int p) {
        for (SearchListener<T> sl : this.listeners) {
            sl.learnUnit(p);
        }
    }

    public void delete(int[] clause) {
        for (SearchListener<T> sl : this.listeners) {
            sl.delete(clause);
        }

    }

    public void conflictFound(IConstr confl, int dlevel, int trailLevel) {
        for (SearchListener<T> sl : this.listeners) {
            sl.conflictFound(confl, dlevel, trailLevel);
        }

    }

    public void conflictFound(int p) {
        for (SearchListener<T> sl : this.listeners) {
            sl.conflictFound(p);
        }

    }

    public void solutionFound(int[] model, RandomAccessModel lazyModel) {
        for (SearchListener<T> sl : this.listeners) {
            sl.solutionFound(model, lazyModel);
        }

    }

    public void beginLoop() {
        for (SearchListener<T> sl : this.listeners) {
            sl.beginLoop();
        }
    }

    public void start() {
        for (SearchListener<T> sl : this.listeners) {
            sl.start();
        }

    }

    public void end(Lbool result) {
        for (SearchListener<T> sl : this.listeners) {
            sl.end(result);
        }
    }

    public void restarting() {
        for (SearchListener<T> sl : this.listeners) {
            sl.restarting();
        }

    }

    public void backjump(int backjumpLevel) {
        for (SearchListener<T> sl : this.listeners) {
            sl.backjump(backjumpLevel);
        }

    }

    public void init(T solverService) {
        for (SearchListener<T> sl : this.listeners) {
            sl.init(solverService);
        }
    }

    public void cleaning() {
        for (SearchListener<T> sl : this.listeners) {
            sl.cleaning();
        }
    }

}
