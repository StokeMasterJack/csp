package com.smartsoft.csp.bitSet

import com.smartsoft.csp.ast.Var
import com.smartsoft.csp.varSet.VarSets

class JJ {

    internal var w1 = 0L

    @Throws(Exception::class)
    fun test1(vr: Var) {
        val mask = VarSets.getMaskForLongWord(vr)
        w1 = w1 and mask.inv()
    }

}
