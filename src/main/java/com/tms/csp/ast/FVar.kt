package com.tms.csp.ast


import java.util.*

class FVar(var vr: Var) {

    var fVars: FVars? = null
        internal set
    internal var xorParent: Exp? = null
    internal var vvConstraints: MutableList<Exp>? = null
    internal var complexConstraints: MutableList<Exp>? = null

    internal var value: Boolean? = null

    internal var score = -1

    val vvCount: Int
        get() = if (vvConstraints == null) 0 else vvConstraints!!.size


    val complexCount: Int
        get() = if (complexConstraints == null) 0 else complexConstraints!!.size

    fun addConstraint(constraint: Exp) {
        assert(constraint.isComplex)

        if (!constraint.containsVar(vr)) {
            System.err.println("constraint: $constraint")
            System.err.println("constraint: " + constraint.javaClass)
            System.err.println("vr: $vr")
            System.err.println()
        }
        assert(constraint.containsVar(vr))



        if (constraint.isXor) {
            assert(xorParent == null)
            xorParent = constraint
            assert(xorParent!!.vars.containsVar(vr))
            throw IllegalStateException()
        }
//        else if (constraint.isLitImp) {
//            assert(!constraint.isXor)
//            addVV(constraint)
//        }
        else if (constraint.isLitImp) {
            assert(!constraint.isXor)
            addVV(constraint)
        } else if (constraint.isComplex) {
            assert(!constraint.isXor)
            addComplex(constraint)
        }
    }

    fun addVV(constraint: Exp) {
        if (vvConstraints == null) {
            vvConstraints = ArrayList()
        }
        vvConstraints!!.add(constraint)
    }

    fun addComplex(constraint: Exp) {
        if (complexConstraints == null) {
            complexConstraints = ArrayList()
        }
        complexConstraints!!.add(constraint)
    }

    fun getScore(): Int {
        if (score == -1) {
            var s = 0

            if (xorParent != null) {
//                println("AAAAA")
//                val argCount = xorParent!!.asXor.argCount
//                s += 1000 * argCount
                throw IllegalStateException()
            }
            s += vvCount * 100

            s += complexCount * 10

            score = s
        }
        return score
    }

    fun assign(value: Boolean) {
        assert(this.value == null)
        this.value = value
    }

    fun print() {
        System.err.println("vr[" + vr + "]  score[" + getScore() + "]")
    }

    companion object {

        fun sort(fVars: List<FVar>) {
            Collections.sort(fVars, F_VAR_COMPARATOR)
        }

        val F_VAR_COMPARATOR: Comparator<FVar> = Comparator { v1, v2 ->
            val s1 = v1.getScore()
            val s2 = v2.getScore()
            -s1.compareTo(s2)
        }
    }

}

