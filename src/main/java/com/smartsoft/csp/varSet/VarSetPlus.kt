package com.smartsoft.csp.varSet

import com.smartsoft.csp.ast.Space
import com.smartsoft.csp.ast.Var
import com.smartsoft.csp.bitSet.BitSet64

fun plus(s1: VarSet, vr: Var): VarSet {
    val space = vr.space
    return when (s1) {
        is EmptyVarSet -> vr.mkSingletonVarSet()
        is SingletonVarSet -> space.mkVarPair(s1.vr, vr)
        is VarPair -> plus(s1, vr)
        is VarSetBuilder -> plus(s1, vr)
        is VarNSet -> plus(s1, vr)
        else -> throw IllegalStateException()
    }
}

fun plus(s1: VarPair, vr: Var): VarSet = if (s1._contains(vr)) s1 else s1.copyToVarSetBuilder(vr)
fun plus(s1: VarSetBuilder, vr: Var): VarSet = if (s1._contains(vr)) s1 else s1.copyToVarSetBuilder(vr)
fun plus(s1: VarNSet, vr: Var): VarSet = if (s1._contains(vr)) s1 else s1.copyToVarSetBuilder(vr)

fun plus(s1: VarSet, s2: VarSet): VarSet {
    return when (s1) {
        is EmptyVarSet -> s2
        is SingletonVarSet -> when (s2) {
            is SingletonVarSet -> plus(s2, s1)
            is VarPair -> plus(s1, s2)
            is VarSetBuilder -> plus(s2, s1)
            is VarNSet -> plus(s2, s1)
            else -> throw IllegalStateException()
        }
        is VarPair -> when (s2) {
            is SingletonVarSet -> plus(s2, s1)
            is VarPair -> plus(s2, s1)
            is VarSetBuilder -> plus(s2, s1)
            is VarNSet -> plus(s2, s1)
            else -> throw IllegalStateException()
        }
        is VarSetBuilder -> when (s2) {
            is SingletonVarSet -> plus(s2, s1)
            is VarPair -> plus(s2, s1)
            is VarSetBuilder -> plus(s2, s1)
            is VarNSet -> plus(s2, s1)
            else -> throw IllegalStateException()
        }
        is VarNSet -> when (s2) {
            is SingletonVarSet -> plus(s2, s1)
            is VarPair -> plus(s2, s1)
            is VarSetBuilder -> plus(s2, s1)
            is VarNSet -> plus(s2, s1)
            else -> throw IllegalStateException()
        }
        else -> throw IllegalStateException()
    }
}

fun plus(s1: SingletonVarSet, s2: SingletonVarSet): VarSet = s2.space.mkVarPair(s1.vr, s2.vr)

fun plus(s1: VarPair, s2: VarPair): VarSet {
    return when {
        s1.containsBoth(s2) -> s1
        s1.containsVar(s2.vr1) -> s1.copyToVarSetBuilder(s2.vr2)
        s1.containsVar(s2.vr2) -> s1.copyToVarSetBuilder(s2.vr1)
        else -> s1.copyToVarSetBuilder(s2)
    }
}

fun plus(s1: VarPair, s2: SingletonVarSet): VarSet {
    return when {
        s1.containsVar(s2.vr) -> s1
        else -> s1.copyToVarSetBuilder(s2.vr)
    }
}

fun plus(s1: VarSetBuilder, s2: VarPair): VarSet {
    return when {
        s1.containsBoth(s2) -> s1
        s1.containsVar(s2.vr1) -> s1.copyToVarSetBuilder(s2.vr2)
        s1.containsVar(s2.vr2) -> s1.copyToVarSetBuilder(s2.vr1)
        else -> s1.copyToVarSetBuilder(s2)
    }
}

fun plus(s1: VarSetBuilder, s2: VarSetBuilder): VarSet {
    val words = LongArray(s1.wordCount)
    s1.wordIndexes.forEach {
        val w1 = s1.getWord(it)
        val w2 = s2.getWord(it)
        words[it] = BitSet64.plus(w1, w2)
    }
    return VarSetBuilder(s1.varSpace, words)
}

fun plusVarSets(space: Space, that: Array<VarSet?>): VarSet {
    val b = space.varSetBuilder()
    for (vars in that) {
        if (vars == null) continue
        b.addVarSet(vars)
    }
    return b.build()
}