package com.tms.csp.transforms

import com.tms.csp.ast.Exp

/**
 * Replaces each And(a) with a
 */
class OneArgAnd : BaseTransformer() {

    override fun local(inExp: Exp): Exp? {
        return inExp.arg
    }

    override fun executeLocal(inExp: Exp): Boolean {
        return inExp.isAnd && inExp.argCount() == 1
    }

}
