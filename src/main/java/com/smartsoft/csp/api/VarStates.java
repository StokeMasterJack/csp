package com.smartsoft.csp.api;

import com.google.common.collect.ImmutableSet;
import com.smartsoft.csp.ast.Lit;
import com.smartsoft.csp.ast.Var;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class VarStates {

    private Long satCount;
    private Boolean sat;

    private Map<Var, VarState> map = new HashMap<Var, VarState>();

    public void addVarState(VarState varState) {
        map.put(varState.getVar(), varState);
    }


    public Set<Lit> getBB() {

        ImmutableSet.Builder<Lit> bb = ImmutableSet.builder();
        for (VarState varState : map.values()) {
            if (varState.getUserValue().isOpen() && varState.getComputedValue().isAssigned()) {
                Lit lit = varState.toLit();
                bb.add(lit);
            }
        }
        return bb.build();
    }

    public void dump() {
        for (VarState varState : map.values()) {
            log.info(varState.toString());
        }
    }

    public void print() {
        if (sat) {
            for (VarState varState : map.values()) {
                System.err.println(varState.toString());
            }
        } else {
            System.err.println("UNSAT");
        }
    }

    public void setSat(boolean sat) {
        this.sat = sat;
    }

    private static Logger log = Logger.getLogger(VarStates.class.getName());

    public boolean isTrueStuck(Var var) {
        VarState varState = map.get(var);
        return varState.isTrueStuck();
    }
}
