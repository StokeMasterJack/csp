package com.tms.csp.transforms;

import com.tms.csp.ast.Exp;

import javax.annotation.Nonnull;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class CompoundListTransform extends ListTransform {

    private final Transformer[] transformer;

    public CompoundListTransform(@Nonnull Transformer... transformer) {
        checkNotNull(transformer);
        this.transformer = transformer;
    }

    @Nonnull
    @Override
    public List<Exp> transform(@Nonnull List<Exp> listIn) {
        return multiTransformList(listIn, transformer);
    }

}
