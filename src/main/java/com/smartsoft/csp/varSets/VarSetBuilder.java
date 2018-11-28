package com.smartsoft.csp.varSets;

import com.google.common.collect.ImmutableSet;
import com.smartsoft.csp.ast.Ser;
import com.smartsoft.csp.ast.Space;
import com.smartsoft.csp.ast.Var;
import com.smartsoft.csp.parse.VarSpace;
import com.smartsoft.csp.util.HasVarId;
import com.smartsoft.csp.util.ints.IntIterator;
import com.smartsoft.csp.util.ints.Ints;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.*;

import static com.smartsoft.csp.ast.VarK.computeSize;

public class VarSetBuilder extends VarSet {

    public VarSpace vSpace;

    public long[] words;
    public int _size = -1;  // -1 means dirty

    /**
     * Empty VarSetBuilder
     */
    public VarSetBuilder(VarSpace varMap) {
        this.vSpace = varMap;
        this.words = new long[varMap.getWordCount()];
        _size = 0;
    }

//    public VarSetBuilder(VarSpace varMap, long[] words) {
//        assert varMap.getWordCount() == words.length;
//        this.vSpace = varMap;
//        this.words = new long[varMap.getWordCount()];
//    }

    public VarSpace getVarSpace() {
        return vSpace;
    }

    public VarSetBuilder(VarSetBuilder that) {
        this.vSpace = that.getVarSpace();
        this.words = copyWordArrayExactly(that.words);
        this._size = that._size;
    }

    public long[] copyAndCompressWords() {
        return copyAndCompress(this.words);
    }

    public static long[] copyArray(long[] words, boolean compress) {

        if (compress) {
            int activeWordCount = computeActiveWordCount(words);
            if (activeWordCount < words.length) {
                long[] copy = new long[activeWordCount];
                int ii = 0;
                for (int i = 0; i < words.length; i++) {
                    if (words[i] != 0) {
                        copy[ii] = words[i];
                        ii++;
                    }
                }
                return copy;
            }
        }

        long[] copy = new long[words.length];
        System.arraycopy(words, 0, copy, 0, words.length);
        return copy;
    }

    public static long[] copyWordArrayExactly(long[] words) {
        assert words != null;
        long[] copy = new long[words.length];
        System.arraycopy(words, 0, copy, 0, words.length);
        return copy;
    }

    public static long[] copyButShrinkWordArray(long[] words, int activeWords) {
        int newActiveWordCount = Integer.bitCount(activeWords);
        long[] copy = new long[newActiveWordCount];
        int ii = 0;
        for (int i = 0; i < words.length; i++) {
            if (isWordActive(activeWords, i)) {
                copy[ii] = words[i];
                ii++;
            }
        }
        return copy;
    }

    @Override
    public VarSetBuilder asVarSetBuilder() {
        return this;
    }


    public int getWordCount() {
        return words.length;
    }

    public int getActiveWordCount() {
        return computeActiveWordCount();
    }


    public int size() {
        maybeRecomputeSize();
        return _size;
    }

    public boolean recomputeSize() {
        int s1 = _size;
        calculateSize();
        int s2 = _size;
        return (s1 != s2);
    }


    public boolean maybeRecomputeSize() {
        if (isDirty()) {
            return recomputeSize();
        }
        return false;
    }


    public static int computeActiveWordsBitSet(long[] words) {
        int activeWords = 0;
        for (int i = 0; i < words.length; i++) {
            if (words[i] != 0) {
                activeWords |= (getMask(i));
            }
        }
        return activeWords;
    }


    public int computeActiveWordCount() {
        return computeActiveWordCount(words);
    }


    public long getWord(int wordIndex) {
        return words[wordIndex];
    }

    public static long[] computeActiveWordArray(long[] completeWordArray, int activeWordCount) {
        long[] activeWordArray = new long[activeWordCount];
        int ii = 0;
        for (int i = 0; i < completeWordArray.length; i++) {
            if (completeWordArray[i] != 0) {
                activeWordArray[ii] = completeWordArray[i];
                ii++;
            }
        }
        return activeWordArray;
    }

    public boolean addVar(Var var) {
        int varId = var.getVarId();
        return addVarId(varId);
    }

    public void addVarCodes(String[] varCodes) {
        for (String varCode : varCodes) {
            Var var = vSpace.getVar(varCode);
            addVar(var);
        }
    }

    public void addVarCodes1(Iterable<String> varCodes) {
        for (String varCode : varCodes) {
            Var var = vSpace.getVar(varCode);
            addVar(var);
        }
    }

