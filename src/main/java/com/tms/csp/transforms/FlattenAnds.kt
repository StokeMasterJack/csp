package com.tms.csp.transforms

import com.tms.csp.ast.And
import com.tms.csp.ast.Exp

class FlattenAnds : BaseTransformer() {

    override fun local(inExp: Exp): Exp? {
        return flattenAnd(inExp.asAnd)
    }

    override fun executeLocal(expIn: Exp): Boolean {
        if (expIn.isAnd) {
            for (arg in expIn.argIt) {
                if (arg.isAnd) {
                    return true
                }
            }
        }
        return false
    }

    private fun flattenAnd(expIn: And): Exp {
        return expIn.flatten()
    }


}
