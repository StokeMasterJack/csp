package com.tms.csp.transforms

import com.tms.csp.argBuilder.ArgBuilder
import com.tms.csp.ast.Exp
import com.tms.csp.ast.Op

import com.google.common.base.Preconditions.checkArgument

//  !or(a b) => and(!a !b)
class NegOrToAnd : BaseTransformer() {

    override fun executeLocal(inExp: Exp): Boolean {
        return inExp.isNegOr
    }

    //  !and(a b)   => or(!a !b)
    override fun local(inExp: Exp): Exp? {
        checkArgument(inExp.isNegOr)
        val args = inExp.pos.args

        val flippedArgs = ArgBuilder(inExp.space, Op.And)
        for (arg in args) {
            flippedArgs.addExp(arg.flip)
        }

        return flippedArgs.mk()
    }


}
