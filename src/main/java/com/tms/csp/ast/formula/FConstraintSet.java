package com.tms.csp.ast.formula;


import com.tms.csp.ast.Exp;
import com.tms.csp.ast.Var;

/**
 * represents a set of complex fact
 */
public interface FConstraintSet extends Iterable<Exp> {

    /**
     * Does not include simple fact
     */
    int getConstraintCount();

    boolean isDirectlyRelated(int c1, int c2);
}
