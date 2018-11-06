package com.tms.csp.transforms;

import com.tms.csp.ast.Exp;

import javax.annotation.Nonnull;

public class IdentityTransformer extends Transformer {

    @Nonnull
    @Override
    public Exp transform(@Nonnull Exp in) {
        return in;
    }
}
