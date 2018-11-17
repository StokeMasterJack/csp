package com.tms.csp.transforms

import com.tms.csp.ast.Exp

class IdentityTransformer : Transformer() {

    override fun transform(inExp: Exp): Exp {
        return inExp
    }
}