    public boolean addVar(String varCode) {
        Var v = getSpace().getVar(varCode);
        return addVar(v);
    }

    public boolean add(HasVarId var) {
        return addVarId(var.getVarId());
    }

    public int getPrefixCount() {
        return getPrefixes().size();
    }

    public Set<String> getPrefixes() {
        ImmutableSet.Builder<String> b = ImmutableSet.builder();
        for (Var var : this) {
            b.add(var.getPrefix());
        }
        return b.build();
    }

    public boolean addVarSet(@NotNull String sVarCodes) {
        VarSet vs = getSpace().mkVarSet(sVarCodes);
        return addVarSet(vs);
    }

    public static class EmptyIntIterator implements IntIterator {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public int next() {
            throw new UnsupportedOperationException();
        }
    }

    public static interface LongIterator {
        boolean hasNext();

        long next();
    }

    public static class ArrayLongIterator {

        private final long[] a;
        private int i = -1;

        public ArrayLongIterator(long[] a) {
            this.a = a;
        }

        public boolean hasNext() {
            return i < a.length;
        }

        public long next() {
            i++;
            return a[i];
        }
    }


    public static int setBit(int word, int bitIndex) {
        int mask = getMaskForIntWord(bitIndex);
        return word | mask;
    }

    public boolean isWordActive(int wordIndex) {
        return words[wordIndex] != 0;
    }

    public boolean containsVarId(int varId) {
        final int varIndex = varId - 1;
        final int wordIndex = varIndex >>> 6;
        final long mask = 1L << varIndex;
        return (words[wordIndex] & mask) != 0;
    }

    public boolean isDirty() {
        return _size == -1;
    }

    public void makeDirty() {
        _size = -1;
    }

    private boolean maybeDirty(long before, long after) {
        boolean ch = before != after;
        if (ch) {
            makeDirty();
        }
        return ch;
    }

//    public void addVarIdQuiet(int varId) {
//        int wordIndex = getWordIndexForVarId(varId);
//        long mask = getMaskForLongWord(varId);
//        words[wordIndex] |= mask;
//    }

//    public boolean addVarIds(Iterable<Integer> varIds) {
//        for (Integer varId : varIds) {
//            addVarIdQuiet(varId);
//        }
//        return recalculateSize();
//    }

    public boolean addVarId(int varId) {
        int wordIndex = getWordIndexForVarId(varId);
        long mask = getMaskForLongWord(varId);
        long oldValue = words[wordIndex];
        words[wordIndex] |= mask;
        return maybeDirty(oldValue, words[wordIndex]);
    }

//    public void addLits(Iterable<? extends Exp> lits) {
//        for (Exp lit : lits) {
//            int varId = lit.getVarId();
//            addVarIdQuiet(varId);
//        }
//    }

    public void addVars(HasVarId[] vars) {
        for (HasVarId var : vars) {
            add(var);
        }
    }

