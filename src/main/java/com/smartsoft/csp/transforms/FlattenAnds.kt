package com.smartsoft.csp.transforms

import com.smartsoft.csp.ast.And
import com.smartsoft.csp.ast.Exp

class FlattenAnds : BaseTransformer() {

    override fun local(inExp: Exp): Exp? {
        return flattenAnd(inExp.asAnd)
    }

    override fun executeLocal(inExp: Exp): Boolean {
        if (inExp.isAnd) {
            for (arg in inExp.argIt) {
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
