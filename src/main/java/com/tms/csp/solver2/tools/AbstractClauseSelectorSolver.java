package com.tms.csp.solver2.tools;

import java.util.Collection;

import com.tms.csp.solver2.core.VecInt;
import com.tms.csp.solver2.specs.ISolver;
import com.tms.csp.solver2.specs.IVecInt;
import com.tms.csp.solver2.specs.IteratorInt;
import com.tms.csp.solver2.specs.TimeoutException;

public abstract class AbstractClauseSelectorSolver<T extends ISolver> extends
        SolverDecorator<T> {

    private static final long serialVersionUID = 1L;
    private int lastCreatedVar;
    private boolean pooledVarId = false;

    private interface SelectorState {

        boolean isSatisfiable(boolean global) throws TimeoutException;

        boolean isSatisfiable() throws TimeoutException;

        boolean isSatisfiable(IVecInt assumps) throws TimeoutException;

        boolean isSatisfiable(IVecInt assumps, boolean global)
                throws TimeoutException;

    }

    private final SelectorState external = new SelectorState() {

        private IVecInt getNegatedSelectors() {
            IVecInt assumps = new VecInt();
            for (int var : getAddedVars()) {
                assumps.push(-var);
            }
            return assumps;
        }

        public boolean isSatisfiable(boolean global) throws TimeoutException {
            return decorated().isSatisfiable(getNegatedSelectors(), global);
        }

        public boolean isSatisfiable(IVecInt assumps, boolean global)
                throws TimeoutException {
            IVecInt all = getNegatedSelectors();
            assumps.copyTo(all);
            return decorated().isSatisfiable(all, global);
        }

        public boolean isSatisfiable() throws TimeoutException {
            return decorated().isSatisfiable(getNegatedSelectors());
        }

        public boolean isSatisfiable(IVecInt assumps) throws TimeoutException {
            IVecInt all = getNegatedSelectors();
            assumps.copyTo(all);
            return decorated().isSatisfiable(all);
        }

    };

    private final SelectorState internal = new SelectorState() {

        public boolean isSatisfiable(boolean global) throws TimeoutException {
            return decorated().isSatisfiable(global);
        }

        public boolean isSatisfiable() throws TimeoutException {
            return decorated().isSatisfiable();
        }

        public boolean isSatisfiable(IVecInt assumps) throws TimeoutException {
            return decorated().isSatisfiable(assumps);
        }

        public boolean isSatisfiable(IVecInt assumps, boolean global)
                throws TimeoutException {
            return decorated().isSatisfiable(assumps, global);
        }
    };

    private SelectorState selectedState = external;

    public AbstractClauseSelectorSolver(T solver) {
        super(solver);
    }

    public abstract Collection<Integer> getAddedVars();

    /**
     * 
     * @param literals
     * @return
     * @since 2.1
     */
    protected int createNewVar(IVecInt literals) {
        for (IteratorInt it = literals.iterator(); it.hasNext();) {
            if (Math.abs(it.next()) > nextFreeVarId(false)) {
                throw new IllegalStateException(
                        "Please call newVar(int) before adding fact!!!");
            }
        }
        if (this.pooledVarId) {
            this.pooledVarId = false;
            return this.lastCreatedVar;
        }
        this.lastCreatedVar = nextFreeVarId(true);
        return this.lastCreatedVar;
    }

    protected void discardLastestVar() {
        this.pooledVarId = true;
    }

    @Override
    public boolean isSatisfiable(boolean global) throws TimeoutException {
        return selectedState.isSatisfiable(global);
    }

    @Override
    public boolean isSatisfiable(IVecInt assumps, boolean global)
            throws TimeoutException {
        return selectedState.isSatisfiable(assumps, global);
    }

    @Override
    public boolean isSatisfiable() throws TimeoutException {
        return selectedState.isSatisfiable();
    }

    @Override
    public boolean isSatisfiable(IVecInt assumps) throws TimeoutException {
        return selectedState.isSatisfiable(assumps);
    }

    /**
     * In the internal state, the solver will allow the selector variables to be
     * satisfied. As such, the solver will answer "true" to isSatisfiable() c
     * an UNSAT problem. it is the responsibility of the user to take into
     * account the meaning of the selector variables.
     */
    public void internalState() {
        this.selectedState = internal;
    }

    /**
     * In external state, the solver will prevent the selector variables to be
     * satisfied. As a consequence, from an external point of view, an UNSAT
     * problem will answer "false" to isSatisfiable().
     */

    public void externalState() {
        this.selectedState = external;
    }
}
