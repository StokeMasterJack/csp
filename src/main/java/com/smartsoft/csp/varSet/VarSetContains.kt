package com.smartsoft.csp.varSet

import com.smartsoft.csp.ast.Lit
import com.smartsoft.csp.ast.Var
import com.smartsoft.csp.ast.VarId
import com.smartsoft.csp.bitSet.BitSet64


fun VarSet._contains(s2: VarSet): Boolean {
    val s1 = this
    if (s1.isEmpty()) return false;
    if (s2.isEmpty()) return true;
    return when (s1) {
        is SingletonVarSet -> when (s2) {
            is EmptyVarSet -> true
            is SingletonVarSet -> s1._contains(s2)
            is VarPair -> s1._contains(s2)
            is VarSetBuilder -> s1._contains(s2)
            is VarNSet -> s1._contains(s2)
            else -> throw IllegalStateException()
        }
        is VarPair -> when (s2) {
            is SingletonVarSet -> s1._contains(s2)
            is VarPair -> s1._contains(s2)
            is VarSetBuilder -> s1._contains(s2)
            is VarNSet -> s1._contains(s2)
            else -> throw IllegalStateException()
        }
        is VarSetBuilder -> when (s2) {
            is SingletonVarSet -> s1._contains(s2)
            is VarPair -> s1._contains(s2)
            is VarSetBuilder -> s1._contains(s2)
            is VarNSet -> s1._contains(s2)
            else -> throw IllegalStateException()
        }
        is VarNSet -> when (s2) {
            is SingletonVarSet -> s1._contains(s2)
            is VarPair -> s1._contains(s2)
            is VarSetBuilder -> s1._contains(s2)
            is VarNSet -> s1._contains(s2)
            else -> throw IllegalStateException()
        }
        else -> throw IllegalStateException()

    }

}


//SingletonVarSet extensions
fun SingletonVarSet._contains(s: SingletonVarSet): Boolean = _contains(s.vr)

fun SingletonVarSet._contains(s: VarPair): Boolean = false
fun SingletonVarSet._contains(s: VarSetBuilder): Boolean = s.isEmpty() || (s.size == 1 && _contains(s.minVar))
fun SingletonVarSet._contains(s: VarNSet): Boolean = TODO()

//VarPair extensions
fun VarPair._contains(s: SingletonVarSet): Boolean = _contains(s.vr)

fun VarPair._contains(s: VarPair): Boolean = _contains(s.vr1) && _contains(s.vr2)
fun VarPair._contains(s: VarSetBuilder): Boolean = if (s.size > 2) false else _contains(s.minVar) && _contains(s.maxVar)
fun VarPair._contains(s: VarNSet): Boolean = TODO()

//VarSetBuilder extensions
fun VarSetBuilder._contains(s: SingletonVarSet): Boolean = _contains(s.vr)

fun VarSetBuilder._contains(s: VarPair): Boolean = _contains(s.vr1) && _contains(s.vr2)
fun VarSetBuilder._contains(s: VarSetBuilder): Boolean {
    assert(space == s.space)
    wordIndexes.forEach {
        val w1 = words[it]
        val w2 = s.words[it]
        val c = BitSet64.contains(w1, w2)
        if (!c) return false;
    }
    return true
}

fun VarSetBuilder._contains(s: VarNSet): Boolean = TODO()

//VarSetBuilder extensions
fun VarNSet._contains(s: SingletonVarSet): Boolean = TODO()

fun VarNSet._contains(s: VarPair): Boolean = TODO()
fun VarNSet._contains(s: VarSetBuilder): Boolean = TODO()
fun VarNSet._contains(s: VarNSet): Boolean = TODO()

fun VarSet._containsVar(lit: Lit): Boolean = _contains(lit.vr)

fun VarSet._contains(vr: Var): Boolean = when (this) {
    is EmptyVarSet -> false
    is SingletonVarSet -> this._contains(vr)
    is VarPair -> this._contains(vr)
    is VarSetBuilder -> this._contains(vr)
    is VarNSet -> this._contains(vr)
    else -> throw IllegalStateException()
}

fun VarSet._notContains(vr: Var): Boolean = !_contains(vr)
fun VarSet._notContains(vs: VarSet): Boolean = !_contains(vs)

fun SingletonVarSet._contains(vr: Var): Boolean = this.vr == vr

fun VarPair._contains(vr: Var): Boolean = vr == vr1 || vr == vr2

//((words[wordIndex] & (1L << bitIndex)) != 0)
fun VarSetBuilder._contains(vr: Var): Boolean {
    val word: Long = getWord(vr)
    return BitSet64.contains(word, vr.index)
}

fun VarNSet._contains(vr: Var): Boolean {
    TODO()
}

fun VarSet._containsVarCode(varCode: String): Boolean = this._contains(space.getVar(varCode))
fun VarSet._notContainsVarCode(varCode: String): Boolean = !this._containsVarCode(varCode)

fun VarSet._containsVarId(varId: VarId): Boolean = this._contains(space.getVar(varId))


fun VarSet._containsBoth(vs: VarPair): Boolean {
    return _containsBothVars(vs.vr1, vs.vr2)
}

fun VarSet._containsBothVars(vr1: Var, vr2: Var): Boolean {
    return _contains(vr1) && _contains(vr2)
}

fun VarSet._containsNeither(vr1: Var, vr2: Var): Boolean {
    return !_contains(vr1) && !_contains(vr2)
}

fun VarSet._containsNeither(vs: VarPair): Boolean {
    return _containsNeither(vs.vr1, vs.vr2)
}

fun VarSet._containsBothVarIds(vr1: VarId, vr2: VarId): Boolean {
    return _containsVarId(vr1) && _containsVarId(vr2)
}


fun VarSet._containsEitherVarId(vr1: VarId, vr2: VarId): Boolean {
    return _containsVarId(vr1) || _containsVarId(vr2)
}

fun VarSet.containsEitherVar(vr1: Var, vr2: Var): Boolean {
    return _contains(vr1) || _contains(vr2)
}

fun VarSet.containsEitherVar(p: VarPair): Boolean {
    return _contains(p.vr1) || _contains(p.vr2)
}

