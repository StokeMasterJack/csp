package com.smartsoft.csp.ast;

import com.smartsoft.csp.util.varSets.VarSet;

public interface ISpace {

    VarSet varSetBuilder();

    String getVarCode(int varId);
}
