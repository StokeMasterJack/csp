package com.tms.csp.transforms;

import com.tms.csp.ast.Exp;

import javax.annotation.Nonnull;

public class CompoundTransformer extends Transformer {

    private final Transformer[] transformers;

    public CompoundTransformer(Transformer... transformers) {
        this(null, transformers);
    }

    public CompoundTransformer(String name, Transformer... transformers) {
        this.transformers = transformers;
    }

    @Nonnull
    @Override
    public Exp transform(@Nonnull Exp in) {
        Exp exp = in;
        for (Transformer transformer : transformers) {
            exp = exp.transform(transformer);
        }
        return exp;
    }

}
