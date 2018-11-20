package com.smartsoft.csp.ast

import com.smartsoft.csp.ast.formula.NoVarsException

import java.util.ArrayList
import java.util.Comparator
import java.util.HashMap

import com.google.common.base.Preconditions.checkNotNull

class FVars(complex: Iterable<Exp>) {

    private val map = HashMap<Var, FVar>()

    private var xors: ArrayList<Xor>? = null
    val sortedList: ArrayList<FVar>


//    val sortedFVarList: List<FVar>
//        get() {
//            if (this.sortedList == null) {
//                this.sortedList = createFVarList()
//            }
//            return sortedList!!
//        }

    init {
        checkNotNull(complex)
        addConstraints(complex)
        if (map.isEmpty()) throw IllegalStateException()
        sortedList = ArrayList(map.values)
        FVar.sort(sortedList)
    }

    private fun addConstraints(e: Iterable<Exp>) {
        for (constraint in e) {
            assert(constraint.isComplex) { "Non-complex: $constraint" }
            addConstraint(constraint)
        }
    }

    private fun addConstraint(constraint: Exp) {
        assert(constraint.isComplex)
        if (constraint.isXor) {
            if (xors == null) {
                xors = ArrayList()
            }
            xors!!.add(constraint.asXor)
        }
        for (`var` in constraint.varIt()) {
            val fVar = getFVar(`var`)
            fVar.addConstraint(constraint)
        }
    }


    private fun getFVar(`var`: Var): FVar {
        var fVar: FVar? = map[`var`]
        if (fVar == null) {
            fVar = FVar(`var`)
            map[`var`] = fVar
        }
        return fVar
    }

    fun createFVarList(): ArrayList<FVar> {
        val a = ArrayList<FVar>()
        for (fVar in map.values) {
            a.add(fVar)
        }
        return a
    }


    fun print() {
        for (fVar in sortedList) {
            fVar.print()
        }
    }

    @Throws(NoVarsException::class)
    fun decide(): FVar {
        if (sortedList.isEmpty()) {
            throw NoVarsException()
        }
        return sortedList[0]
    }

    companion object {

        val F_VAR_COMPARATOR: Comparator<FVar> = Comparator { v1, v2 ->
            val s1 = v1.getScore()
            val s2 = v2.getScore()
            -s1.compareTo(s2)
        }
    }

}

