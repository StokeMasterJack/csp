package com.smartsoft.csp.transforms

import com.smartsoft.csp.ast.Exp

/**
 * Replaces each Or(a) with a
 */
class OneArgOr : BaseTransformer() {

    override fun local(inExp: Exp): Exp? {
        return inExp.arg
    }

    override fun executeLocal(inExp: Exp): Boolean {
        return inExp.isOr && inExp.argCount() == 1
    }

}
