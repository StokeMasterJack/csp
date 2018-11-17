package com.tms.csp.transforms

import com.tms.csp.argBuilder.ArgBuilder
import com.tms.csp.ast.Exp
import com.tms.csp.ast.Op

class FlattenImps : BaseTransformer() {

    override fun local(inExp: Exp): Exp? {
        val e1 = inExp.expr1
        val e2 = inExp.expr2

        val and = ArgBuilder(inExp.space, Op.And)
        for (e2Arg in e2.args) {
            val imp = inExp.mkImp(e1, e2Arg)
            and.addExp(imp)
        }

        return and.mk()
    }

    override fun executeLocal(expIn: Exp): Boolean {
        return expIn.isImp && expIn.isPair && expIn.arg2.isAnd
    }
}
