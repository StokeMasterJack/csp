package com.smartsoft.csp.litImp

import com.smartsoft.csp.ast.Exp
import com.smartsoft.csp.ast.ExpFactory
import com.smartsoft.csp.ast.Space

class KbSplit(val parent: Kb, val decision: VarImps) {

    val space: Space get() = decision.space;
    val expFactory: ExpFactory get() = space.expFactory

    fun toDnnf(): Exp {
        val t = mkKb(true)
        val tt = t.toDnnf()
        if (tt.isTrue) return tt
        val ff = mkKb(false).toDnnf();
        return expFactory.mkDOr(tt, ff)
    }

    fun mkKb(sign: Boolean): Kb {
        checkNotNull(decision)
        return parent.refine(decision.vr.lit(sign))
    }


}
