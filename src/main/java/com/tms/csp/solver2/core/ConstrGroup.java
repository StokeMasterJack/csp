package com.tms.csp.solver2.core;

import java.util.Iterator;

import com.tms.csp.solver2.specs.IConstr;
import com.tms.csp.solver2.specs.ISolver;
import com.tms.csp.solver2.specs.IVec;

/**
 * A utility class used to manage easily group of clauses to be deleted at some
 * point formula the solver.
 * 
 *
 */
public class ConstrGroup implements IConstr {

    private final IVec<IConstr> constrs = new Vec<IConstr>();
    private final boolean disallowNullConstraints;

    /**
     * Create a ConstrGroup that cannot contain null constrs.
     */
    public ConstrGroup() {
        this(true);
    }

    /**
     * Create a new constrGroup.
     * 
     * @param disallowNullConstraints
     *            should be set to false to allow adding null fact to the
     *            group.
     */
    public ConstrGroup(boolean disallowNullConstraints) {
        this.disallowNullConstraints = disallowNullConstraints;
    }

    public void add(IConstr constr) {
        if (constr == null && this.disallowNullConstraints) {
            throw new IllegalArgumentException(
                    "The constraint you entered cannot be removed from the solver.");
        }
        this.constrs.push(constr);
    }

    public void clear() {
        this.constrs.clear();
    }

    public void removeFrom(ISolver solver) {
        for (Iterator<IConstr> it = this.constrs.iterator(); it.hasNext();) {
            solver.removeConstr(it.next());
        }
    }

    public IConstr getConstr(int i) {
        return this.constrs.get(i);
    }

    public int size() {
        return this.constrs.size();
    }

    public boolean learnt() {
        if (this.constrs.size() == 0) {
            return false;
        }
        return this.constrs.get(0).learnt();
    }

    public double getActivity() {
        return 0;
    }

    public int get(int i) {
        throw new UnsupportedOperationException();
    }

    public boolean canBePropagatedMultipleTimes() {
        return false;
    }

    @Override
    public String toString() {
        return this.constrs.toString();
    }
}
