package com.tms.csp.solver2.tools;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.tms.csp.solver2.core.VecInt;
import com.tms.csp.solver2.specs.ContradictionException;
import com.tms.csp.solver2.specs.IConstr;
import com.tms.csp.solver2.specs.ISolver;
import com.tms.csp.solver2.specs.IVecInt;

public class FullClauseSelectorSolver<T extends ISolver> extends
        AbstractClauseSelectorSolver<T> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final Map<Integer, IConstr> constrs = new HashMap<Integer, IConstr>();
    private final IVecInt lastClause = new VecInt();
    private IConstr lastConstr;
    private final boolean skipDuplicatedEntries;

    public FullClauseSelectorSolver(T solver, boolean skipDuplicatedEntries) {
        super(solver);
        this.skipDuplicatedEntries = skipDuplicatedEntries;
    }

    public IConstr addControlableClause(IVecInt literals)
            throws ContradictionException {
        if (this.skipDuplicatedEntries) {
            if (literals.equals(this.lastClause)) {
                return null;
            }
            this.lastClause.clear();
            literals.copyTo(this.lastClause);
        }
        int newvar = createNewVar(literals);
        literals.push(newvar);
        this.lastConstr = super.addClause(literals);
        if (this.lastConstr == null) {
            discardLastestVar();
        } else {
            this.constrs.put(newvar, this.lastConstr);
        }
        return this.lastConstr;
    }

    public IConstr addNonControlableClause(IVecInt literals)
            throws ContradictionException {
        return super.addClause(literals);
    }

    @Override
    public IConstr addClause(IVecInt literals) throws ContradictionException {
        return addControlableClause(literals);
    }

    @Override
    public int[] model() {
        int[] fullmodel = super.modelWithInternalVariables();
        if (fullmodel == null) {
            return null;
        }
        int[] model = new int[fullmodel.length - this.constrs.size()];
        int j = 0;
        for (int element : fullmodel) {
            if (this.constrs.get(Math.abs(element)) == null) {
                model[j++] = element;
            }
        }
        return model;
    }

    /**
     * 
     * @since 2.1
     */
    public Collection<IConstr> getConstraints() {
        return this.constrs.values();
    }

    @Override
    public Collection<Integer> getAddedVars() {
        return this.constrs.keySet();
    }

    public IConstr getLastConstr() {
        return lastConstr;
    }

    public void setLastConstr(IConstr lastConstr) {
        this.lastConstr = lastConstr;
    }

    public Map<Integer, IConstr> getConstrs() {
        return constrs;
    }

    public IVecInt getLastClause() {
        return lastClause;
    }

    public boolean isSkipDuplicatedEntries() {
        return skipDuplicatedEntries;
    }

}
