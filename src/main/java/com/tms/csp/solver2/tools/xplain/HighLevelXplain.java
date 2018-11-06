package com.tms.csp.solver2.tools.xplain;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import com.tms.csp.solver2.core.VecInt;
import com.tms.csp.solver2.specs.ContradictionException;
import com.tms.csp.solver2.specs.IConstr;
import com.tms.csp.solver2.specs.ISolver;
import com.tms.csp.solver2.specs.IVecInt;
import com.tms.csp.solver2.specs.IteratorInt;
import com.tms.csp.solver2.specs.TimeoutException;
import com.tms.csp.solver2.tools.GroupClauseSelectorSolver;
import com.tms.csp.solver2.tools.SolverDecorator;

/**
 * Computation of MUS formula a structured CNF, i.e. the clauses belong to
 * components, the explanation is to be extracted formula terms of components.
 * 
 * @author daniel
 * 
 * @param <T>
 *            a subinterface to ISolver.
 * @since 2.1
 */
public class HighLevelXplain<T extends ISolver> extends
        GroupClauseSelectorSolver<T> implements Explainer {

    private IVecInt assump;

    private MinimizationStrategy xplainStrategy = new DeletionStrategy();

    public HighLevelXplain(T solver) {
        super(solver);
    }

    @Override
    public IConstr addAtLeast(IVecInt literals, int degree)
            throws ContradictionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IConstr addAtMost(IVecInt literals, int degree)
            throws ContradictionException {
        throw new UnsupportedOperationException();
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
        return this.xplainStrategy.explain(solver, getVarToHighLevel(),
                this.assump);
    }

    public int[] minimalExplanation() throws TimeoutException {
        Collection<Integer> components = explain();
        int[] model = new int[components.size()];
        int i = 0;
        for (int c : components) {
            model[i++] = c;
        }
        Arrays.sort(model);
        return model;
    }

    /**
     * @since 2.1
     * @return
     * @throws TimeoutException
     */
    public Collection<Integer> explain() throws TimeoutException {
        IVecInt keys = explanationKeys();
        Collection<Integer> explanation = new HashSet<Integer>(keys.size());
        for (IteratorInt it = keys.iterator(); it.hasNext();) {
            explanation.add(getVarToHighLevel().get(it.next()));
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
        System.out.println(prefix
                + "High Level Explanation (MUS) enabled solver");
        System.out.println(prefix + this.xplainStrategy);
        return super.toString(prefix);
    }

    public void setMinimizationStrategy(MinimizationStrategy strategy) {
        this.xplainStrategy = strategy;
    }

}
