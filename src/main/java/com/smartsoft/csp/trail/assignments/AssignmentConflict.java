package com.smartsoft.csp.trail.assignments;


import com.smartsoft.csp.ast.Exp;

public class AssignmentConflict {

    private final Exp exp; //conflicted exp
    private final AssignmentSupport support; //conflicted expSupport

    public AssignmentConflict(Exp exp, AssignmentSupport support) {
        this.exp = exp;
        this.support = support;
    }

    public Exp getExp() {
        return exp;
    }

    public AssignmentSupport getSupport() {
        return support;
    }


    @Override
    public String toString() {
        return "AssignmentConflict[" + exp + "] support[" + support + "] ";
    }
}
