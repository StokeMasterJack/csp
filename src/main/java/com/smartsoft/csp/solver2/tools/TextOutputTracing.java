package com.smartsoft.csp.solver2.tools;

import java.util.Map;

import com.smartsoft.csp.solver2.specs.IConstr;
import com.smartsoft.csp.solver2.specs.ISolverService;
import com.smartsoft.csp.solver2.specs.Lbool;
import com.smartsoft.csp.solver2.specs.RandomAccessModel;
import com.smartsoft.csp.solver2.specs.SearchListener;

/**
 * Debugging Search Listener allowing to follow the search formula a textual way.
 * 
 * @author daniel
 * @since 2.2
 */
public class TextOutputTracing<T> implements SearchListener<ISolverService> {

    private static final long serialVersionUID = 1L;

    private final Map<Integer, T> mapping;

    /**
     * @since 2.1
     */
    public TextOutputTracing(Map<Integer, T> mapping) {
        this.mapping = mapping;
    }

    private String node(int dimacs) {

        if (this.mapping != null) {
            int var = Math.abs(dimacs);
            T t = this.mapping.get(var);
            if (t != null) {
                if (dimacs > 0) {
                    return t.toString();
                }
                return "-" + t.toString();
            }
        }
        return Integer.toString(dimacs);
    }

    public void assuming(int p) {
        System.out.println("assuming " + node(p));
    }

    /**
     * @since 2.1
     */
    public void propagating(int p, IConstr reason) {
        System.out.println("propagating " + node(p));
    }

    public void backtracking(int p) {
        System.out.println("backtracking " + node(p));
    }

    public void adding(int p) {
        System.out.println("adding " + node(p));
    }

    /**
     * @since 2.1
     */
    public void learn(IConstr clause) {
        System.out.println("learning " + clause);

    }

    /**
     * @since 2.3.4
     */
    public void learnUnit(int p) {
        System.out.println("learning unit " + p);

    }

    public void delete(int[] clause) {

    }

    /**
     * @since 2.1
     */
    public void conflictFound(IConstr confl, int dlevel, int trailLevel) {
        System.out.println("conflict ");
    }

    /**
     * @since 2.1
     */
    public void conflictFound(int p) {
        System.out.println("conflict during propagation");
    }

    public void solutionFound(int[] model, RandomAccessModel lazyModel) {
        System.out.println("solution found ");
    }

    public void beginLoop() {
    }

    public void start() {
    }

    /**
     * @since 2.1
     */
    public void end(Lbool result) {
    }

    /**
     * @since 2.2
     */
    public void restarting() {
        System.out.println("restarting ");
    }

    public void backjump(int backjumpLevel) {
        System.out.println("backjumping to decision level " + backjumpLevel);
    }

    /**
     * @since 2.3.2
     */
    public void init(ISolverService solverService) {
    }

    /**
     * @since 2.3.2
     */
    public void cleaning() {
        System.out.println("cleaning");
    }

}
