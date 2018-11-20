package com.smartsoft.csp.transforms


import com.smartsoft.csp.ast.Exp
import com.smartsoft.csp.ast.Op
import com.smartsoft.csp.argBuilder.ArgBuilder

import com.google.common.base.Preconditions.checkArgument

//!and(a b)   => or(!a !b)


//DF: !or(a b) => and(!a !b)
class PushNotsIn : BaseTransformer() {

    override fun executeLocal(inExp: Exp): Boolean {
        return inExp.isNegAnd || inExp.isNegOr
    }

    //  !and(a b)   => or(!a !b)
    override fun local(inExp: Exp): Exp? {
        return if (inExp.isNegAnd) {
            negAnd(inExp)
        } else if (inExp.isNegOr) {
            negOr(inExp)
        } else {
            throw IllegalStateException()
        }
    }

    /**
     * !and(a b)   => or(!a !b)
     */
    private fun negAnd(expIn: Exp): Exp {
        checkArgument(expIn.isNegAnd)
        val pos = expIn.pos
        val pArgs = pos.args

        val flippedArgs = ArgBuilder(expIn.space, Op.Or)
        for (arg in pArgs) {
            flippedArgs.addExp(arg.flip)
        }

        return flippedArgs.mk()
    }

    /**
     * !or(a b) => and(!a !b)
     */
    protected fun negOr(expIn: Exp): Exp {
        checkArgument(expIn.isNegOr)

        val pos = expIn.pos
        val pArgs = pos.args

        val oop: Op
        if (pos.isAllLits) {
            oop = Op.DAnd
        } else {
            oop = Op.And
        }
        val flippedArgs = ArgBuilder(expIn.space, oop)
        for (arg in pArgs) {
            flippedArgs.addExp(arg.flip)
        }

        return flippedArgs.mk()
        //        return formula.getSpace().mkAnd(flippedArgs);
    }


}
