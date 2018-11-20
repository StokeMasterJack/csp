package com.smartsoft.csp.transforms

import com.smartsoft.csp.ast.Exp
import com.smartsoft.csp.ast.Or

import com.google.common.base.Preconditions.checkArgument

class FlattenOrs : BaseTransformer() {

    override fun local(inExp: Exp): Exp? {
        return flattenOr(inExp.asOr)
    }

    override fun executeLocal(inExp: Exp): Boolean {
        if (inExp.isOr) {
            for (arg in inExp.argIt) {
                if (arg.isOr) {
                    return true
                }
            }
        }
        return false
    }

    private fun flattenOr(inExp: Or): Exp {
        checkArgument(inExp.argList().size >= 2)
        assert(!inExp.isNestedOr) { inExp.toString() }
        return inExp

    }

}
