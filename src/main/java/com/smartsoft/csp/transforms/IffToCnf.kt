package com.smartsoft.csp.transforms

import com.smartsoft.csp.ast.Exp

/**
 *
 * iff(a and(z y z)).toCnf =
 *
 * or(!a x)
 * or(!a y)
 * or(!a z)
 * or(!z !y !x a)
 *
 * all horn clauses
 *
 */
class IffToCnf : BaseTransformer() {

    override fun local(inExp: Exp): Exp? {
        val e1 = inExp.arg1
        val e2 = inExp.arg2

        val imp1 = inExp.mkOr(e1.flip, e2)
        val imp2 = inExp.mkOr(e2.flip, e1)



        return inExp.mkAnd(imp1, imp2)
    }

    override fun executeLocal(expIn: Exp): Boolean {
        return expIn.isIff
    }


}
