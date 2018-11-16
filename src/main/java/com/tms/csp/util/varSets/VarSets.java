package com.tms.csp.util.varSets;

import com.tms.csp.util.ints.IntIterator;

import java.util.NoSuchElementException;

public class VarSets {

    public static int getWordIndexForVarId(int varId) {
        int varIndex = varId - 1;
        return varIndex >>> 6;
    }

    public static int getMask(int varId) {
        int varIndex = varId - 1;
        return 1 << varIndex;
    }

    public static long getMaskForLongWord(int varId) {
        int varIndex = varId - 1;
        return 1L << varIndex;
    }

    public static int getMaskForIntWord(int varId) {
        int varIndex = varId - 1;
        return 1 << varIndex;
    }

    public static int getBitIndexForIntWord(int varId) {
        int mask = getMaskForIntWord(varId);
        return Integer.numberOfTrailingZeros(mask);
    }

    public static int getBitIndexForLongWord(int varId) {
        long mask = getMaskForLongWord(varId);
        return Long.numberOfTrailingZeros(mask);
    }

    public static boolean isBitSet(long word, int varId) {
        long mask = getMaskForLongWord(varId);
        return (word & (mask)) != 0;
    }

    public static boolean isBitSet(int word, int varId) {
        int mask = getMaskForIntWord(varId);
        return (word & (mask)) != 0;
    }

    public static IntIterator bitIterator(long word) {
        return new BitIteratorLong(word);
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
        int varId = varIndex + 1;
        return varId;
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


//    public static IntIterator bitIterator(int word) {
//        return new VarNSet.BitIteratorInt(word);
//    }


    public static int minBit(int word) {
        return Integer.numberOfTrailingZeros(word);
    }

    public static int maxBit(int word) {
        return Integer.numberOfLeadingZeros(word);
    }

    public static int minBit(long word) {
        return Long.numberOfTrailingZeros(word);
    }

    public static int maxBit(long word) {
        return 63 - Long.numberOfLeadingZeros(word);
    }

}
