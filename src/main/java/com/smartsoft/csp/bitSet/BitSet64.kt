package com.smartsoft.csp.bitSet

import com.smartsoft.csp.ast.bitCount

typealias WordEntry = IndexedValue<Long>
typealias EntryFilter = (e: WordEntry) -> Boolean

fun entryFilterIdentity(e: WordEntry): Boolean = true

class BitSet64 {

    companion object {

        @JvmStatic
        fun minus(word1: Long, word2: Long): Long = word1 and word2.inv()

        @JvmStatic
        fun minus(word: Long, index: Int): Long = word and (1L shl index).inv()

        @JvmStatic
        fun plus(word1: Long, word2: Long): Long = word1 or word2

        @JvmStatic
        fun plus(word: Long, index: Int): Long = word or (1L shl index)


        @JvmStatic
        fun overlap(word1: Long, word2: Long): Long = word1 and word2


        @JvmStatic
        fun anyOverlap(word1: Long, word2: Long): Boolean = overlap(word1, word2) != 0L


        @JvmStatic
        fun anyOverlap(words1: LongArray, words2: LongArray, index: BitIndex): Boolean {
            val w1 = words1[index]
            val w2 = words2[index]
            return anyOverlap(w1, w2)
        }

        @JvmStatic
        fun anyOverlap(word: Long, index: Int): Boolean = contains(word, index)

        @JvmStatic
        fun contains(word1: Long, word2: Long): Boolean = when {
            word1 == 0L -> false
            word2 == 0L -> true
            else -> plus(word1, word2) == word1
        }

        @JvmStatic
        fun contains(word: Long, index: Int): Boolean = word and (1L shl index) != 0L

        @JvmStatic
        fun contains(word: Long, hasIndex: HasIndex): Boolean = contains(word, hasIndex.index)


//        @JvmStatic
//        fun activeWords(words: LongArray): BitSet32 {
//            val b = bitSet32Of()
//
//            for (i in words.indices) {
//                if (words[i] != 0L) {
//                    b.add(i)
//                }
//            }
//            return b
//        }

        @JvmStatic
        fun size(word: Long): Int = word.bitCount

        @JvmStatic
        fun isActive(word: Long): Boolean = word != 0L;

        @JvmStatic
        fun isEmpty(word: Long): Boolean = word == 0L;


        @JvmStatic
        fun minBit(word: Long): Int {
            return java.lang.Long.numberOfTrailingZeros(word)
        }

        @JvmStatic
        fun maxBit(word: Long): Int {
            return 63 - java.lang.Long.numberOfLeadingZeros(word)
        }

        @JvmStatic
        fun computeMajorIndex(wordIndex: Int, bitIndex: Int): Int {
            return (wordIndex shl 6) + bitIndex
        }
    }


}