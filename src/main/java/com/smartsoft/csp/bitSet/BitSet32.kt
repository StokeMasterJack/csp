package com.smartsoft.csp.bitSet

import com.google.common.base.Splitter
import com.smartsoft.csp.ast.bitCount
import java.util.*

typealias BitIndex = Int

private val MY_SPLITTER = Splitter.on(' ').trimResults().omitEmptyStrings()

fun mutableBitSet32Of(vararg indexes: BitIndex): BitSet32 {
    val s = BitSet32()
    for (index in indexes) {
        s.add(index)
    }
    return s
}


//fun bitSet32Of(vararg indexes: Int): BitSet32 {
//    return if (indexes.isEmpty()) BitSet32.EMPTY
//    else mutableBitSet32Of(*indexes)
//}

fun bitSet32Of(vararg indexes: BitIndex): BitSet32 {
    val s = BitSet32()
    for (index in indexes) {
        s.add(index)
    }
    return s
}


fun bitSet32Of(sIndexes: String): BitSet32 {
    return if (sIndexes.trim().isEmpty()) BitSet32.EMPTY else BitSet32(sIndexes)
}

fun emptyBitSet32(): BitSet32 = BitSet32.EMPTY


open class BitSet32(initWord: Int = 0) {

    internal constructor(bitSet: BitSet32) : this(bitSet._word)

    internal constructor(intIt: Iterable<Int>) : this() {
        addAll(intIt = intIt)
    }

    internal constructor(sIndexes: String) : this() {
        addAll(sIndexes = sIndexes)
    }

    override fun toString(): String {
        return ser
    }

    fun toStringDetail(): String {
        return "${javaClass.simpleName}  ${this.size} $ser   "
    }

    internal var _word: Int = initWord

    operator fun get(index: Int): Boolean = _word and (1 shl index) != 0

    fun isSet(index: Int): Boolean = get(index)
    fun isClear(index: Int): Boolean = !isSet(index)

    val word: Int get() = _word

    operator fun contains(index: Int): Boolean = get(index)


    val asInt: Int get() = _word

    val size: Int get() = _word.bitCount

    val isEmpty: Boolean get() = _word == 0
    val isNotEmpty: Boolean get() = !isEmpty

    fun filter(f: (BitIndex) -> Boolean): BitSet32 {
        val ret = BitSet32()
        forEach {
            if (f(it)) {
                ret.set(it)
            }
        }
        return ret
    }

    fun any(f: (BitIndex) -> Boolean): Boolean = seq.any(f)

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

    fun copy(): BitSet32 {
        return BitSet32(word)
    }

    val min: Int get() = nextSetBit(0)

    val max: Int get() = previousSetBit(31)

    fun forEach(action: (Int) -> Unit) {
        var i = 0
        while (true) {
            if (i > MAX_INDEX) break
            val next = nextSetBit(i)
            if (next == -1) {
                break
            } else {
                action(next)
                i = next + 1
            }
        }
    }

    val seq: Sequence<Int>
        get() = sequence {
            var i = 0
            while (true) {
                if (i > MAX_INDEX) break
                val next = nextSetBit(i)
                if (next == -1) {
                    break
                } else {
                    yield(next)
                    i = next + 1
                }
            }
        }


    fun toIntList(): List<Int> = seq.toList()
    fun toSortedIntSet(): SortedSet<Int> = seq.toSortedSet()


    //15 16 17
    val ser: String get() = seq.joinToString(" ")
    val sr: String get() = ser


    override fun equals(other: Any?): Boolean = other is BitSet32 && word == other.word

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

    fun anyOverlap(other: BitSet32): Boolean = anyOverlap(this, other)

    fun overlap(other: BitSet32): BitSet32 = overlapBitSet(this, other)


    fun set(index: Int) {
        requireIndex(index)
        _word = _word or (1 shl index)
    }

    fun clear(index: Int) {
        requireIndex(index)
        _word = _word and (1 shl index).inv()
    }


    //same as set
    fun add(index: Int) = set(index)

    //same as clear
    fun remove(index: Int) = clear(index)

    fun removeAll(bitSet: BitSet32) {
        _word = _word and bitSet._word.inv()
    }

    fun minus(that: BitSet32): BitSet32 {
        return minus(this.word, that.word).toBitSet
    }

    //less efficient than removeAll(bitSet: BitSet)
    fun removeAll(intIt: Iterable<Int>) {
        for (index in intIt) {
            remove(index)
        }
    }

    //less efficient than removeAll(bitSet: BitSet)
    fun removeAll(sIndexes: String) {
        val bitSet = bitSet32Of(sIndexes)
        removeAll(bitSet)
    }

    //more efficient than addAll(indexes: Iterable<Int>)
    fun addAll(bitSet: BitSet32) {
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

    fun reset(o: BitSet32) {
        _word = o._word
    }

    fun clearAll() {
        _word = 0;
    }

    companion object {

        const val BITS_PER_WORD: Int = 32
        const val MAX_INDEX: Int = 31
        val INDEX_RANGE: IntRange = 0..MAX_INDEX


        val EMPTY: BitSet32 = BitSet32()

        private const val WORD_MASK = -1

        fun requireIndex(index: Int) {
            if (index !in INDEX_RANGE) {
                throw IndexOutOfBoundsException("Invalid BitSet32 index: $index")
            }
        }

        fun size(word: Int): Int = word.bitCount

        @JvmStatic
        fun minus(word1: Int, word2: Int): Int = word1 and word2.inv()

        @JvmStatic
        fun contains(word1: Int, word2: Int): Boolean = plus(word1, word2) == word1

        @JvmStatic
        fun plus(word1: Int, word2: Int): Int = word1 or word2


        @JvmStatic
        fun overlap(word1: Int, word2: Int): Int = word1 and word2

        @JvmStatic
        fun overlap(s1: BitSet32, s2: BitSet32): Int = overlap(s1.word, s2.word)

        @JvmStatic
        fun overlapBitSet(s1: BitSet32, s2: BitSet32): BitSet32 = overlap(s1, s2).toBitSet


        @JvmStatic
        fun anyOverlap(word1: Int, word2: Int): Boolean = overlap(word1, word2) != 0


        @JvmStatic
        fun anyOverlap(bs1: BitSet32, bs2: BitSet32): Boolean = overlap(bs1.word, bs2.word) != 0


        @JvmStatic
        fun plus(s1: BitSet32, s2: BitSet32): BitSet32 {
            return BitSet32(initWord = s1._word or s2._word)
        }

        @JvmStatic
        fun plus(s1: BitSet32, s2: BitSet32, s3: BitSet32): BitSet32 {
            return BitSet32(initWord = s1._word or s2._word or s3._word)
        }


        fun fromWord(initWord: Int): BitSet32 = BitSet32(initWord = initWord)

        fun fromIntIt(indexes: Iterable<Int>): BitSet32 {
            val s = BitSet32()
            s.addAll(indexes)
            return s
        }

        fun fromBitSet(bitSet: BitSet32): BitSet32 {
            val s = BitSet32()
            s.addAll(bitSet)
            return s
        }
    }

}


val Int.toBitSet: BitSet32 get() = BitSet32(this)

