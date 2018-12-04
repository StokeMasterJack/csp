package com.smartsoft.csp.varSet;

import com.smartsoft.csp.ast.Var;
import com.smartsoft.csp.util.ints.IntIterator;

import java.util.NoSuchElementException;

public class VarSets {

    public static int getWordIndexForVarId(int varId) {
        int varIndex = varId - 1;
        return varIndex >>> 6;
    }

    public static int getWordIndexForVarIndex(int varIndex) {
        return varIndex >>> 6;
    }

    public static int getWordIndexForVar(Var vr) {
        int varIndex = vr.getVarIndex();
        return varIndex >>> 6;
    }

    public static int getMask(int varId) {
        int varIndex = varId - 1;
        return getMaskForIntWord(varId);
    }

    public static long getMaskForLongWord(int varId) {
        int varIndex = varId - 1;
        return 1L << varIndex;
    }

    public static int getMaskForIntWord(int varId) {
        int varIndex = varId - 1;
        return 1 << varIndex;
    }

    public static long getMaskForLongWord(Var vr) {
        int varIndex = vr.getVarIndex();
        return 1L << varIndex;
    }

//    public static int getMaskForIntWord(int varId) {
//        int varIndex = varId - 1;
//        return 1 << varIndex;
//    }

//    public static int getBitIndexForIntWord(int varId) {
//        int mask = getMaskForIntWord(varId);
//        return Integer.numberOfTrailingZeros(mask);
//    }

    public static int getBitIndexForLongWord(int varId) {
        long mask = getMaskForLongWord(varId);
        return Long.numberOfTrailingZeros(mask);
    }

    public static int getBitIndexForLongWord(Var vr) {
        long mask = getMaskForLongWord(vr);
        return Long.numberOfTrailingZeros(mask);
    }

    public static boolean isBitSet(long word, int varId) {
        long mask = getMaskForLongWord(varId);
        return (word & (mask)) != 0L;
    }

    public static boolean isBitSet(long word, Var vr) {
        long mask = getMaskForLongWord(vr);
        return (word & (mask)) != 0L;
    }

    public static long foo(long word1, long word2) {
        long word3 = word1 & ~word2;
        return word3;
    }

    public static boolean isBitSet(int word, int varId) {
        int mask = getMaskForIntWord(varId);
        return (word & (mask)) != 0;
    }

    public static IntIterator bitIterator(long word) {
        return new VarSetBuilder.BitIteratorLong(word);
    }


    public static class BitIteratorInt implements IntIterator {

        private int unseen;

        public BitIteratorInt(int word) {
            unseen = word;
        }

        public boolean hasNext() {
            return (unseen != 0);
        }

        public int next() {
            if (!hasNext()) throw new NoSuchElementException();
            final int mask = Integer.lowestOneBit(unseen);
            unseen -= mask;
            return Integer.numberOfTrailingZeros(mask);
        }

    }

    public static int computeVarIdBad(int wordIndex, int bitIndex) {
        return wordIndex * 64 + bitIndex - 1;
    }

    public static int computeVarId(int wordIndex, int bitIndex) {
        int varIndex = (wordIndex << 6) + bitIndex;
        return varIndex + 1;
    }




    public int clearBit(int word, int varId) {
        int mask = getMaskForIntWord(varId);
        return word & ~mask;
    }

    //1L << varIndex
    public long clearBit(long word, Var vr) {
        long mask = getMaskForLongWord(vr);
        return word & ~(mask);
    }

    public long clearBit(long word, int bitIndex) {
        return word & ~(1L << bitIndex);
    }

    public long clearBit(long[] words, int wordIndex, Var vr) {
        long mask = getMaskForLongWord(vr);
        return words[wordIndex] &= ~(mask);
    }




//    public static IntIterator bitIterator(int word) {
//        return new VarNSet.BitIteratorInt(word);
//    }


}
