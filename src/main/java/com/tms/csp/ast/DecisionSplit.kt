package com.tms.csp.ast

import com.google.common.base.Preconditions.checkNotNull
import com.tms.csp.argBuilder.ArgBuilder

class DecisionSplit(val formula: Exp, val decisionVar: Var) {


    val argSeq = formula.argSeq
    val space: Space = decisionVar.space;
    val formulaVars = formula.vars


    init {
        checkNotNull(formula)
        checkNotNull(decisionVar)
        check(formula.isFormula || formula.isOr || formula.isXor)
        //        assert formula.isFcc();
    }


    val isSat: Boolean
        get() {


            val t = mkCsp(true)
            if (t.isSat()) {
                return true
            } else {
                val f = mkCsp(false)
                return f.isSat()
            }
        }

    val isSatLite: Lit?
        get() {
            val t = mkCsp(true)
            if (t.isFailed) {
                return decisionVar.mkNegLit()
            } else {
                val f = mkCsp(false)
                return if (f.isFailed) {
                    decisionVar.mkPosLit()
                } else {
                    null
                }
            }
        }


    fun toDnnf(): Exp {
        val space = decisionVar.space

        val b = ArgBuilder(space, Op.DOr)
        val t = mkCsp(true)

        if (t.isFailed) {
            //implied lit: nLit
            //            System.err.println("CSP Failed: " + t.get_failure());
        }

        val tt = t.toDnnf()
        if (tt.isTrue) {
            return space.mkTrue()
        } else if (tt.isFalse) {
            val f = mkCsp(false)
            return f.toDnnf()
        } else {
            val f = mkCsp(false)
            val ff = f.toDnnf()
            b.addExp(tt)
            b.addExp(ff)
            return b.mk()
        }

    }

    fun mkCsp(sign: Boolean): Csp {
        val add = Add.decisionSplit(formula, decisionVar, sign)
        return add.mkCsp();
    }

    fun plSatCount(): Long {
        val t = mkCsp(true)
        val pSatCount = t.satCountPL(formula.vars)
        val f = mkCsp(false)
        val nSatCount = f.satCountPL(formula.vars)
        return pSatCount + nSatCount
    }

}
