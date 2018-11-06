package com.tms.csp.transforms;

import com.tms.csp.ast.Exp;

import javax.annotation.Nonnull;

public class UntilStableTransformer extends Transformer {



    private Transformer transformer;

    public UntilStableTransformer(Transformer... transformer) {
        this(null, transformer);
    }

    public UntilStableTransformer(String name, Transformer... transformer) {
        this.name = name;
        if (transformer.length > 1) {
            this.transformer = new CompoundTransformer(transformer);
        } else {
            this.transformer = transformer[0];
        }
    }

    @Nonnull
    @Override
    public Exp transform(@Nonnull Exp in) {
        return transformUntilStable(transformer, in);
    }

}
