package com.tms.csp.transforms

import com.tms.csp.ast.Exp

/**
 *
 */
class OrToAnd : BaseTransformer() {

    override fun local(inExp: Exp): Exp? {
        assert(inExp.isOr)

        //        if(true) throw new IllegalStateException();
        val or = inExp.asOr
        return or.createEquivAnd()
    }

    override fun executeLocal(expIn: Exp): Boolean {
        return expIn.isOr
    }


}
