package com.smartsoft.csp.varSet

import com.smartsoft.csp.bitSet.BitIndex
import com.smartsoft.csp.bitSet.BitSet32
import com.smartsoft.csp.bitSet.BitSet64
import com.smartsoft.csp.bitSet.Entry

val VarSetBuilder.activeWords: BitSet32 get() = BitSet64.activeWords(words)

val VarSetBuilder.wordIndexes: IntRange get() = minWordIndex..maxWordIndex

val VarSetBuilder.asVarNSet: VarNSet
    get() {
        val aw = activeWords
        val aa = LongArray(aw.size)
        var i = 0
        aw.forEach {
            aa[i] = words[it]
            i++
        }
        return VarNSet(space.varSpace, aw, aa)
    }

val VarSetBuilder.entries: Sequence<Entry>
    get() = sequence {
        words.indices.forEach { yield(Entry(it, words[it])) }
    }

val VarSetBuilder.activeEntries: Sequence<Entry> get() = entries.filter { BitSet64.isActive(it.value) }

val VarSetBuilder.activeWordArray: LongArray
    get() {
        val a = LongArray(activeWordCount)
        var i = 0
        words.forEach {
            if (it != 0L) {
                a[i] = it
                i++
            }
        }
        assert(a.none { it == 0L })
        return a
    }

fun VarSetBuilder.adjust(action: Adjust = Adjust.None) {
    when (action) {
        is Adjust.AddVar -> addVar(action.vr)
        is Adjust.AddVarSet -> addVarSet(action.vs)
        is Adjust.RemoveVar -> removeVar(action.vr)
        is Adjust.RemoveVarSet -> removeVarSet(action.vs)
        is Adjust.None -> Unit //do nothing
    }
}

fun VarSetBuilder._removeVarSetBuilderBitWise(b: VarSetBuilder): Boolean {
    assert(words.size == b.words.size)
    var ch = false
    for (i in 0 until b.wordCount) {
        val newVal = BitSet64.minus(words[i], b.words[i])
        if (words[i] != newVal) {
            ch = true
        }
        words[i] = newVal
    }
    if (ch) {
        _makeDirty()
    }
    return ch
}

fun VarSetBuilder._removeSingletonVarSet(vs: SingletonVarSet): Boolean {
    val v1 = vs.minVrId()
    return _removeVarId(v1)
}

val VarSetBuilder._isEmpty: Boolean get() = words.all { BitSet64.isEmpty(it) }

fun VarSetBuilder._removeVarPair(vs: VarPair): Boolean {
    val v1 = vs.vr1
    val v2 = vs.vr2
    assert(v1.vrId == vs.min)
    assert(v2.vrId == vs.max)
    val ch1 = _removeVarId(v1.vrId)
    val ch2 = _removeVarId(v2.vrId)
    return ch1 || ch2
}

fun VarSetBuilder._removeVarId(varId: Int): Boolean {
    val vr = space.getVar(varId)
    val wordIndex = VarSets.getWordIndexForVar(vr)
    val mask = VarSets.getMaskForLongWord(vr)
    val before = words[wordIndex]
    words[wordIndex] = words[wordIndex] and mask.inv()
    val after = words[wordIndex]
    return _maybeDirty(before, after);
}

fun VarSetBuilder._maybeDirty(before: Long, after: Long): Boolean {
    val ch = before != after
    return if (ch) {
        _makeDirty()
        true
    } else {
        false
    }
}

fun VarSetBuilder._makeDirty() {
    _size = -1
}


fun VarSetBuilder._removeVarSet(vs: VarSet): Boolean {
    return if (vs is EmptyVarSet) {
        false
    } else if (vs is SingletonVarSet) {
        _removeSingletonVarSet(vs)
    } else if (vs is VarPair) {
        _removeVarPair(vs)
    } else if (vs is VarSetBuilder) {
        _removeVarSetBuilderBitWise(vs)
    } else {
        throw IllegalStateException(vs.javaClass.toString() + "")
    }
}


object VarSetBuilderK {

    @JvmStatic
    fun overlap(b1: VarSetBuilder, b2: VarSetBuilder): VarSetBuilder {
        assert(b1.wordCount == b2.wordCount)
        val words = LongArray(b1.wordCount)
        words.indices.forEach {
            val w1 = b1.getWord(it)
            val w2 = b2.getWord(it)
            words[it] = BitSet64.overlap(w1, w2);
        }
        return VarSetBuilder(b1.varSpace, words, -1)
    }

    @JvmStatic
    fun wordOverlap(b1: VarSetBuilder, b2: VarSetBuilder): BitSet32 {
        val maxOverlap: BitSet32 = BitSet32.overlapMutableBitSet(b1.activeWords, b2.activeWords)
        if (maxOverlap.isEmpty) return BitSet32.EMPTY
        return maxOverlap.filter {
            val w1 = b1.words[it]
            val w2 = b2.words[it]
            BitSet64.anyOverlap(w1, w2)
        }
    }

    @JvmStatic
    fun anyOverlap(b1: VarSetBuilder, b2: VarSetBuilder): Boolean {
        val maxOverlap = BitSet32.overlapBitSet(b1.activeWords, b2.activeWords)
        if (maxOverlap.isEmpty) return false
        return maxOverlap.any { anyOverlap(b1, b2, it) }
    }

    @JvmStatic
    fun anyOverlap(word1: Long, word2: Long): Boolean {
        return BitSet64.anyOverlap(word1, word2)
    }

    private fun anyOverlap(words1: LongArray, words2: LongArray, index: BitIndex): Boolean {
        return anyOverlap(words1[index], words2[index])
    }

    private fun anyOverlap(b1: VarSetBuilder, b2: VarSetBuilder, index: BitIndex): Boolean {
        return anyOverlap(b1.words, b2.words, index)
    }


}





