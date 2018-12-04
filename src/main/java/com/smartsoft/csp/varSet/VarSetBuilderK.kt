package com.smartsoft.csp.varSet

import com.smartsoft.csp.bitSet.BitIndex
import com.smartsoft.csp.bitSet.BitSet64
import com.smartsoft.csp.bitSet.WordEntry
import java.util.*




//val VarSetBuilder._activeEntries: Sequence<WordEntry> get() = entries.filter { BitSet64.isActive(it.value) }

//
//val VarSetBuilder._activeWordArray: LongArray
//    get() {
//        val a = LongArray(_activeWordCount)
//        var i = 0
//        words.forEach {
//            if (it != 0L) {
//                a[i] = it
//                i++
//            }
//        }
//        assert(a.none { it == 0L })
//        return a
//    }


/**
 *
 */

//fun VarSetBuilder._contentEquals(o: VarSetBuilder): Boolean {
//    return Arrays.equals(words, o.words)
//}
//

/*
private void recalculateWordsInUse() {
    // Traverse the bitset until a used word is found
    int i;
    for (i = wordsInUse-1; i >= 0; i--)
    if (words[i] != 0)
        break;

    wordsInUse = i+1; // The new logical size
}
*/








object VarSetBuilderK {


//    @JvmStatic
//    fun wordOverlap(b1: NVarSet, b2: NVarSet): BitSet32 {
//        val maxOverlap: BitSet32 = BitSet32.overlapMutableBitSet(b1.activeWords, b2.activeWords)
//        if (maxOverlap.isEmpty) return BitSet32.EMPTY
//        return maxOverlap.filter {
//            val w1 = b1.words[it]
//            val w2 = b2.words[it]
//            BitSet64.anyOverlap(w1, w2)
//        }
//    }



}



