package com.tms.csp.util;

import com.tms.csp.ast.Exp;

public interface ConstraintSink {

    void addConstraint(Exp exp);

}
