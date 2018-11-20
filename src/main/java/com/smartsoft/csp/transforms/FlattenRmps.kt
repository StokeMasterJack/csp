package com.smartsoft.csp.transforms

import com.smartsoft.csp.argBuilder.ArgBuilder
import com.smartsoft.csp.ast.Exp
import com.smartsoft.csp.ast.Op

class FlattenRmps : BaseTransformer() {

    override fun local(inExp: Exp): Exp? {
        val e1 = inExp.expr2
        val e2 = inExp.expr1

        val and = ArgBuilder(inExp.space, Op.And)
        for (e2Arg in e2.args) {
            val imp = inExp.mkImp(e1, e2Arg)
            and.addExp(imp)
        }
        return and.mk()
    }

    override fun executeLocal(inExp: Exp): Boolean {
        return inExp.isRmp && inExp.isPair && inExp.arg1.isAnd
    }
}
