package com.smartsoft.csp.transforms

import com.smartsoft.csp.ast.Exp

/**
 * Replaces each imp(a b) with an or(!a b)
 */
class RmpsToOr : BaseTransformer() {

    override fun local(inExp: Exp): Exp? {
        return inExp.asRmp().toOr
    }

    override fun executeLocal(inExp: Exp): Boolean {
        return inExp.isRmp
    }


}
