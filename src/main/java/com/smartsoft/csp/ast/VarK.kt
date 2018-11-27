package com.smartsoft.csp.ast

val Var.varCount: Int get() = space.varSpace.varCount

val Var.wordCount: Int get() = space.varSpace.wordCount

val Var.wordIndex: Int get() = VarK.computeWordIndexFromVarIndex(varIndex)

object VarK {
    fun computeWordIndexFromVarIndex(varIndex: Int): Int {
        return varIndex.ushr(6)
    }
}


