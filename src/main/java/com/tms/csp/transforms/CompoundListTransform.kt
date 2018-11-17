package com.tms.csp.transforms

import com.tms.csp.ast.Exp

import com.google.common.base.Preconditions.checkNotNull

class CompoundListTransform(vararg transformer: Transformer) : ListTransform() {

    private val transformer: Array<out Transformer>

    init {
        checkNotNull<Array<out Transformer>>(transformer)
        this.transformer = transformer
    }

    override fun transform(listIn: List<Exp>): List<Exp> {
        return ListTransform.multiTransformList(listIn, transformer)
    }

}
