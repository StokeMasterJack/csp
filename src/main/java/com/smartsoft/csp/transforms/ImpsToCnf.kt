package com.smartsoft.csp.transforms

import com.smartsoft.csp.ast.Exp

/**
 * Replaces each imp(a b) with an or(!a b)
 */
class ImpsToCnf : BaseTransformer() {

    override fun local(inExp: Exp): Exp? {
        return inExp.asImp().toOr
    }

    override fun executeLocal(inExp: Exp): Boolean {
        return inExp.isImp
    }


}
