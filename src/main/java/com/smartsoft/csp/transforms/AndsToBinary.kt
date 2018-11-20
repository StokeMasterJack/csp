package com.smartsoft.csp.transforms

import com.smartsoft.csp.argBuilder.ArgBuilder
import com.smartsoft.csp.ast.Exp
import com.smartsoft.csp.ast.Op

class AndsToBinary : BaseTransformer() {

    override fun local(inExp: Exp): Exp? {
        val first = inExp.arg1
        val and = first.mkAnd(inExp.argsRest()).transform(Transformer.Companion.AND_TO_BINARY)

        val space = inExp.space

        val b = ArgBuilder(space, Op.And, false)
        b.addExp(first)
        b.addExp(and)
        return b.mk()

    }

    override fun executeLocal(inExp: Exp): Boolean {
        return inExp.isAnd && inExp.isNary
    }


}
