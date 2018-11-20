package com.smartsoft.csp.solver2.minisat.orders;

import com.smartsoft.csp.solver2.minisat.core.ILits;
import com.smartsoft.csp.solver2.minisat.core.IOrder;
import com.smartsoft.csp.solver2.minisat.core.IPhaseSelectionStrategy;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Random;

/**
 * @since 2.2
 */
public class RandomWalkDecorator implements IOrder, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final VarOrderHeap decorated;

    private double p;

    private static final Random RAND = new Random(123456789);
    private ILits voc;
    private int nbRandomWalks;

    public RandomWalkDecorator(VarOrderHeap order) {
        this(order, 0.01);
    }

    public RandomWalkDecorator(VarOrderHeap order, double p) {
        this.decorated = order;
        this.p = p;
    }

    public void assignLiteral(int q) {
        this.decorated.assignLiteral(q);
    }

    public IPhaseSelectionStrategy getPhaseSelectionStrategy() {
        return this.decorated.getPhaseSelectionStrategy();
    }

    public double getProbability() {
        return this.p;
    }

    public void setProbability(double p) {
        this.p = p;
    }

    public void init() {
        this.decorated.init();
    }

    public void printStat(PrintStream out, String prefix) {
        out.println(prefix + "random walks\t: " + this.nbRandomWalks);
        this.decorated.printStat(out, prefix);
    }

    public int select() {
        if (RAND.nextDouble() < this.p) {
            int var, lit, max;

            while (!this.decorated.heap.empty()) {
                max = this.decorated.heap.size();
                var = this.decorated.heap.get(RAND.nextInt(max) + 1);
                lit = getPhaseSelectionStrategy().select(var);
                if (this.voc.isUnassigned(lit)) {
                    this.nbRandomWalks++;
                    return lit;
                }
            }
        }
        return this.decorated.select();
    }

    public void setLits(ILits lits) {
        this.decorated.setLits(lits);
        this.voc = lits;
        this.nbRandomWalks = 0;
    }

    public void setPhaseSelectionStrategy(IPhaseSelectionStrategy strategy) {
        this.decorated.setPhaseSelectionStrategy(strategy);
    }

    public void setVarDecay(double d) {
        this.decorated.setVarDecay(d);
    }

    public void undo(int x) {
        this.decorated.undo(x);
    }

    public void updateVar(int q) {
        this.decorated.updateVar(q);
    }

    public double varActivity(int q) {
        return this.decorated.varActivity(q);
    }

    public void varDecayActivity() {
        this.decorated.varDecayActivity();
    }

    public void updateVarAtDecisionLevel(int q) {
        this.decorated.updateVarAtDecisionLevel(q);
    }

    @Override
    public String toString() {
        return this.decorated.toString() + " with random walks " + this.p;
    }

    public double[] getVariableHeuristics() {
        return this.decorated.getVariableHeuristics();
    }

}
