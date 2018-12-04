package com.smartsoft.csp.varSet

import com.smartsoft.csp.util.ints.IntIterator

class EmptyIntIterator : IntIterator {
    override fun hasNext(): Boolean {
        return false
    }

    override fun next(): Int {
        throw UnsupportedOperationException()
    }
}

interface LongIterator {
    operator fun hasNext(): Boolean

    operator fun next(): Long
}

class ArrayLongIterator(private val a: LongArray) {
    private var i = -1

    operator fun hasNext(): Boolean {
        return i < a.size
    }

    operator fun next(): Long {
        i++
        return a[i]
    }
}

open class BitSetVarIdIterator(protected var varSet: VarSetBuilder) : IntIterator {

    protected var wordIndex = -1
    protected var unseen: Long = 0
    protected var bitIndex: Int = 0
    protected var varId: Int = 0
    protected var mask: Long = 0

    init {
        moveToNextStableState()
    }


    val isStableLive: Boolean
        get() = unseen != 0L

    val isStableDead: Boolean
        get() = wordIndex >= maxWordCount()

    val isStable: Boolean
        get() = isStableLive || isStableDead

    fun maxWordCount(): Int {
        return varSet.wordCount
    }


    private fun computeVarId(): Int {
        return VarSets.computeVarId(wordIndex, bitIndex)
    }


    fun moveToNextStableState() {

        //stable: unseen != 0
        // dead:  wordIndex >= maxWordCount
        while (true) {
            if (unseen == 0L) {
                wordIndex++
                if (wordIndex >= maxWordCount()) {
                    return
                }
                maybeIncrementWord()
            } else {
                break
            }

        }


        mask = java.lang.Long.lowestOneBit(unseen)
        bitIndex = java.lang.Long.numberOfTrailingZeros(mask)
        varId = computeVarId()


    }

    fun maybeIncrementWord() {
        unseen = varSet.getWord(wordIndex)
    }

    override fun hasNext(): Boolean {
        return isStableLive
    }

    override fun next(): Int {
        assert(isStableLive)
        val retVal = varId
        takeNext()
        return retVal
    }

    fun takeNext() {
        unseen -= mask
        moveToNextStableState()
    }


}


//class BitIteratorLong(private var unseen: Long) : IntIterator {
//
//    override fun hasNext(): Boolean {
//        return unseen != 0L
//    }
//
//    override fun next(): Int {
//        if (!hasNext()) throw NoSuchElementException()
//        val mask = java.lang.Long.lowestOneBit(unseen)
//        unseen -= mask
//        return java.lang.Long.numberOfTrailingZeros(mask)
//    }
//
//}
