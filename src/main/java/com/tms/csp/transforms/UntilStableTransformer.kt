package com.tms.csp.transforms

import com.tms.csp.ast.Exp

class UntilStableTransformer(name: String?, vararg transformer: Transformer) : Transformer() {


    private var transformer: Transformer? = null

    constructor(vararg transformer: Transformer) : this(null, *transformer) {}

    init {

        this._name = name
        if (transformer.size > 1) {
            this.transformer = CompoundTransformer(*transformer)
        } else {
            this.transformer = transformer[0]
        }
    }

    override fun transform(inExp: Exp): Exp {
        return Transformer.Companion.transformUntilStable(transformer!!, inExp)
    }

}
