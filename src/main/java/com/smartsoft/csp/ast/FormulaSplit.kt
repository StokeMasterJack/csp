package com.smartsoft.csp.ast

import com.smartsoft.csp.litImp.VarImps


class FormulaSplit(val formula: Formula, val decisionVar: Var, val decision: VarImps? = null) {


    val space: Space get() = formula.space;
    val expFactory: ExpFactory get() = space.expFactory

    constructor(formula: Formula, decision: VarImps) : this(formula, decision = decision, decisionVar = decision.vr)
    constructor(formula: Formula, decisionVar: Var) : this(formula, decision = null, decisionVar = decisionVar)

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

    fun turbo(sign: Boolean): Boolean = Space.config.cspTurbo && decision != null && decision.hasImps(sign)

    fun mkCsp(sign: Boolean): Csp {
        return if (true && turbo(sign)) {
            mkCspTurbo(sign)
        } else {
            mkCspSimple(sign);
        }
    }


    fun mkCspSimple(sign: Boolean): Csp {
        val lit = decisionVar.lit(sign)
//        println("mkCspSimple decisionVar: ${decisionVar} lit:${lit}")
        return Csp(formula = formula, condition = lit)
    }


    fun mkCspTurbo(sign: Boolean): Csp {
        checkNotNull(decision)


//        println("best = pImpCount: ${decision.pImpCount}  nImpCount: ${decision.nImpCount}  ")
        val cube: ConditionOn = decision.impsCube(sign)
//        println("  ${decision.vr.lit(sign)} cube[$sign]: $cube")

        return Csp(formula = formula, condition = cube)
    }


    fun plSatCount(): Long {
        val t = mkCsp(true)
        val pSatCount = Csp.computeDcVars(t.satCountPL(), formula.vars, t.vars)
        val f = mkCsp(false)
        val nSatCount = Csp.computeDcVars(f.satCountPL(), formula.vars, f.vars)
        return pSatCount + nSatCount
    }

}


