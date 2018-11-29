package com.smartsoft.csp.varSet

import com.smartsoft.csp.ast.Var
import com.smartsoft.csp.ast.VarId
import com.smartsoft.csp.bitSet.BitIndex
import com.smartsoft.csp.bitSet.BitSet32
import com.smartsoft.csp.bitSet.BitSet64
import com.smartsoft.csp.bitSet.Entry
import com.smartsoft.csp.parse.VarSpace
import com.smartsoft.csp.util.ints.IntIterator
import java.util.*

class VarNSet(
        val vrSpace: VarSpace,

        /**
         * active word indexes
         */
        val activeWords: BitSet32,

        /**
         * active words (non-empty words)
         */
        val words: LongArray


) : VarSet() {


    override fun getVarSpace(): VarSpace = vrSpace


    override fun minVrId(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun maxVrId(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun intIterator(): IntIterator {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun containsPrefix(prefix: String?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val size: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun isEmpty(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun getVarId(index: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun recomputeSize(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun activeEquals(other: VarNSet): Boolean {
        return activeWords == other.activeWords
    }

    fun wordsEquals(other: VarNSet): Boolean {
        return Arrays.equals(words, other.words)
    }

    fun contentEquals(other: VarNSet): Boolean {
        assert(space === other.space)
        return activeEquals(other) && wordsEquals(other)
    }

    fun eq(b: VarSetBuilder): Boolean {
        return eqActiveWords(b) && eqWords(b)
    }

    fun eq(b: VarNSet): Boolean {
        return eqActiveWords(b) && eqWords(b)
    }

    fun eqActiveWords(b: VarSetBuilder): Boolean = activeWords == b.activeWords
    fun eqActiveWords(b: VarNSet): Boolean = activeWords == b.activeWords

    val entries: Sequence<Entry>
        get() = sequence {
            words.indices.forEach { yield(Entry(it, words[it])) }
        }


    fun eqWords(b: VarSetBuilder): Boolean = words.contentEquals(b.activeWordArray)
    fun eqWords(b: VarNSet): Boolean = words.contentEquals(b.words)

    fun activeWordIndex(wordIndex: Int): Int {
        return activeWordSeq.indexOf(wordIndex)
    }

    fun getActiveWordIndexForVarId(vrId: VarId): Int = getActiveWordIndexForVar(space.getVar(vrId))

    fun getActiveWordIndexForVar(vr: Var): Int = activeWordIndex(vr.wordIndex)

//    fun getWordIndexForVar(vr: Var): Int = vr.varIndex

    val activeWordSeq: Sequence<Int> get() = activeWords.trueSeq

    fun getWordForVar(vr: Var): Long {
        return getWordByWordIndex(vr.varIndex)
    }

    fun getWordByWordIndex(wordIndex: BitIndex): Long {
        val activeWordIndex = activeWordIndex(wordIndex)
        return getWordByActiveWordIndex(activeWordIndex)
    }

    fun getWordByActiveWordIndex(activeWordIndex: BitIndex): Long {
        return words[activeWordIndex]
    }


    override fun removeVar(vr: Var): Boolean {
        val mask: Long = VarSets.getMaskForLongWord(vr)
        val before = words[vr.varIndex];
        words[vr.varIndex] = words[vr.varIndex] and mask.inv()
        val after = words[vr.varIndex];
        return before != after
    }

    override fun removeVarId(varId: Int): Boolean {
        val vr = space.getVar(varId)
        return removeVar(vr)
    }

//    override fun removeVarId(varId: Int): Boolean {
//        val wordIndex = VarSets.getWordIndexForVarId(varId)
//        val mask = VarSets.getMaskForLongWord(varId)
//        val before = words[wordIndex]
//        words[wordIndex] = words[wordIndex] and mask.inv()
//        val after = words[wordIndex]
//        return maybeDirty(before, after)
//    }


    fun copyToVarNSet(adjust: Adjust = Adjust.none): VarNSet {
        return VarNSet(vrSpace = varSpace, activeWords = copyActiveWords(), words = copyWords())
    }

    fun copyWords(): LongArray {
        return words.copyOf()
    }

    fun copyActiveWords(): BitSet32 {
        return activeWords.copy()
    }


    companion object {
        fun wordOverlap(b1: VarNSet, b2: VarNSet): BitSet32 {
            val maxOverlap: BitSet32 = BitSet32.overlapMutableBitSet(b1.activeWords, b2.activeWords)
            if (maxOverlap.isEmpty) return BitSet32.EMPTY
            return maxOverlap.filter { BitSet64.anyOverlap(b1.words[it], b2.words[it]) }
        }

        fun overlap(s1: VarNSet, s2: VarNSet): VarNSet {
            TODO()
        }

        fun anyOverlap(s1: VarNSet, s2: VarNSet): Boolean {
            TODO()
        }

    }
}