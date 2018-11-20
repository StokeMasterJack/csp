package com.smartsoft.csp.transforms

import com.smartsoft.csp.ast.Exp

class IdentityTransformer : Transformer() {

    override fun transform(inExp: Exp): Exp {
        return inExp
    }
}
