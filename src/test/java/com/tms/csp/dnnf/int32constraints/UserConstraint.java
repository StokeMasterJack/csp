package com.tms.csp.dnnf.int32constraints;

import com.tms.csp.OrConstraint;
import com.tms.csp.RangeConstraint;
import com.tms.csp.ast.Lit;

import java.util.List;
import java.util.Set;

public class UserConstraint {

    List<Lit> picConstraints;
    Set<RangeConstraint> rangeConstraints;
    Set<OrConstraint> orConstraints;

}
