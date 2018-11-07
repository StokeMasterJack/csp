package com.tms.csp.ast

class OrVarSplit(val or: Or, val decisionVar: Var) {

    val space: Space get() = or.space;

    val tLit: Lit get() = decisionVar.pLit()
    val fLit: Lit get() = decisionVar.nLit()

    val tCon: Exp by lazy { or.condition(tLit) }
    val fCon: Exp by lazy { or.condition(fLit) }


    val isSat: Boolean get() = tCon.isSat || fCon.isSat

    fun toDnnf(): Exp = with(space.expFactory) {
        val tt = tCon.toDnnf()
        if (tt.isTrue) return mkTrue();
        val ff = fCon.toDnnf()
        mkDOr(mkDAnd(tt, tLit), mkDAnd(ff, fLit))
    }


    fun satCountPL(): Long {
        val tSatCount = tCon.satCountPL(or.vars)
        val fSatCount = fCon.satCountPL(or.vars)
        return tSatCount + fSatCount
    }

}
