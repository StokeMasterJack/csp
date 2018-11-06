package com.tms.csp.ast;

import com.tms.csp.util.varSets.VarSet;

import javax.annotation.Nonnull;

public interface IXor {

    @Nonnull
    VarSet getVars();

    @Nonnull
    String getPrefix();

}
