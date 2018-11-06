package com.tms.csp.solver2.minisat.orders;

import java.io.PrintStream;
import java.util.LinkedList;

import com.tms.csp.solver2.minisat.core.ILits;
import com.tms.csp.solver2.minisat.core.IOrder;
import com.tms.csp.solver2.minisat.core.IPhaseSelectionStrategy;

/**
 * Uses a tabu list to prevent the solver to
 * 
 * @since 2.3.2
 */
public class TabuListDecorator implements IOrder {

    private final VarOrderHeap decorated;

    private final int tabuSize;

    private ILits voc;
    private int lastVar = -1;

    private final LinkedList<Integer> tabuList = new LinkedList<Integer>();

    public TabuListDecorator(VarOrderHeap order) {
        this(order, 10);
    }

    public TabuListDecorator(VarOrderHeap order, int tabuSize) {
        this.decorated = order;
        this.tabuSize = tabuSize;
    }

    public void assignLiteral(int q) {
        this.decorated.assignLiteral(q);
    }

    public IPhaseSelectionStrategy getPhaseSelectionStrategy() {
        return this.decorated.getPhaseSelectionStrategy();
    }

    public void init() {
        this.decorated.init();
        this.lastVar = -1;
    }

    public void printStat(PrintStream out, String prefix) {
        out.println(prefix + "tabu list size\t: " + this.tabuSize);
        this.decorated.printStat(out, prefix);
    }

    public int select() {
        int lit = this.decorated.select();
        if (lit == ILits.UNDEFINED) {
            int var;
            do {
                if (this.tabuList.isEmpty()) {
                    return ILits.UNDEFINED;
                }
                var = this.tabuList.removeFirst();
            } while (!this.voc.isUnassigned(var << 1));
            return getPhaseSelectionStrategy().select(var);
        }
        this.lastVar = lit >> 1;
        return lit;
    }

    public void setLits(ILits lits) {
        this.decorated.setLits(lits);
        this.voc = lits;
    }

    public void setPhaseSelectionStrategy(IPhaseSelectionStrategy strategy) {
        this.decorated.setPhaseSelectionStrategy(strategy);
    }

    public void setVarDecay(double d) {
        this.decorated.setVarDecay(d);
    }

    public void undo(int x) {
        if (this.tabuList.size() == this.tabuSize) {
            int var = this.tabuList.removeFirst();
            this.decorated.undo(var);
        }
        if (x == this.lastVar) {
            this.tabuList.add(x);
            this.lastVar = -1;
        } else {
            this.decorated.undo(x);
        }
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
        return this.decorated.toString() + " with tabu list of size "
                + this.tabuSize;
    }

    public double[] getVariableHeuristics() {
        return this.decorated.getVariableHeuristics();
    }

}
