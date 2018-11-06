package com.tms.csp.ast.formula

import com.tms.csp.ast.*
import com.tms.csp.util.varSets.VarSet

interface FF {


    val topXorSplit: Xor?
    val bestXorSplit: Xor?
    val decide: Var
}

class KFormula(space: Space, expId: Int, args: Array<Exp>, fcc: Boolean?) : Formula(space, expId, args, fcc) {



    override fun satCountPL(parentVars: VarSet): Long {


        fun topXorSplitSatCount(): Long? {

            val xor: Xor? = topXorSplit
            return if (xor != null) {
                val split = XorSplit(this, xor.asXor())
                split.plSatCount()
            } else {
                null
            }

        }

        fun bestXorSplitSatCount(): Long? {
            val xor1: Xor? = bestXorSplit
            return if (xor1 != null) {
                val split = XorSplit(this, xor1.asXor())
                split.plSatCount()
            } else {
                null
            }
        }

        fun decisionSplitSatCount(): Long {
            val decisionVar: Var = decide()
            val split = DecisionSplit(this, decisionVar)
            return split.plSatCount()
        }

        fun satCount(): Long {
            val sc1 = topXorSplitSatCount()
            if (sc1 != null) {
                return sc1
            }


            val sc2 = bestXorSplitSatCount()
            if (sc2 != null) {
                return sc2
            }

            val sc3 = decisionSplitSatCount();
            return sc3;
        }


        val baseSatCount = satCount()

        return Csp.computeDcVars(baseSatCount, parentVars, vars);
//
//        val satCountWithDc = if (parentVars.isNullOrEmpty()) {
//            baseSatCount
//        } else {
//            val dcVars = parentVars.minus(this._vars)
//            val pow = Math.pow(2.0, dcVars.size.toDouble()).toLong()
//            baseSatCount * pow
//        }
//
//        return satCountWithDc


    }


    override fun print(heading: String) {
        System.err.println("${this.depth} FormulaK:$heading")
        for (arg in args) {
            System.err.println("  $arg")
        }
    }


}