package com.smartsoft.csp.transforms

import com.smartsoft.csp.ast.Exp

/**
 * Replaces each iff(a b) with an and(imp(a b) imp(b a))
 */
class RmpsToCnf : BaseTransformer() {

    override fun local(inExp: Exp): Exp? {
        return inExp.asRmp().toOr
    }

    override fun executeLocal(inExp: Exp): Boolean {
        return inExp.isRmp
    }


}
