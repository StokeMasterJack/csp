package com.tms.csp.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.List;

import static java.lang.Math.abs;

/**
 * Range(0..3)
 *  01
 *  02
 *  03
 *  12
 *  13
 *  23
 *
 *
 * Range(0..4)
 *  01
 *  02
 *  03
 *  04
 *  12
 *  13
 *  14
 *  23
 *  24
 *  34
 */
public class Range {

    public final int min;
    public final int max;

    public Range(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public Range(int max) {
        this(0, max);
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public <T> Control<T> forEachPair2(IntPairCallback2<T> callback) {
        return forEachPair2(callback, null);
    }

    public <T> Control<T> forEachPair2(IntPairCallback2<T> callback, Control<T> control) {
        if (control == null) {
            control = new Control<T>();
        }
        for (int i = min; i <= max; i++) {
            for (int j = i + 1; j <= max; j++) {
                callback.processPair(i, j, control);
                if (control.isStopped()) {
                    return control;
                }
            }
        }
        return control;
    }

    public void forEachTwoTuple(IntPairCallback callback) {
        for (int i = min; i <= max; i++) {
            for (int j = min; j <= max; j++) {
                try {
                    callback.processPair(i, j);
                } catch (StopIteratingException e) {
                    return;
                }
            }
        }
    }

    public void forEachTwoTuple2(NTupleCallback callback) {
        for (int i = min; i <= max; i++) {
            for (int j = min; j <= max; j++) {
                try {
                    ImmutableList<Integer> tuple = ImmutableList.of(i, j);
                    callback.processTuple(tuple);
                } catch (StopIteratingException e) {
                    return;
                }
            }
        }
    }

    public void forEachNTupleTuple(int n, NTupleCallback callback) {
        if (n == 1) {
            for (Integer i : getAllElementsAsList()) {
                callback.processTuple(ImmutableList.of(i));
            }
        } else if (n == 2) {
            forEachTwoTuple2(callback);
        } else {
            for (int i = min; i <= max; i++) {
                forEachNTupleTuple(n - 1, new NTupleCallback() {
                    @Override
                    public void processTuple(List<Integer> nMinusTuple) {

                    }
                });
            }
        }

    }

    public void forEachTwoSet(IntPairCallback callback) {
        forEachPair(callback);
    }

    public void forEachPair4(IntPairCallback callback) {
        for (int i = min; i <= max; i++) {
            for (int j = i + 1; j <= max; j++) {
                try {
                    callback.processPair(i, j);
                } catch (StopIteratingException e) {
                    return;
                }
            }
        }
    }

    public void forEachPair(IntPairCallback callback) {
        for (int i = min; i <= max; i++) {
            for (int j = i + 1; j <= max; j++) {
                try {
                    callback.processPair(i, j);
                } catch (StopIteratingException e) {
                    return;
                }
            }
        }
    }

//    public void forEachPair(IntPairCallback callback) {
//        for (int i = min; i <= max; i++) {
//            for (int j = i + 1; j <= max; j++) {
//                callback.processPair(i, j);
//            }
//        }
//    }

    public ImmutableSet<Pair> getAllPairs() {
        PairCollectorCallback1 callback = new PairCollectorCallback1();
        forEachPair(callback);
        return callback.getPairs();
    }

    public List<Integer> getAllElementsAsList() {
        ImmutableList.Builder<Integer> a = ImmutableList.builder();
        for (int i = min; i <= max; i++) {
            a.add(i);
        }
        return a.build();
    }

    public ImmutableSet<Integer> getAllElementsAsSet() {
        ImmutableSet.Builder<Integer> a = ImmutableSet.builder();
        for (int i = min; i <= max; i++) {
            a.add(i);
        }
        return a.build();
    }

    public int toZeroBase(int index) {
        return index - min;
    }

    public int size() {
        return (max - min) + 1;
    }

    public long twoSetCount() throws Exception {
        long c = 0;
        for (long i = max; i > min; i--) {
            c += i;
        }
        return c;
    }

    public long nSetCount(int n) {
        ImmutableSet<ImmutableSet<Integer>> nSets = getNSets(n);
        return nSets.size();
    }

    public long nTupleCount(int n) {
        if (n > size()) {
            throw new IllegalArgumentException();
        }
        return n * size();
    }

    public long twoTupleCount() throws Exception {
        return size() * size();
    }

    public void print() {
        System.err.println("size[" + size() + "]");
        for (int n = 1; n <= size(); n++) {
            long nTupleCount = nTupleCount(n);
            System.err.println("n" + n + "TupleCount[" + nTupleCount + "]");
        }

        for (int n = 1; n <= size(); n++) {
            long nSetCount = nSetCount(n);
            System.err.println("n" + n + "SetCount[" + nSetCount + "]");
        }

    }

    public int getElement(int index) {
        return min + index;
    }

    public int indexOf(int element) {
        return element - min;
    }


    public static class PairCollectorCallback2 implements IntPairCallback {

        ImmutableSet.Builder<ImmutableSet<Integer>> b = ImmutableSet.builder();

        @Override
        public void processPair(int i, int j) {
            ImmutableSet<Integer> pair = ImmutableSet.of(i, j);
            b.add(pair);
        }

        public ImmutableSet<ImmutableSet<Integer>> getPairs() {
            return b.build();
        }
    }

    public static class TwoTupleCollectorCallback2 implements IntPairCallback {

        ImmutableSet.Builder<ImmutableList<Integer>> b = ImmutableSet.builder();

        @Override
        public void processPair(int i, int j) {
            ImmutableList<Integer> pair = ImmutableList.of(i, j);
            b.add(pair);
        }

        public ImmutableSet<ImmutableList<Integer>> getTwoTuples() {
            return b.build();
        }
    }

    public static class PairCollectorCallback1 implements IntPairCallback {

        ImmutableSet.Builder<Pair> b = ImmutableSet.builder();

        @Override
        public void processPair(int i, int j) {
            Pair<Integer> pair = new Pair<Integer>(i, j);
            b.add(pair);
        }

        public ImmutableSet<Pair> getPairs() {
            return b.build();
        }
    }

    public ImmutableSet<ImmutableSet<Integer>> getAllOneSets() {
        ImmutableSet.Builder<ImmutableSet<Integer>> b = ImmutableSet.builder();
        for (Integer i : getAllElementsAsSet()) {
            b.add(ImmutableSet.of(i));
        }
        return b.build();
    }

    public ImmutableSet<ImmutableList<Integer>> getAllOneTuples() {
        ImmutableSet.Builder<ImmutableList<Integer>> b = ImmutableSet.builder();
        for (Integer i : getAllElementsAsSet()) {
            b.add(ImmutableList.of(i));
        }
        return b.build();
    }

    public ImmutableSet<ImmutableSet<Integer>> getAllTwoSets() {
        PairCollectorCallback2 pairCollector = new PairCollectorCallback2();
        forEachPair(pairCollector);
        return pairCollector.getPairs();
    }

    public ImmutableSet<ImmutableList<Integer>> getAllTwoTuples() {
        ImmutableSet.Builder<ImmutableList<Integer>> b = ImmutableSet.builder();
        for (Integer i : getAllElementsAsSet()) {

            b.add(ImmutableList.of(i));
        }
        return b.build();
    }

    public ImmutableSet<ImmutableSet<Integer>> getNSets(int n) {
        if (n == 1) {
            return getAllOneSets();
        } else if (n == 2) {
            return getAllTwoSets();
        }

        ImmutableSet<ImmutableSet<Integer>> nMinus1Sets = getNSets(n - 1);
        ImmutableSet.Builder<ImmutableSet<Integer>> bb = ImmutableSet.builder();

        for (ImmutableSet<Integer> nMinus1Set : nMinus1Sets) {
            for (Integer i : getAllElementsAsSet()) {
                ImmutableSet.Builder<Integer> b = ImmutableSet.builder();
                b.addAll(nMinus1Set);
                b.add(i);
                bb.add(b.build());
            }
        }

        return bb.build();

    }


    public static class CountingIntPairCallback implements IntPairCallback {

        private long count;

        @Override
        public void processPair(int i, int j) {
            count++;
        }

        public long getCount() {
            return count;
        }
    }

    private static class DupCheckIntPairCallback implements IntPairCallback {

        private final HashSet<Pair> pairs = new HashSet<Pair>();

        @Override
        public void processPair(int i, int j) {
            Pair<Integer> pair = new Pair<Integer>(i, j);
            boolean added = pairs.add(pair);
            if (!added) {
                throw new IllegalStateException();
            }

        }


    }


    public boolean inAbs(int val) {
        val = abs(val);
        return val >= min && val <= max;
    }

    public boolean in(int val) {
        return val >= min && val <= max;
    }

    @Override
    public String toString() {
        return "Range[" + min + ".." + max + "]";
    }

    public static interface Callback {
        void onValue(int min, int current, int max);
    }

    public void each(Callback cb) {
        for (int i = min; i <= max; i++) {
            cb.onValue(min, i, max);
        }
    }

    public static void dupCheck() {
        Range range = new Range(0, 20);
        DupCheckIntPairCallback callback = new DupCheckIntPairCallback();
        range.forEachPair(callback);
    }


}
