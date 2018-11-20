package com.smartsoft.csp.ast

class FormulaSplit(val formula: Formula, val decisionVar: Var) {


    val space: Space get() = formula.space;
    val expFactory: ExpFactory get() = space.expFactory

    val isSat: Boolean
        get() {
            val t = mkCsp(true)
            return if (t.isSat()) {
                true
            } else {
                val f = mkCsp(false)
                f.isSat()
            }
        }


    fun toDnnf(): Exp {

        val t = mkCsp(true)
        val tt = t.toDnnf()
        if (tt.isTrue) return tt

        val ff = mkCsp(false).toDnnf();

        return expFactory.mkDOr(tt, ff)


    }


    fun mkCsp(sign: Boolean): Csp {
        val lit = decisionVar.lit(sign)
        return Csp(formula = formula.argIt, condition = lit)
    }


    fun plSatCount(): Long {
        val t = mkCsp(true)
        val pSatCount = Csp.computeDcVars(t.satCountPL(), formula.vars, t.vars)
        val f = mkCsp(false)
        val nSatCount = Csp.computeDcVars(f.satCountPL(), formula.vars, f.vars)
        return pSatCount + nSatCount
    }

}
