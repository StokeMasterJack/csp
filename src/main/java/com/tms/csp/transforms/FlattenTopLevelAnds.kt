package com.tms.csp.transforms

import com.tms.csp.argBuilder.ArgBuilder
import com.tms.csp.ast.Exp
import com.tms.csp.ast.Op

class FlattenTopLevelAnds : BaseTransformer() {

    override fun local(inExp: Exp): Exp? {
        return flattenTopLevelAnds(inExp)
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

    private fun flattenTopLevelAnds(inExp: Exp): Exp {
        var anyChange = false

        val flatter = ArgBuilder(inExp.space, Op.And)
        for (arg in inExp.args) {
            if (arg.isAnd) {
                val args = arg.args
                flatter.addExpIt(args)
                anyChange = true
            } else {
                flatter.addExp(arg)
            }
        }


        return if (anyChange) {
            flatter.mk()
            //            return space.mkAnd(flatter);
        } else {
            inExp
        }

    }

}
