package com.smartsoft.csp.ast

val Var.varCount: Int get() = space.varSpace.varCount

val Var.wordCount: Int get() = space.varSpace.wordCount

val Var.wordIndex: Int get() = VarK.wordIndexFromVar(this)

object VarK {

    fun varIdFromVarIndex(varIndex: Int): VarId = varIndex + 1

    fun varIndexFromVarId(varId: VarId): Int = varId - 1

    fun wordIndexFromVarId(varId: VarId): Int = wordIndexFromVarIndex(varIndexFromVarId(varId))

    fun wordIndexFromVarIndex(varIndex: Int): Int = varIndex.ushr(6)

    fun wordIndexFromVar(vr: Var): Int = wordIndexFromVarIndex(vr.varIndex)

    @JvmStatic
    fun computeSize(words: LongArray): Int = words.bitCount

//    fun computeActiveWords(words: LongArray): BitSet32 {
//        val a = BitSet32()
//        for ((index, word) in words.withIndex()) {
//            if (word != 0L) a.set(index)
//        }
//        return a
//    }


//    override fun addVarId(varId: Int): Boolean {
//        val wordIndex = VarSets.getWordIndexForVarId(varId)
//        val mask = VarSets.getMaskForLongWord(varId)
//        val oldValue = words[wordIndex]
//        words[wordIndex] = words[wordIndex] or mask
//        val ch = oldValue != words[wordIndex]
//        if (ch) {
//            makeDirty()
//            return true
//        } else {
//            return false
//        }
//    }

}


val Long.bitCount get() = java.lang.Long.bitCount(this)

val Int.bitCount get() = java.lang.Integer.bitCount(this)


val LongArray.bitCount: Int
    get() {
        var s = 0
        this.forEach { s += it.bitCount }
        return s
    }

