package com.smartsoft.csp.varSet

import com.smartsoft.csp.ast.Exp
import com.smartsoft.csp.ast.Var
import com.smartsoft.csp.ast.VarId
import com.smartsoft.csp.bitSet.BitSet64

fun minus(s1: VarSet, varCode: String): VarSet {
    val vr = s1.space.getVar(varCode)
    return minus(s1, vr)
}

fun minus(s1: VarSet, vrId: VarId): VarSet {
    val vr = s1.space.getVar(vrId)
    return minus(s1, vr)
}

fun minus(s1: VarSet, vr: Var): VarSet {
    return when (s1) {
        is EmptyVarSet -> s1
        is SingletonVarSet -> minus(s1, vr)
        is VarPair -> minus(s1, vr)
        is VarSetBuilder -> minus(s1, vr)
        is VarNSet -> minus(s1, vr)
        else -> throw IllegalStateException()
    }
}

fun minus(s1: SingletonVarSet, vr: Var): VarSet = if (s1.vr === vr) s1.mkEmptyVarSet() else s1

fun minus(s1: VarPair, vr: Var): VarSet = when {
    s1.vr1 === vr -> s1.vr2.mkSingletonVarSet()
    s1.vr2 === vr -> s1.vr1.mkSingletonVarSet()
    else -> s1
}

fun minus(b: VarSetBuilder, vr: Var): VarSet = b.copyToVarSetBuilder(Adjust.rem(vr))
fun minus(b: VarSetBuilder, vrId: VarId): VarSet = minus(b, b.space.getVar(vrId))

fun minus(s1: VarNSet, vr: Var): VarSet {
    return if (!s1.containsVar(vr)) {
        s1
    } else {

        val aWordIndex = s1.getActiveWordIndexForVar(vr)
        val oldWord = s1.words[aWordIndex]
        val mask = VarSets.getMaskForLongWord(vr)

        val newWord = oldWord and mask.inv()

        val activeWords = if (newWord == 0L) {
            s1.activeWords.minus(vr.varIndex)
        } else {
            s1.copyActiveWords()
        }
        val words: LongArray = if (newWord == 0L) {
            s1.words.filter { it != 0L }.toLongArray()
        } else {
            val words = s1.copyWords()
            words[aWordIndex] = newWord
            words
        }


        VarNSet(s1.varSpace, activeWords = activeWords, words = words)


    }


}


fun minus(s1: VarSet, s2: VarSet): VarSet {
    return when (s1) {
        is EmptyVarSet -> s1
        is SingletonVarSet -> when (s2) {
            is EmptyVarSet -> s1
            is SingletonVarSet -> minus(s1, s2)
            is VarPair -> minus(s1, s2)
            is VarSetBuilder -> minus(s1, s2)
            is VarNSet -> minus(s1, s2)
            else -> throw IllegalStateException()
        }
        is VarPair -> when (s2) {
            is EmptyVarSet -> s1
            is SingletonVarSet -> minus(s1, s2)
            is VarPair -> minus(s1, s2)
            is VarSetBuilder -> minus(s1, s2)
            is VarNSet -> minus(s1, s2)
            else -> throw IllegalStateException()
        }
        is VarSetBuilder -> when (s2) {
            is EmptyVarSet -> s1
            is SingletonVarSet -> minus(s1, s2)
            is VarPair -> minus(s1, s2)
            is VarSetBuilder -> minus(s1, s2)
            is VarNSet -> minus(s1, s2)
            else -> throw IllegalStateException(s2.simpleName + " " + s2)
        }
        is VarNSet -> when (s2) {
            is EmptyVarSet -> s1
            is SingletonVarSet -> minus(s1, s2)
            is VarPair -> minus(s1, s2)
            is VarSetBuilder -> minus(s1, s2)
            is VarNSet -> minus(s1, s2)
            else -> throw IllegalStateException()
        }
        else -> throw IllegalStateException()
    }
}

fun minus(s1: SingletonVarSet, s2: SingletonVarSet): VarSet {
    return if (s1.vr == s2.vr) {
        s1.mkEmptyVarSet()
    } else {
        s1
    }
}

fun minus(s1: VarPair, s2: VarPair): VarSet {
    val space = s1.space
    return when {
        s1._containsBoth(s2) -> space.mkEmptyVarSet();
        s1._containsNeither(s2) -> s1
        s2.containsVar(s1.vr1) -> s1.vr2.mkSingletonVarSet()
        s2.containsVar(s1.vr2) -> s1.vr1.mkSingletonVarSet()
        else -> throw IllegalStateException()
    }
}

fun minus(s1: VarPair, s2: VarSetBuilder): VarSet {
    return when {
        s2.containsBoth(s1) -> s1.mkEmptyVarSet()
        s2.containsNeither(s1) -> s1
        s2.containsVar(s1.vr1) -> s1.vr2.mkSingletonVarSet()
        s2.containsVar(s1.vr2) -> s1.vr1.mkSingletonVarSet()
        else -> Exp.th()
    }
}


fun minus(s1: VarPair, s2: SingletonVarSet): VarSet {
    return when {
        s1.notContains(s2.vr) -> s1
        s1.vr1Id == s2.vrId -> s1.vr2.mkSingletonVarSet()
        s1.vr2Id == s2.vrId -> s1.vr1.mkSingletonVarSet()
        else -> throw IllegalStateException()
    }
}

fun minus(s1: VarSetBuilder, s2: SingletonVarSet): VarSet {
    return when {
        s1.containsVar(s2.vr) -> s1.copyToVarSetBuilder(Adjust.rem(s2.vr))
        else -> s1
    }
}

fun minus(s1: VarSetBuilder, s2: VarPair): VarSet {
    return when {
        s1.containsNeither(s2) -> s1
        s1.containsBoth(s2) -> s1.copyToVarSetBuilder(Adjust.rem(s2))
        s1.containsVar(s2.vr1) -> s1.minus(s2.vr1)
        s1.containsVar(s2.vr2) -> s1.minus(s2.vr2)
        else -> Exp.th()
    }
}


fun minus(s1: VarSetBuilder, s2: VarSetBuilder): VarSet {
    s1.calculateSize();
    s2.calculateSize();
    assert(s1.wordCount == s2.wordCount)
    val words = LongArray(s1.wordCount)
    s1.wordIndexes.forEach {
        val w1: Long = s1.words[it]
        val w2: Long = s2.words[it]
        val w: Long = BitSet64.minus(w1, w2)
        words[it] = w
    }
    val ret = VarSetBuilder(s1.varSpace, words)
    ret.calculateSize()
    return ret;
}

/*
    public boolean removeVarSetBuilderBitWise(VarSetBuilder b) {
        assert words.length == b.words.length;
        boolean ch = false;
        for (int i = 0; i < b.getWordCount(); i++) {
            long newVal = BitSet64.minus(words[i], b.words[i]);
            if (words[i] != newVal) {
                ch = true;
            }
            words[i] = newVal;
        }
        if (ch) {
            makeDirty();
        }
        return ch;
    }
 */