    public boolean addVars(Iterable<? extends HasVarId> vars) {
        boolean anyChange = false;
        for (HasVarId var : vars) {
            boolean ch = add(var);
            if (ch) anyChange = true;
        }
        return anyChange;
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

    public boolean addVars(Collection<Var> vars) {
        if (vars == null) return false;
        if (vars instanceof VarSet) {
            VarSet vs = (VarSet) vars;
            return this.addVarSet(vs);
        } else {
            boolean anyChange = false;
            for (HasVarId var : vars) {
                boolean ch = addVarId(var.getVarId());
                if (ch) {
                    anyChange = true;
                }
            }
            return anyChange;
        }
    }


    public boolean addVarSet(VarSet vs) {
        if (vs == null) {
            return false;
        } else if (vs.isEmpty()) {
            return false;
        } else if (vs.isSingleton()) {
            int v1 = vs.min();
            return addVarId(v1);
        } else if (vs.isVarPair()) {
            int v1 = vs.min();
            int v2 = vs.max();
            boolean ch1 = addVarId(v1);
            boolean ch2 = addVarId(v2);
            return ch1 || ch2;
        } else if (vs.isVarSetBuilder()) {
            VarSetBuilder s = vs.asVarSetBuilder();
            boolean anyChange = false;
            for (int wordIndex = 0; wordIndex < words.length; wordIndex++) {
                long oldWord = words[wordIndex];
                words[wordIndex] |= s.words[wordIndex];
                if (oldWord != words[wordIndex]) {
                    anyChange = true;
                }
            }
            if (anyChange) {
                makeDirty();
            }
            return anyChange;
        } else if (vs.isVarNSet()) {
            if (true) throw new IllegalStateException();
            return false;
        } else {
            throw new IllegalStateException();
        }

    }

    public boolean addAll(Collection<? extends Var> vars) {
        return addVars(vars);
    }

    @Override
    public boolean add(Var var) {
        return addVar(var);
    }


    public boolean removeVarIdDead(int varId) {
        removeVarId(varId);
        return recalculateSize();
    }

    public boolean removeVarId(int varId) {
        int wordIndex = getWordIndexForVarId(varId);
        long mask = getMaskForLongWord(varId);
        long before = words[wordIndex];
        words[wordIndex] &= ~(mask);
        long after = words[wordIndex];
        return maybeDirty(before, after);
    }


//    public boolean removeVarSetBuilderBruteForce(VarSetBuilder b) {
//        boolean anyChange = false;
//        for (HasVarId var : b) {
//            int varId = var.getVarId();
//            boolean ch = removeVarId(varId);
//            if (ch) {
//                anyChange = true;
//            }
//        }
//        return anyChange;
//    }

    public void removeVarIt(Iterable<? extends HasVarId> b) {
        for (HasVarId var : b) {
            int varId = var.getVarId();
            removeVarId(varId);
        }
    }

    public void removeVarSetBuilderBruteForce(VarSetBuilder b) {
        removeVarIt(b);
    }

    public boolean removeVarSetBuilderBitWise(VarSetBuilder b) {
        assert words.length == b.words.length;
        boolean ch = false;
        for (int i = 0; i < b.getWordCount(); i++) {
            long newVal = VarSetK.minus(words[i], b.words[i]);
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

    public boolean removeSingletonVarSet(@Nonnull SingletonVarSet vs) {
        int v1 = vs.min();
        return removeVarId(v1);
    }

    public boolean removeVarPair(@Nonnull VarPair vs) {
        int v1 = vs.min();
        int v2 = vs.max();
        boolean ch1 = removeVarId(v1);
        boolean ch2 = removeVarId(v2);
        return ch1 || ch2;
    }

    public boolean removeVarSet(@Nonnull VarSet vs) {
        if (vs instanceof EmptyVarSet) {
            return false;
        } else if (vs instanceof SingletonVarSet) {
            return removeSingletonVarSet((SingletonVarSet) vs);
        } else if (vs instanceof VarPair) {
            return removeVarPair((VarPair) vs);
        } else if (vs instanceof VarSetBuilder) {
            return removeVarSetBuilderBitWise((VarSetBuilder) vs);
        } else {
            throw new IllegalStateException(vs.getClass() + "");
        }
    }


    public int clearBit(int word, int varId) {
        int mask = getMaskForIntWord(varId);
        return word & ~mask;
    }

    public long clearBit(long word, int bitIndex) {
        return word & ~(1L << bitIndex);
    }

    public long clearBit(long[] words, int wordIndex, int varId) {
        int mask = getMaskForIntWord(varId);
        return words[wordIndex] &= ~(mask);
    }


    public boolean recalculateSize() {
        int oldSize = _size;
        calculateSize();
        return oldSize != _size;
    }

    public void calculateSize() {
        this._size = computeSize(words);
    }

    public void clear() {
        Arrays.fill(words, 0);
        _size = 0;
    }

    public boolean isEmpty() {
        return _size == 0;
    }

    public Space getSpace() {
        return vSpace.getSpace();
    }

    public int getVarId(int index) throws IndexOutOfBoundsException {
        int lvi = 0;
        for (int wordIndex = 0; wordIndex < this.getWordCount(); wordIndex++) {
            IntIterator it = bitIterator(words[wordIndex]);
            while (it.hasNext()) {
                int bitIndex = it.next();
                int varIndex = (wordIndex << 6) + bitIndex;
                int varId = varIndex + 1;
                if (lvi == index) {
                    return varId;
                }
                lvi++;
            }
        }
        throw new IndexOutOfBoundsException();
    }


    public int minActiveWord() {
        for (int wordIndex = 0; wordIndex < this.getWordCount(); wordIndex++) {
            if (isWordActive(wordIndex)) {
                return wordIndex;
            }
        }
        throw new NoSuchElementException();
    }

    public int maxActiveWord() {
        for (int wordIndex = this.getWordCount() - 1; wordIndex >= 0; wordIndex--) {
            if (isWordActive(wordIndex)) {
                return wordIndex;
            }
        }
        throw new NoSuchElementException();
    }

    public int min() throws NoSuchElementException {
        int minActiveWord = minActiveWord();
        long minWord = words[minActiveWord];
        int minBit = minBit(minWord);
        return computeVarId(minActiveWord, minBit);
    }

    public int max() throws NoSuchElementException {
        int maxActiveWord = maxActiveWord();
        long maxWord = words[maxActiveWord];
        int maxBit = maxBit(maxWord);
        return computeVarId(maxActiveWord, maxBit);
        //return (maxWordIndex << 6) + 63 - Long.numberOfLeadingZeros(elements[maxWordIndex]);
    }


    public VarSet immutable() {
        return build();
    }

    @Override
    public boolean isVarSetBuilder() {
        return true;
    }

    @Override
    public int computeContentHash() {
        int hash = size();

//        hash = Ints.superFastHashIncremental(Arrays.hashCode(words), hash);

        for (IntIterator iter = intIterator(); iter.hasNext(); ) {
            hash = Ints.superFastHashIncremental(iter.next(), hash);
        }

        return Ints.superFastHashAvalanche(hash);


    }

    public VarSet build() {
        recalculateSize();

        if (_size == 0) {
            return vSpace.mkEmptyVarSet();
        } else if (_size == 1) {
            return vSpace.mkSingleton(min());
        } else if (_size == 2) {
            int v1 = min();
            int v2 = max();
            return vSpace.mkVarPair(v1, v2);
        } else {

//            long[] words = copyAndCompressWords();
//            int ss = computeVarCount(words);
//            assert size == ss;
//            return varSpace.mkVarNSetFixed(words, size);

            return this;
        }
    }


    public static class BitIteratorLong implements IntIterator {

        private long unseen;

        public BitIteratorLong(long word) {
            unseen = word;
        }

        public boolean hasNext() {
            return (unseen != 0);
        }

        public int next() {
            if (!hasNext()) throw new NoSuchElementException();
            final long mask = Long.lowestOneBit(unseen);
            unseen -= mask;
            return Long.numberOfTrailingZeros(mask);
        }

    }


    @Override
    public boolean containsAllBitSet(VarSetBuilder that) {
//        System.err.println("VarSetBuilder.containsAllBitSet");
        //todo use bitwise ops
        for (Var var : that) {
            int thatVarId = var.getVarId();
            if (!this.containsVarId(thatVarId)) {
                return false;
            }
        }

        return true;
    }

    public static class VarSetBuilderIntIterator extends BitSetIntIterator<VarSetBuilder> {

        public VarSetBuilderIntIterator(VarSetBuilder varSet) {
            super(varSet);
            moveToNextStableState();
        }

    }


//    @Override
//    public boolean anyIntersection(VarSetBuilder that) {
//        if (that == null || that.isEmpty()) return false;
//        assert this.words.length == varSpace.getMaxWordCount();
//        for (int wordIndex = 0; wordIndex < words.length; wordIndex++) {
//            if ((words[wordIndex] & that.words[wordIndex]) != 0) {
//                return true;
//            }
//        }
//        return false;
//    }

    public static boolean anyIntersection(VarSetBuilder vs1, VarSetBuilder vs2) {
        if (vs1 == null || vs2 == null) return false;

        Space space = vs1.getSpace();
        VarSpace vSpace = space.getVarSpace();

        assert vs1.words.length == vSpace.getWordCount();
        assert vs2.words.length == vSpace.getWordCount();

        for (int wordIndex = 0; wordIndex < vs1.words.length; wordIndex++) {
            if ((vs1.words[wordIndex] & vs2.words[wordIndex]) != 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public VarSet minus(int varIdToRemove) {
        VarSetBuilder copy = copy();
        copy.removeVarId(varIdToRemove);
        return copy;
    }

    @Override
    public VarSet plus(VarSet that) {
        VarSetBuilder b = copy();
        b.addVarSet(that);
        return b;
    }

    @Override
    public VarSet plus(Var var) {
        VarSetBuilder copy = copy();
        copy.addVar(var);
        return copy;
    }

    @Override
    public VarSet minus(VarSet varsToRemove) {
        VarSetBuilder copy = copy();
        copy.removeVarSet(varsToRemove);
        return copy.build();
    }

    public VarSetBuilder copy() {
        return new VarSetBuilder(this);
    }

//    public String toString() {
//        Ser a = new Ser();
//        serialize(a);
//        return a.toString();
//    }


    public int indexOf(final int qVarId) {
        int bitIndexInWords = 0;
        for (int wordIndex = 0; wordIndex < this.getWordCount(); wordIndex++) {
            IntIterator it = bitIterator(words[wordIndex]);
            while (it.hasNext()) {
                int bitIndexInWord = it.next();
                int bitIndexInWordsXX = (wordIndex << 6) + bitIndexInWord;
                int varId = bitIndexInWordsXX + 1;
                if (varId == qVarId) {
                    return bitIndexInWords;
                }
                bitIndexInWords++;
            }
        }
        return -1;
    }


    public static long[] copyAndCompress(long[] wordsIn) {
        int activeWordCount = computeActiveWordCount(wordsIn);

        long[] copy;
        if (activeWordCount < wordsIn.length) {
            copy = new long[activeWordCount];
            int ii = 0;
            for (int i = 0; i < wordsIn.length; i++) {
                if (wordsIn[i] != 0) {
                    copy[ii] = wordsIn[i];
                    ii++;
                }
            }
        } else {
            int L = wordsIn.length;
            copy = new long[L];
            System.arraycopy(wordsIn, 0, copy, 0, L);
        }

        return copy;
    }

    @Override
    public void serialize(Ser a) {
        Iterator<Var> it = varIter();
        while (it.hasNext()) {
            Var next = it.next();
            a.append(next.getVarCode());
            if (it.hasNext()) {
                a.argSep();
            }
        }
    }

    public IntIterator intIterator() {
        return new VarSetBuilderIntIterator(this);
    }


    @Override
    public boolean containsPrefix(String prefix) {
        for (Var var : varIt()) {
            if (var.is(prefix)) {
                return true;
            }
        }
        return false;
    }

    public static class BitSetIntIterator<T extends VarSetBuilder> implements IntIterator {

        protected int wordIndex = -1;
        protected long unseen = 0;
        protected int bitIndex;
        protected int varId;
        protected long mask;

        protected T varSet;

        public BitSetIntIterator(T varSet) {
            this.varSet = varSet;
        }

        public int maxWordCount() {
            return varSet.getWordCount();
        }


        private int computeVarId() {
            return VarSets.computeVarId(wordIndex, bitIndex);
        }


        public void moveToNextStableState() {

            //stable: unseen != 0
            // dead:  wordIndex >= maxWordCount
            while (true) {
                if (unseen == 0) {
                    wordIndex++;
                    if (wordIndex >= maxWordCount()) {
                        return;
                    }
                    maybeIncrementWord();
                } else {
                    break;
                }

            }


            mask = Long.lowestOneBit(unseen);
            bitIndex = Long.numberOfTrailingZeros(mask);
            varId = computeVarId();


        }

        public void maybeIncrementWord() {
            unseen = varSet.words[wordIndex];
        }


        public boolean isStableLive() {
            return unseen != 0;
        }

        public boolean isStableDead() {
            return wordIndex >= maxWordCount();
        }

        public boolean isStable() {
            return isStableLive() || isStableDead();
        }

        public boolean hasNext() {
            return isStableLive();
        }

        public int next() {
            assert isStableLive();
            int retVal = varId;
            takeNext();
            return retVal;
        }

        public void takeNext() {
            unseen -= mask;
            moveToNextStableState();
        }


    }


    public static int computeActiveWordCount(long[] words) {
        int c = 0;
        for (int i = 0; i < words.length; i++) {
            if (words[i] != 0) {
                c++;
            }
        }
        return c;
    }


    public int getLocalWordIndexForVarId(int activeWords, int varId) {
        int wordIndex = getWordIndexForVarId(varId);
        return getLocalWordIndex(wordIndex, activeWords);
    }

    public int getLocalWordIndex(int globalWordIndex, int activeWords) {
        if (!isWordActive(activeWords, globalWordIndex)) {
            return -1;
        }
        int lwi = 0;
        for (int wordIndex = 0; wordIndex < this.getWordCount(); wordIndex++) {
            if (isWordActive(activeWords, wordIndex)) {
                if (wordIndex == globalWordIndex) {
                    return lwi;
                }
                lwi++;
            }
        }
        throw new IllegalStateException();
    }

    public static boolean isWordActive(int activeWords, int wordIndex) {
        return isWordIndexBitSet(activeWords, wordIndex);
    }


    public static boolean isWordIndexBitSet(int word, int wordIndex) {
        int mask = getWordIndexMaskForIntWord(wordIndex);
        return (word & (mask)) != 0;
    }

    public static int removeActiveWord(int activeWords, int wordToRemove) {
        int mask = getWordIndexMaskForIntWord(wordToRemove);
        return activeWords & ~mask;
    }


    public static int getWordIndexMaskForIntWord(int wordIndex) {
        return 1 << wordIndex;
    }

}
