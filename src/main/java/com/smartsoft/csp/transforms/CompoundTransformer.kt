package com.smartsoft.csp.transforms

import com.smartsoft.csp.ast.Exp

class CompoundTransformer(name: String?, vararg transformers: Transformer) : Transformer() {

    private val transformers: Array<out Transformer>

    constructor(vararg transformers: Transformer) : this(null, *transformers) {}

    init {
        this.transformers = transformers
    }

    override fun transform(inExp: Exp): Exp {
        var exp = inExp
        for (transformer in transformers) {
            exp = exp.transform(transformer)
        }
        return exp
    }

}
