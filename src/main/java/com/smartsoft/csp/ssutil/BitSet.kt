package com.smartsoft.csp.ssutil

import com.google.common.base.Splitter
import java.util.*

private val MY_SPLITTER = Splitter.on(' ').trimResults().omitEmptyStrings()

fun mutableBitSetOf(vararg indexes: Int): MutableBitSet {
    val s = MutableBitSet()
    for (index in indexes) {
        s.add(index)
    }
    return s
}

fun mutableBitSetOf(sIndexes: String): MutableBitSet = MutableBitSet(sIndexes)

fun bitSetOf(vararg indexes: Int): BitSet {
    return if (indexes.isEmpty()) BitSet.EMPTY
    else mutableBitSetOf(*indexes)
}

fun bitSetOf(sIndexes: String): BitSet {
    return if (sIndexes.trim().isEmpty()) BitSet.EMPTY else mutableBitSetOf(sIndexes)
}

fun emptyBitSet(): BitSet = BitSet.EMPTY

open class BitSet(initWord: Int) {

    internal var _word: Int = initWord

    operator fun get(index: Int): Boolean = _word and (1 shl index) != 0

    fun isSet(index: Int): Boolean = get(index)
    fun isClear(index: Int): Boolean = !isSet(index)


    operator fun contains(index: Int): Boolean = get(index)


    val asInt: Int get() = _word

    val size: Int get() = Integer.bitCount(_word)

    val isEmpty: Boolean get() = _word == 0
    val isNotEmpty: Boolean get() = !isEmpty


    @Throws(IndexOutOfBoundsException::class)
    fun nextSetBit(fromIndex: Int): Int {
        require(fromIndex in 0..31) { "fromIndex: $fromIndex" }
        val ww = _word and (WORD_MASK shl fromIndex)
        val ntz = Integer.numberOfTrailingZeros(ww)
        return if (ntz == BITS_PER_WORD) {
            -1
        } else {
            ntz
        }
    }

    @Throws(IndexOutOfBoundsException::class)
    fun nextSetBit(fromIndex: Int, action: ((Int) -> Unit)) {
        val index = nextSetBit(fromIndex)
        if (index != -1) {
            action(index)
        }
    }

    @Throws(IndexOutOfBoundsException::class)
    fun previousSetBit(fromIndex: Int): Int {
        require(fromIndex in 0..31) { "fromIndex: $fromIndex" }
        val ww = _word and WORD_MASK.ushr(-(fromIndex + 1))
        val ntz = Integer.numberOfLeadingZeros(ww)
        return if (ntz == BITS_PER_WORD) {
            -1
        } else {
            BITS_PER_WORD - 1 - ntz
        }
    }

    @Throws(IndexOutOfBoundsException::class)
    fun previousSetBit(fromIndex: Int, action: ((Int) -> Unit)) {
        val index = previousSetBit(fromIndex)
        if (index != -1) {
            action(index)
        }
    }

    val min: Int get() = nextSetBit(0)

    val max: Int get() = previousSetBit(31)

    fun forEach(action: (Int) -> Unit) = toIntSeq().forEach(action)

    fun toIntSeq() = sequence {
        var index = 0
        while (true) {
            if (index > MAX_INDEX) break
            val next = nextSetBit(index)
            if (next == -1) {
                break
            } else {
                yield(next)
                index = next + 1
            }
        }
    }

    fun toIntList(): List<Int> = toIntSeq().toList()
    fun toSortedIntSet(): SortedSet<Int> = toIntSeq().toSortedSet()

    fun anyOverlap(other: BitSet): Boolean = BitSet.anyOverlap(this._word, other._word)

    //15 16 17
    val ser: String get() = toIntSeq().joinToString(" ")


    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (other !is BitSet) return false
        return _word == other._word
    }

    override fun hashCode(): Int {
        return _word
    }

    fun bitString(): String {
        val sb = StringBuilder()
        for (i in 0..31) {
            val value = this[i]
            if (value) {
                sb.append("1")
            } else {
                sb.append("0")
            }
        }
        return sb.toString()
    }


    fun assertSer(s: String) = assert(ser == s)

    fun assertSet(index: Int) = assert(isSet(index))
    fun assertClear(index: Int) = assert(isClear(index))

    companion object {

        const val BITS_PER_WORD: Int = 32
        const val MAX_INDEX: Int = 31
        val INDEX_RANGE: IntRange = 0..MAX_INDEX


        val EMPTY: BitSet = MutableBitSet()

        private const val WORD_MASK = -1

        fun requireIndex(index: Int) {
            if (index !in INDEX_RANGE) {
                throw IndexOutOfBoundsException("Invalid BitSet32 index: $index")
            }
        }

        fun anyOverlap(bs1: BitSet, bs2: BitSet): Boolean = anyOverlap(bs1._word, bs2._word)

        fun anyOverlap(word1: Int, word2: Int): Boolean = word1 and word2 != 0

        fun union(s1: BitSet, s2: BitSet): BitSet {
            return MutableBitSet(initWord = s1._word or s2._word)
        }

        fun union(s1: BitSet, s2: BitSet, s3: BitSet): BitSet {
            return MutableBitSet(initWord = s1._word or s2._word or s3._word)
        }

        fun fromWord(initWord: Int): BitSet = MutableBitSet(initWord = initWord)

        fun fromIntIt(indexes: Iterable<Int>): BitSet {
            val s = MutableBitSet()
            s.addAll(indexes)
            return s
        }

        fun fromBitSet(bitSet: BitSet): BitSet {
            val s = MutableBitSet()
            s.addAll(bitSet)
            return s
        }
    }

}

class MutableBitSet(initWord: Int = 0) : BitSet(initWord = initWord) {

    internal constructor(bitSet: BitSet) : this(bitSet._word)

    internal constructor(intIt: Iterable<Int>) : this() {
        addAll(intIt = intIt)
    }

    internal constructor(sIndexes: String) : this() {
        addAll(sIndexes = sIndexes)
    }

    fun set(index: Int) {
        BitSet.requireIndex(index)
        _word = _word or (1 shl index)
    }

    fun clear(index: Int) {
        BitSet.requireIndex(index)
        _word = _word and (1 shl index).inv()
    }


    //same as set
    fun add(index: Int) = set(index)

    //same as clear
    fun remove(index: Int) = clear(index)

    fun removeAll(bitSet: BitSet) {
        _word = _word and bitSet._word.inv()
    }

    //less efficient than removeAll(bitSet: BitSet)
    fun removeAll(intIt: Iterable<Int>) {
        for (index in intIt) {
            remove(index)
        }
    }

    //less efficient than removeAll(bitSet: BitSet)
    fun removeAll(sIndexes: String) {
        val bitSet = bitSetOf(sIndexes)
        removeAll(bitSet)
    }

    //more efficient than addAll(indexes: Iterable<Int>)
    fun addAll(bitSet: BitSet) {
        _word = _word or bitSet._word
    }

    //less efficient than addAll(other: BitSet)
    fun addAll(intIt: Iterable<Int>) {
        for (index in intIt) {
            set(index)
        }
    }

    //less efficient than addAll(other: BitSet)
    fun addAll(intArray: IntArray) {
        for (index in intArray) {
            set(index)
        }
    }

    //less efficient than addAll(other: BitSet)
    fun addAll(sIndexes: String) {
        val strIt: Iterable<String> = MY_SPLITTER.split(sIndexes.trim())
        for (sIndex in strIt) {
            val index = sIndex.trim().toInt()
            add(index)
        }
    }


}



