package com.tms.csp.solver2.tools;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.tms.csp.solver2.specs.ContradictionException;
import com.tms.csp.solver2.specs.IConstr;
import com.tms.csp.solver2.specs.IGroupSolver;
import com.tms.csp.solver2.specs.ISolver;
import com.tms.csp.solver2.specs.IVecInt;

public class GroupClauseSelectorSolver<T extends ISolver> extends
        AbstractClauseSelectorSolver<T> implements IGroupSolver {

    private static final long serialVersionUID = 1L;

    private final Map<Integer, Integer> varToHighLevel = new HashMap<Integer, Integer>();
    private final Map<Integer, Integer> highLevelToVar = new HashMap<Integer, Integer>();

    public GroupClauseSelectorSolver(T solver) {
        super(solver);
    }

    public IConstr addControlableClause(IVecInt literals, int desc)
            throws ContradictionException {
        if (desc == 0) {
            return super.addClause(literals);
        }
        Integer hlvar = this.highLevelToVar.get(desc);
        if (hlvar == null) {
            hlvar = createNewVar(literals);
            this.highLevelToVar.put(desc, hlvar);
            this.varToHighLevel.put(hlvar, desc);
        }
        literals.push(hlvar);
        return super.addClause(literals);
    }

    public IConstr addNonControlableClause(IVecInt literals)
            throws ContradictionException {
        return super.addClause(literals);
    }

    public IConstr addClause(IVecInt literals, int desc)
            throws ContradictionException {
        return addControlableClause(literals, desc);
    }

    @Override
    public Collection<Integer> getAddedVars() {
        return varToHighLevel.keySet();
    }

    @Override
    public int[] model() {
        int[] fullmodel = super.modelWithInternalVariables();
        if (fullmodel == null) {
            return null;
        }
        int[] model = new int[fullmodel.length - this.varToHighLevel.size()];
        int j = 0;
        for (int element : fullmodel) {
            if (this.varToHighLevel.get(Math.abs(element)) == null) {
                model[j++] = element;
            }
        }
        return model;
    }

    public Map<Integer, Integer> getVarToHighLevel() {
        return varToHighLevel;
    }

}
