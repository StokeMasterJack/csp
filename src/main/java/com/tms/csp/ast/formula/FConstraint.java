package com.tms.csp.ast.formula;

import com.tms.csp.ast.Exp;
import com.tms.csp.ast.Space;
import com.tms.csp.fm.dnnf.FVars;
import com.tms.csp.util.varSets.VarSet;

public class FConstraint {

    Space fSpace;
    Space space;

    Exp groupParent;   //fcc parent

    long[] xorGroups;  //1 bit per xor group
    long[] boolVars;

    FConstraint fConstraints;
    FVars fVars;
    Exp constraint;

    public FConstraint(FConstraintSet fSpace, Exp constraint) {

    }

    public void addComplex(Exp complex) {

    }

    public VarSet getFVars() {
        return null;
    }

    public Exp getConstraint() {
        return constraint;
    }
}
