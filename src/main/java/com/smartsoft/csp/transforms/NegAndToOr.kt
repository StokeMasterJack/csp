package com.smartsoft.csp.transforms

import com.smartsoft.csp.argBuilder.ArgBuilder
import com.smartsoft.csp.ast.Exp
import com.smartsoft.csp.ast.Op

import com.google.common.base.Preconditions.checkArgument

//!and(a b)   => or(!a !b)


//DF: !or(a b) => and(!a !b)
class NegAndToOr : BaseTransformer() {

    override fun executeLocal(inExp: Exp): Boolean {
        return inExp.isNegAnd
    }

    //  !and(a b)   => or(!a !b)
    override fun local(inExp: Exp): Exp? {
        checkArgument(inExp.isNegAnd)
        val args = inExp.pos.args

        val flippedArgs = ArgBuilder(inExp.space, Op.Or)
        for (arg in args) {
            flippedArgs.addExp(arg.flip)
        }

        assert(flippedArgs.op().isOrLike)
        return flippedArgs.mk()
        //        return ff.getSpace().mkOr();
    }


}
