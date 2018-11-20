package com.smartsoft.csp.transforms

import com.smartsoft.csp.argBuilder.ArgBuilder
import com.smartsoft.csp.ast.Exp
import com.smartsoft.csp.ast.Op

class PushAndsOut : BaseTransformer() {

    override fun local(inExp: Exp): Exp? {
        return pushAndsUp(inExp)
    }

    override fun executeLocal(inExp: Exp): Boolean {
        if (inExp.isOr) {
            val args = inExp.argsToList
            val argCount = args.size
            for (i in 0 until argCount) {
                val arg = args[i]
                if (arg.isAnd) {
                    return true
                }
            }
        }
        return false
    }

    //a or (b and c and d) = (a or b) and (a or c) and (a or d)
    private fun pushAndsUp(e: Exp): Exp {

        val space = e.space

        val args = e.args



        val firstAnd = Exp.getFirstAnd(args)
        if (firstAnd != null) {
            val orTerms = ArgBuilder(space, Op.Or)
            for (f in args) {
                if (f !== firstAnd) {
                    orTerms.addExp(f)
                }
            }

            val x: Exp
            if (orTerms.size == 0) {
                throw IllegalStateException()
            } else if (orTerms.size == 1) {
                x = orTerms.first
            } else {
                x = orTerms.mk()
            }

            val expressions1 = firstAnd.args


            val newAndTerms = ArgBuilder(space, Op.And)

            for ((i,faExpr) in expressions1.withIndex()) {
                val or = e.mkOr(x, faExpr)
                newAndTerms.addExp(or)
            }

            return newAndTerms.mk()

        } else {
            val cnfArgs = ArgBuilder(space, Op.Or)
            cnfArgs.addExpIt(args)
            return cnfArgs.mk()
        }


    }


}
