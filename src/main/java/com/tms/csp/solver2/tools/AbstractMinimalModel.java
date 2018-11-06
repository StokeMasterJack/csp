package com.tms.csp.solver2.tools;

import java.util.SortedSet;
import java.util.TreeSet;

import com.tms.csp.solver2.core.VecInt;
import com.tms.csp.solver2.specs.ISolver;
import com.tms.csp.solver2.specs.IVecInt;
import com.tms.csp.solver2.specs.IteratorInt;

public class AbstractMinimalModel extends SolverDecorator<ISolver> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    protected final SortedSet<Integer> pLiterals;
    protected final SolutionFoundListener modelListener;

    public static IVecInt positiveLiterals(ISolver solver) {
        IVecInt literals = new VecInt(solver.nVars());
        for (int i = 1; i <= solver.nVars(); i++) {
            literals.push(i);
        }
        return literals;
    }

    public static IVecInt negativeLiterals(ISolver solver) {
        IVecInt literals = new VecInt(solver.nVars());
        for (int i = 1; i <= solver.nVars(); i++) {
            literals.push(-i);
        }
        return literals;
    }

    public AbstractMinimalModel(ISolver solver) {
        this(solver, SolutionFoundListener.VOID);
    }

    public AbstractMinimalModel(ISolver solver, IVecInt p) {
        this(solver, p, SolutionFoundListener.VOID);
    }

    public AbstractMinimalModel(ISolver solver,
            SolutionFoundListener modelListener) {
        this(solver, negativeLiterals(solver), modelListener);
    }

    public AbstractMinimalModel(ISolver solver, IVecInt p,
            SolutionFoundListener modelListener) {
        super(solver);
        this.pLiterals = new TreeSet<Integer>();
        for (IteratorInt it = p.iterator(); it.hasNext();) {
            this.pLiterals.add(it.next());
        }
        this.modelListener = modelListener;

    }

}
