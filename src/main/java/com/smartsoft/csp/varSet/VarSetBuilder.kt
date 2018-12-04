package com.smartsoft.csp.varSet

import com.smartsoft.csp.ast.Space
import com.smartsoft.csp.ast.Var
import com.smartsoft.csp.ast.VarId
import com.smartsoft.csp.ast.bitCount
import com.smartsoft.csp.bitSet.BitIndex
import com.smartsoft.csp.bitSet.BitSet32
import com.smartsoft.csp.bitSet.BitSet64
import com.smartsoft.csp.bitSet.WordEntry
import com.smartsoft.csp.parse.VarSpace
import com.smartsoft.csp.util.HasVarId
import com.smartsoft.csp.util.ints.IntIterator
import com.smartsoft.csp.util.ints.Ints
import java.util.*

private val ADDRESS_BITS_PER_WORD = 6
private val BITS_PER_WORD = 1 shl ADDRESS_BITS_PER_WORD
private val BIT_INDEX_MASK = BITS_PER_WORD - 1
private val WORD_MASK = -0x1L


open class VarSetBuilder
@JvmOverloads
constructor(varSpace: VarSpace, val words: LongArray = LongArray(varSpace.wordCount), var awi: BitSet32 = BitSet32()) : VarSet() {

    private var vSpace: VarSpace = varSpace

    constructor(o: VarSetBuilder) : this(o.varSpace, o.copyWords(), o.awi.copy())

    val maxVarIndex: Int get() = varSpace.maxVarIndex

    fun getWord(wordIndex: Int): Long {
        return words[wordIndex]
    }

    fun getWord(vr: Var): Long {
        return getWord(vr.wordIndex)
    }

    fun copyWords(): LongArray {
        return Arrays.copyOf(words, words.size)
    }

    @Throws(NoSuchElementException::class)
    override fun minVrId(): Int {
        val wordIndex = awi.min
        val word = words[wordIndex];
        val bitIndex = BitSet64.minBit(word)
        val varIndex = BitSet64.computeMajorIndex(wordIndex, bitIndex)
        val varId = varIndex + 1;
        return varId
    }

    @Throws(NoSuchElementException::class)
    override fun maxVrId(): Int {
        val wordIndex = awi.max
        val word = words[wordIndex];
        val bitIndex = BitSet64.maxBit(word)
        val varIndex = BitSet64.computeMajorIndex(wordIndex, bitIndex)
        val varId = varIndex + 1;
        return varId
    }


    override fun getVarSpace(): VarSpace = vSpace

    @Throws(NoSuchElementException::class)
    fun minActiveWord(): Long {
        if (awi.isEmpty) throw NoSuchElementException()
        return words[awi.min]
    }

    @Throws(NoSuchElementException::class)
    fun maxActiveWord(): Long {
        if (awi.isEmpty) throw NoSuchElementException()
        return words[awi.max]
    }


    fun awOverlap(o: VarSetBuilder): BitSet32 {
        val awi1 = awi;
        val awi2 = o.awi;
        return awi1.overlap(awi2)
    }


    fun anyAwOverlap(o: VarSetBuilder): Boolean {
        val aw1 = awi;
        val aw2 = o.awi;
        return aw1.anyOverlap(aw2)
    }


    override fun intIterator(): IntIterator {
        return BitSetVarIdIterator(this)
    }

//
//    override fun intIterator(): IntIterator {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }

    override fun containsPrefix(prefix: String): Boolean {

        for (vr in varIt()) {
            if (vr.has(prefix)) {
                return true
            }
        }
        return false
    }


    fun contentEquals(other: VarSetBuilder): Boolean {
        return Arrays.equals(words, other.words)
    }

    override fun equals(other: Any?): Boolean {
        return VarSetK.eqNullSafe(this, other as VarSet)
    }

    override fun isEmpty(): Boolean = awi.isEmpty


    val maxWordIndex: Int get() = wordCount - 1
    val minWordIndex: Int get() = 0

    val wordRange: IntRange get() = minWordIndex..maxWordIndex

    val wordCount: Int get() = words.size

    val wordSeq: Sequence<Long> get() = words.asSequence()
    val wordIndexes: Sequence<Int> get() = wordRange.asSequence()
    val wordEntries: Sequence<WordEntry> get() = words.withIndex().asSequence()


    val activeWordIndexes: Sequence<Int> get() = awi.seq
    val activeWords: Sequence<Long> get() = activeWordIndexes.map { words[it] }
    val activeWordCount: Int get() = awi.size


    fun isWordActive(wordIndex: Int): Boolean {
        return words[wordIndex] != 0L
    }

    fun getPrefixCount(): Int {
        return prefixes.size
    }

    override fun getPrefixes(): Set<String> = this.varIt().map { it.prefix }.toSet()


    fun isWordActive(activeWords: Int, wordIndex: Int): Boolean {
        return isWordIndexBitSet(activeWords.toLong(), wordIndex)
    }


    fun isWordIndexBitSet(word: Long, wordIndex: Int): Boolean {
        val mask = VarSets.getMaskForLongWord(wordIndex)
        return word and mask != 0L
    }

    fun removeActiveWord(activeWords: Int, wordToRemove: Int): Int {
        val mask = getWordIndexMaskForIntWord(wordToRemove)
        return activeWords and mask.inv()
    }


    fun getWordIndexMaskForIntWord(wordIndex: Int): Int {
        return 1 shl wordIndex
    }


    /**
     * Returns the index of the first bit that is set to {@code true}
     * that occurs on or after the specified starting index. If no such
     * bit exists then {@code -1} is returned.
     *
     * <p>To iterate over the {@code true} bits in a {@code BitSet},
     * use the following loop:
     *
     *  <pre> {@code
     * for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i+1)) {
     *     // operate on index i here
     *     if (i == Integer.MAX_VALUE) {
     *         break; // or (i+1) would overflow
     *     }
     * }}</pre>
     *
     * @param  fromIndex the index to start checking from (inclusive)
     * @return the index of the next set bit, or {@code -1} if there
     *         is no such bit
     * @throws IndexOutOfBoundsException if the specified index is negative
     * @since  1.4
     */
    private fun nextSetBit(fromVarIndex: Int): Int {
        if (fromVarIndex < 0) throw IndexOutOfBoundsException("fromIndex < 0: $fromVarIndex")
        var wordIndex = VarSets.getWordIndexForVarIndex(fromVarIndex);
        var word = if (wordIndex !in awi) 0L else words[wordIndex] and (WORD_MASK shl fromVarIndex)
        while (true) {
            if (word != 0L) {
                return wordIndex * BITS_PER_WORD + java.lang.Long.numberOfTrailingZeros(word)
            }
            val wordIndex = awi.nextSetBit(wordIndex)
            word = words[wordIndex]
        }
    }


    fun forEachVar(fromVarIndex: Int = 0, toVarIndex: Int = maxVarIndex, action: (Var) -> Unit) {
        forEachVarId(fromVarIndex, toVarIndex) {
            val vr: Var = space.getVar(it)
            action(vr)
        }
    }

    fun forEachVarId(fromVarIndex: Int = 0, toVarIndex: Int = maxVarIndex, action: (VarId) -> Unit) {
        val range = fromVarIndex..toVarIndex
        var varIndex = nextSetBit(fromVarIndex)
        while (varIndex in range) {
            val varId = varIndex + 1;
            action(varId)
            varIndex = nextSetBit(varIndex + 1)
        }
    }


    /**
     * Maps var->index (position in list, not bitIndex, index with in the trueBits)
     */
    fun indexOf(vr: Var): Int {
        return indexOf(vr.vrId)
    }

    override fun indexOf(vrId: VarId): Int {
        var bitIndexInWords = 0
        for (wordIndex in minWordIndex..maxWordIndex) {
            val word = words[wordIndex]
            val it = VarSets.bitIterator(word)
            while (it.hasNext()) {
                val bitIndexInWord = it.next()
                val bitIndexInWordsXX = (wordIndex shl 6) + bitIndexInWord
                val varId = bitIndexInWordsXX + 1
                if (varId == vrId) {
                    return bitIndexInWords
                }
                bitIndexInWords++
            }
        }
        return -1
    }

    @Throws(IndexOutOfBoundsException::class)
    fun varAt(index: Int): Var {
        return getVarId(index).let { space.getVar(it) }
    }

    /**
     * Maps index->Var (position in list, not bitIndex, index with in the trueBits)
     */
    @Throws(IndexOutOfBoundsException::class)
    override fun getVarId(index: Int): Int {
        var lvi = 0
        for (wordIndex in 0 until wordCount) {
            val word = words[wordIndex]
            val it = VarSets.bitIterator(word)
            while (it.hasNext()) {
                val bitIndex = it.next()
                val varIndex = (wordIndex shl 6) + bitIndex
                val varId = varIndex + 1
                if (lvi == index) {
                    return varId
                }
                lvi++
            }
        }
        throw IndexOutOfBoundsException()
    }


    override fun computeContentHash(): Int {
        var hash = size

        //        hash = Ints.superFastHashIncremental(Arrays.hashCode(words), hash);

        val iter = intIterator()
        while (iter.hasNext()) {
            hash = Ints.superFastHashIncremental(iter.next(), hash)
        }

        return Ints.superFastHashAvalanche(hash)


    }

    override fun clear() {
        awi.forEach { words[it] = 0L }
        awi.clearAll()
    }

    companion object {
        @JvmStatic
        fun overlap(b1: VarSetBuilder, b2: VarSetBuilder): VarSet {
            val varSpace = b1.varSpace
            val awiOverlap = b1.awOverlap(b2)
            if (awiOverlap.isEmpty) {
                return varSpace.mkEmptyVarSet()
            }
            val words = LongArray(b1.wordCount)
            awiOverlap.forEach {
                val w1 = b1.getWord(it)
                val w2 = b2.getWord(it)
                words[it] = w1 and w2
                if (words[it] == 0L) {
                    awiOverlap.remove(it)
                }
//                assert(words[it] != 0L)
            }

            return if (awiOverlap.isEmpty) {
                varSpace.mkEmptyVarSet()
            } else {
                VarSetBuilder(varSpace, words, awi = awiOverlap)
            }

        }


        @JvmStatic
        fun anyOverlap(b1: VarSetBuilder, b2: VarSetBuilder): Boolean {
            return anyOverlap1(b1, b2);
        }


        @JvmStatic
        fun anyOverlap1(b1: VarSetBuilder, b2: VarSetBuilder): Boolean {
            val awOverlap = b1.awOverlap(b2)
            return if (awOverlap.isEmpty) {
                false
            } else awOverlap.any { i ->
                val w1 = b1.words[i]
                val w2 = b2.words[i]
                anyOverlap(w1, w2)
            }
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

        @JvmStatic
        fun setBit(word: Long, bitIndex: Int): Long {
            val mask = VarSets.getMaskForLongWord(bitIndex)
            return word or mask
        }

    }


    override fun recomputeSize(): Boolean {
        return false
    }

    public fun computeSize1(): Int = words.bitCount

    override val size: Int get() = computeSize1()

    fun recomputeActiveWords() {
        println("NVarSet.recomputeActiveWords 1")

        val bs1 = BitSet32()

        var ii = minWordIndex
        val jj = maxWordIndex
        val ws = wordCount

        println("minWordIndex = ${minWordIndex}")
        println("maxWordIndex = ${maxWordIndex}")


        var i = minWordIndex
        while (i <= maxWordIndex) {
            val word = words[i]

            if (word != 0L) {
                bs1.add(i)
            }
            i++
        }

        val bs = BitSet32()
        while (i < this.size) {
            this.awi.reset(bs)
        }

        fun recomputeAll() {
            recomputeActiveWords()
            recomputeSize()
        }


    }

    override fun fix(): VarSet {
        return this;
    }

    override fun getSpace(): Space {
        return varSpace.space
    }


    class BitIteratorLong(private var unseen: Long) : IntIterator {

        override fun hasNext(): Boolean {
            return unseen != 0L
        }

        override fun next(): Int {
            if (!hasNext()) throw NoSuchElementException()
            val mask = java.lang.Long.lowestOneBit(unseen)
            unseen -= mask
            return java.lang.Long.numberOfTrailingZeros(mask)
        }

    }


    //mutations:


    override fun addVarId(varId: Int): Boolean {
        val wordIndex = VarSets.getWordIndexForVarId(varId)
        awi.add(wordIndex)
        val mask = VarSets.getMaskForLongWord(varId)
        val oldValue = words[wordIndex]
        words[wordIndex] = words[wordIndex] or mask
        return oldValue != words[wordIndex]
    }

    fun adjust(action: Adjust = Adjust.None) {
        when (action) {
            is Adjust.AddVar -> addVar(action.vr)
            is Adjust.AddVarSet -> addVarSet(action.vs)
            is Adjust.RemoveVar -> removeVar(action.vr)
            is Adjust.RemoveVarSet -> removeVarSet(action.vs)
            is Adjust.None -> Unit //do nothing
        }
    }


    fun removeVarSetBuilderBitWise(b: VarSetBuilder): Boolean {
        assert(words.size == b.words.size)
        var ch = false
        for (i in 0 until b.wordCount) {
            val newVal = BitSet64.minus(words[i], b.words[i])
            if (words[i] != newVal) {
                ch = true
            }
            words[i] = newVal
            if (words[i] == 0L) {
                awi.clear(i)
            }
        }
        return ch
    }


    fun removeSingletonVarSet(vs: SingletonVarSet): Boolean {
        val v1 = vs.minVrId()
        return removeVarId(v1)
    }


    fun removeVarPair(vs: VarPair): Boolean {
        val v1 = vs.vr1
        val v2 = vs.vr2
        assert(v1.vrId == vs.min)
        assert(v2.vrId == vs.max)
        val ch1 = removeVarId(v1.vrId)
        val ch2 = removeVarId(v2.vrId)
        return ch1 || ch2
    }

    override fun removeVarId(varId: Int): Boolean {
        val vr = space.getVar(varId)
        val wordIndex = VarSets.getWordIndexForVar(vr)
        val mask = VarSets.getMaskForLongWord(vr)
        val before = words[wordIndex]
        words[wordIndex] = words[wordIndex] and mask.inv()
        val ch = before != words[wordIndex]
        if (ch && words[wordIndex] == 0L) {
            awi.clear(wordIndex)
        }
        return ch
    }

    fun removeVarSet(vs: VarSet): Boolean {
        return if (vs is EmptyVarSet) {
            false
        } else if (vs is SingletonVarSet) {
            removeSingletonVarSet(vs)
        } else if (vs is VarPair) {
            removeVarPair(vs)
        } else if (vs is VarSetBuilder) {
            removeVarSetBuilderBitWise(vs)
        } else {
            throw IllegalStateException(vs.javaClass.toString() + "")
        }

    }


    fun addNVarSet(b2: VarSetBuilder): Boolean {
        var anyChange = false
        b2.awi.forEach {
            val oldWord = words[it];
            words[it] = words[it] or b2.words[it]
            if (oldWord != words[it]) {
                anyChange = true
                awi.add(it)
            }
        }
        return anyChange
    }

    fun build(): VarSet {
//        recomputeAll()
        val ss = computeSize1()
        return when (ss) {
            0 -> varSpace.mkEmptyVarSet()
            1 -> varSpace.mkSingleton(minVrId())
            2 -> {
                val v1 = minVrId()
                val v2 = maxVrId()
                varSpace.mkVarPair(v1, v2)
            }
            else -> {
                this
            }
        }
    }


    override fun immutable(): VarSet {
        return build();
    }


    fun removeVarIt(b: Iterable<HasVarId>) {
        for (vr in b) {
            val varId = vr.varId
            removeVarId(varId)
        }
    }


    override fun addVar(vr: Var): Boolean {
        return addVarId(vr.vrId)
    }

    fun addVarCodes(varCodes: Array<String>) {
        for (varCode in varCodes) {
            val vr = varSpace.getVar(varCode)
            addVar(vr)
        }
    }

    fun addVarCodes1(varCodes: Iterable<String>) {
        for (varCode in varCodes) {
            val vr = varSpace.getVar(varCode)
            addVar(vr)
        }
    }

    fun addVar(varCode: String): Boolean {
        val v = space.getVar(varCode)
        return addVar(v)
    }

    fun add(`var`: HasVarId): Boolean {
        return addVarId(`var`.varId)
    }


    fun addVarSet(sVarCodes: String): Boolean {
        val vs = space.mkVarSet(sVarCodes)
        return addVarSet(vs)
    }


    /**
     * Less efficient bitwise
     */
    fun addVars(vars: Array<HasVarId>) {
        System.err.println("Warning: prefer addVarSet")
        for (vr in vars) {
            add(vr)
        }
    }

    /**
     * Less efficient bitwise
     */
    fun addVars(vars: Iterable<HasVarId>): Boolean {
        System.err.println("Warning: prefer addVarSet")
        var anyChange = false
        for (vr in vars) {
            val ch = add(vr)
            if (ch) anyChange = true
        }
        return anyChange
    }

//    public boolean addVars(VarSet... varSets) {
//        boolean anyChange = false;
//        for (VarSet varSet : varSets) {
//            boolean ch = addVars(varSet);
//            if (ch) {
//                anyChange = true;
//            }
//        }
//
//        return anyChange;
//    }

    fun addVars(vars: Collection<Var>?): Boolean {
        System.err.println("Warning: prefer addVarSet")
        if (vars == null) return false
        if (vars is VarSet) {
            val vs = vars as VarSet?
            return this.addVarSet(vs!!)
        } else {
            var anyChange = false
            for (vr in vars) {
                val ch = addVarId(vr.vrId)
                if (ch) {
                    anyChange = true
                }
            }
            return anyChange
        }
    }


//    public boolean adjust(VarSet vs, boolean add) {
//        return add ? addVarSet(vs) : removeVarSet(vs);
//    }

    override fun copy(): VarSetBuilder {
        return VarSetBuilder(this)
    }

//    override fun copyVarSetBuilder(): VarSetBuilder {
//        return VarSetK.copy(this)
//    }

    /**
     * Less efficient bitwise
     */
    override fun addVarSet(vs: VarSet): Boolean {
        when {
            vs.isEmpty() -> return false
            vs.isSingleton -> {
                val v1 = vs.minVrId()
                return addVarId(v1)
            }
            vs.isVarPair -> {
                val v1 = vs.minVrId()
                val v2 = vs.maxVrId()
                val ch1 = addVarId(v1)
                val ch2 = addVarId(v2)
                return ch1 || ch2
            }
            vs is VarSetBuilder -> return addNVarSet(vs as VarSetBuilder)
            vs.isVarNSet -> {
                if (true) throw IllegalStateException()
                return false
            }
            else -> throw IllegalStateException()
        }

    }

//    override fun addAll(vars: Collection<Var>): Boolean {
//        return addVars(vars)
//    }
//
//    override fun add(vr: Var): Boolean {
//        return addVar(vr)
//    }


    fun assertAwi(ser: String) {
        if (ser != awi.sr) {
            System.err.println("Expected: $ser")
            System.err.println("Actual: ${awi.sr}")
            throw AssertionError("Expected: ${ser}   actual: ${awi.sr}")
        }
    }


}