package com.smartsoft.csp.util;

import com.smartsoft.csp.ast.Exp;

public interface ConstraintSink {

    void addConstraint(Exp exp);

}
