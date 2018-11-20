package com.smartsoft.csp.transforms

import com.smartsoft.csp.ast.Exp


/**
 * Replaces each ShortCircuit(a b) with an Or(!a !b)
 */
class ConflictsToCnf : BaseTransformer() {

    override fun local(inExp: Exp): Exp? {

        val a1 = inExp.arg1
        val e1 = a1.flip

        val a2 = inExp.arg2
        val e2 = a2.flip

        val space = inExp.space

        return space.mkOr(e1, e2)
        //        return space.mkPosComplex(PosOp.OR, e1, e2);
    }

    override fun executeLocal(inExp: Exp): Boolean {
        return inExp.isNand
    }
}
