package com.tms.csp.trail;

import com.tms.csp.ast.PosComplexMultiVar;
import com.tms.csp.ast.Var;
import com.tms.csp.util.HasCodeComparator;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

public class VarStates {

    private final TreeMap<Var, VarAssignment> map;
    private VarAssignment head;

    public VarStates() {
        HasCodeComparator<Var> comparator = HasCodeComparator.varComparator();
        map = new TreeMap<Var, VarAssignment>(comparator);
    }

    public boolean isTrue(Var var) {
        return getVarState(var).isTrue();
    }

    public boolean isFalse(Var var) {
        return getVarState(var).isFalse();
    }

    public boolean isOpen(Var var) {
        return getVarState(var).isOpen();
    }

    public boolean isConflicted(Var var) {
        return getVarState(var).isConflicted();
    }

    public boolean isAssigned(Var var) {
        return getVarState(var).isAssigned();
    }

    public VarAssignment getVarAssignment(Var var) {
        return map.get(var);
    }

    public VarState getVarState(Var var) {
        VarAssignment varAssignment = getVarAssignment(var);
        if (varAssignment == null) {
            return VarState.OPEN;
        } else {
            return varAssignment.getState();
        }
    }

    public Set<Var> getAssignedVars(boolean assignedValue) throws AlreadyConflictedException {
        HashSet<Var> retVal = new HashSet<Var>();
        for (VarAssignment varAssignment : map.values()) {
            if (varAssignment.isConflict()) {
                throw new AlreadyConflictedException();
            }
            if (varAssignment.isAssigned() && varAssignment.isAssignedValue(assignedValue)) {
                retVal.add(varAssignment.getVar());
            }
        }
        return retVal;
    }

    public Set<Var> getFalseVars() throws AlreadyConflictedException {
        return getAssignedVars(false);
    }

    public Set<Var> getTrueVars() throws AlreadyConflictedException {
        return getAssignedVars(true);
    }

    public VarAssignment assign(Var var, boolean newValue, PosComplexMultiVar cause) throws AlreadyConflictedException {
        if (this.head != null && this.head.isConflict()) throw new AlreadyConflictedException();
        VarAssignment previousForThisVar = getVarAssignment(var);
        if (previousForThisVar == null) {
            this.head = new VarAssignment(this.head, var, newValue, cause);
        } else {
            this.head = new VarAssignment(this.head, previousForThisVar, newValue, cause);
        }
        return this.head;
    }

    public void printAssignment() {
        System.err.println("Var Assignments: ");
        Set<Var> trueVars = getTrueVars();
        Set<Var> falseVars = getFalseVars();
        System.err.println("trueVars:" + trueVars.size() + ":" + trueVars);
        System.err.println("falseVars:" + falseVars.size() + ":" + falseVars);
    }

    public int getAssignmentCount() {
        return map.size();
    }

}
