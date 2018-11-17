package com.tms.csp.transforms

import com.tms.csp.ast.Exp

/**
 * Replaces each imp(a b) with an or(!a b)
 */
class RmpsToOr : BaseTransformer() {

    override fun local(inExp: Exp): Exp? {
        return inExp.asRmp().toOr
    }

    override fun executeLocal(expIn: Exp): Boolean {
        return expIn.isRmp
    }


}
