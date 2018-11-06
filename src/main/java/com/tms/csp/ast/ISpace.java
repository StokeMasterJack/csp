package com.tms.csp.ast;

import com.tms.csp.util.varSets.VarSet;

public interface ISpace {

    VarSet varSetBuilder();

    String getVarCode(int varId);
}
