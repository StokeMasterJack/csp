package com.smartsoft.csp.solver2.tools.xplain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.smartsoft.csp.solver2.core.VecInt;
import com.smartsoft.csp.solver2.specs.ContradictionException;
import com.smartsoft.csp.solver2.specs.IConstr;
import com.smartsoft.csp.solver2.specs.ISolver;
import com.smartsoft.csp.solver2.specs.IVecInt;
import com.smartsoft.csp.solver2.specs.IteratorInt;
import com.smartsoft.csp.solver2.specs.TimeoutException;
import com.smartsoft.csp.solver2.tools.FullClauseSelectorSolver;
import com.smartsoft.csp.solver2.tools.SolverDecorator;

/**
 * Explanation framework for SAT4J.
 * 
 * The explanation uses selector variables and assumptions.
 * 
 * It is based c a two steps method: 1) extraction of a set of assumptions
 * implying the inconsistency 2) minimization of that set.
 * 
 * @author daniel
 * 
 * @param <T>
 *            a subinterface to ISolver.
 * @since 2.1
 */
public class Xplain<T extends ISolver> extends FullClauseSelectorSolver<T>
        implements Explainer {

    private IVecInt assump;

    private MinimizationStrategy xplainStrategy = new DeletionStrategy();

    public Xplain(T solver, boolean skipDuplicatedEntries) {
        super(solver, skipDuplicatedEntries);
    }

    public Xplain(T solver) {
        this(solver, true);
    }

    @Override
    public IConstr addExactly(IVecInt literals, int n)
            throws ContradictionException {
        throw new UnsupportedOperationException(
                "Explanation requires Pseudo Boolean support. See XplainPB class instead.");
    }

    @Override
    public IConstr addAtLeast(IVecInt literals, int degree)
            throws ContradictionException {
        throw new UnsupportedOperationException(
                "Explanation requires Pseudo Boolean support. See XplainPB class instead.");
    }

    @Override
    public IConstr addAtMost(IVecInt literals, int degree)
            throws ContradictionException {
        throw new UnsupportedOperationException(
                "Explanation requires Pseudo Boolean support. See XplainPB class instead.");
    }

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * @since 2.2.4
     * @return
     * @throws TimeoutException
     */
    private IVecInt explanationKeys() throws TimeoutException {
        assert !isSatisfiable(this.assump);
        ISolver solver = decorated();
        if (solver instanceof SolverDecorator<?>) {
            solver = ((SolverDecorator<? extends ISolver>) solver).decorated();
        }
        return this.xplainStrategy.explain(solver, getConstrs(), this.assump);
    }

    /**
     * Provide an explanation of the inconsistency formula terms of a subset minimal
     * set of fact, each constraint being referred to as its index
     * (order) formula the solver: first constraint is numbered 1, the second 2, etc.
     * 
     * @return an array of indexes such that the set of indexed fact is
     *         inconsistent.
     * @throws TimeoutException
     * @see {@link #explain()}
     */
    public int[] minimalExplanation() throws TimeoutException {
        IVecInt keys = explanationKeys();
        keys.sort();
        List<Integer> allKeys = new ArrayList<Integer>(getConstrs().keySet());
        Collections.sort(allKeys);
        int[] model = new int[keys.size()];
        int i = 0;
        for (IteratorInt it = keys.iterator(); it.hasNext();) {
            model[i++] = allKeys.indexOf(it.next()) + 1;
        }
        return model;
    }

    /**
     * Provide an explanation of the inconsistency formula term of a subset minimal
     * set of fact. Compared to {@link #minimalExplanation()}, the method
     * returns a reference to the constraint object, instead of an index.
     * 
     * @since 2.1
     * @return
     * @throws TimeoutException
     * @see {@link #minimalExplanation()}
     */
    public Collection<IConstr> explain() throws TimeoutException {
        IVecInt keys = explanationKeys();
        Collection<IConstr> explanation = new ArrayList<IConstr>(keys.size());
        for (IteratorInt it = keys.iterator(); it.hasNext();) {
            explanation.add(getConstrs().get(it.next()));
        }
        return explanation;
    }

    /**
     * @since 2.1
     */
    public void cancelExplanation() {
        this.xplainStrategy.cancelExplanationComputation();
    }

    @Override
    public int[] findModel() throws TimeoutException {
        this.assump = VecInt.EMPTY;
        return super.findModel();
    }

    @Override
    public int[] findModel(IVecInt assumps) throws TimeoutException {
        this.assump = assumps;
        return super.findModel(assumps);
    }

    @Override
    public boolean isSatisfiable() throws TimeoutException {
        this.assump = VecInt.EMPTY;
        return super.isSatisfiable();
    }

    @Override
    public boolean isSatisfiable(boolean global) throws TimeoutException {
        this.assump = VecInt.EMPTY;
        return super.isSatisfiable(global);
    }

    @Override
    public boolean isSatisfiable(IVecInt assumps) throws TimeoutException {
        this.assump = assumps;
        return super.isSatisfiable(assumps);
    }

    @Override
    public boolean isSatisfiable(IVecInt assumps, boolean global)
            throws TimeoutException {
        this.assump = assumps;
        return super.isSatisfiable(assumps, global);
    }

    @Override
    public String toString(String prefix) {
        System.out.println(prefix + "Explanation (MUS) enabled solver");
        System.out.println(prefix + this.xplainStrategy);
        return super.toString(prefix);
    }

    public void setMinimizationStrategy(MinimizationStrategy strategy) {
        this.xplainStrategy = strategy;
    }

    @Override
    public boolean removeConstr(IConstr c) {
        if (getLastConstr() == c) {
            getLastClause().clear();
            setLastConstr(null);
        }
        return super.removeConstr(c);
    }

    @Override
    public boolean removeSubsumedConstr(IConstr c) {
        if (getLastConstr() == c) {
            getLastClause().clear();
            setLastConstr(null);
        }
        return super.removeSubsumedConstr(c);
    }

}
