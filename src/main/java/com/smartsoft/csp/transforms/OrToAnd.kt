package com.smartsoft.csp.transforms

import com.smartsoft.csp.ast.Exp

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

    override fun executeLocal(inExp: Exp): Boolean {
        return inExp.isOr
    }


}
