package com.tms.csp.transforms;

import com.tms.csp.ast.Exp;

public interface ExpSink {

    void addConstraint(Exp constraint);

}
