package com.tms.csp.ast

import java.util.*

private class FVar2(val vr: Var) {

    private var count = 0

    fun incr() {
        count++
    }


    val score: Int get() = count


}

object FVars2 {

    fun decide(complex: Array<Exp>): Var? {

        var best: FVar2? = null
        val map = HashMap<Var, FVar2>()

        for (constraint in complex) {
            assert(constraint.isComplex)
            assert(constraint !is Xor)

            for (vr in constraint.varIt()) {
                var fVar: FVar2? = map[vr]
                if (fVar == null) {
                    fVar = FVar2(vr)
                    map[vr] = fVar
                }
                fVar.incr()

                if (best == null || fVar.score > best.score) {
                    best = fVar
                }
            }
        }

        return best?.vr

    }


}

