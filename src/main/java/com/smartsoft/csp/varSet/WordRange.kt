package com.smartsoft.csp.varSet

import com.smartsoft.csp.bitSet.BitSet32
import com.smartsoft.csp.parse.VarSpace

class WordRange(var min: Int = -1, var max: Int = -1) {

    private val ss = BitSet32()

    init {
        assert(min >= -1)
        assert(max >= -1)
    }

    fun recomputeMin(words: LongArray, varSpace: VarSpace) {
        var i = 0
        while (i <= varSpace.maxWordIndex) {
            if (words[i] != 0L) {
                min = i
                return
            }
            i++
        }
        this.min = -1;
    }


    fun recomputeMax(words: LongArray, varSpace: VarSpace) {
        var i = varSpace.maxWordIndex
        while (i >= 0) {
            if (words[i] != 0L) {
                max = i
                return
            }
            i--
        }
        this.max = -1
    }

    val size: Int get() = max - min + 1

    val isEmpty: Boolean get() = min == -1 && max == -1


}