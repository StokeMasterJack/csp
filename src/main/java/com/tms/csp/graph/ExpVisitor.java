package com.tms.csp.graph;


import com.tms.csp.ast.Exp;

public abstract class ExpVisitor {

    public void visit(Exp e, int depth) {
        visitHead(e, depth);
        visitArgs(e, depth);
    }

    public void visitHead(Exp e, int depth) {
        System.err.println("visit: " + e.toString());
    }

    public void visitArgs(Exp e, int depth) {
        if (e.isComplex()) {
            for (Exp arg : e.getArgs()) {
                visit(arg, depth + 1);
            }
        }
    }


}
